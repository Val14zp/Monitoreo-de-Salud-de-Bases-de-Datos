import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MonitoringService {
    private static final Logger LOGGER = Logger.getLogger(MonitoringService.class.getName());
    private ConnectionManager connectionManager;

    public MonitoringService(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    // Método para monitorear uso de CPU
    public CpuUsage collectCpuUsage() {
        String query = """
        SELECT\s
            (SELECT VALUE FROM V$SYSSTAT WHERE NAME = 'CPU used by this session') /\s
            (SELECT VALUE FROM V$SYSSTAT WHERE NAME = 'DB time') * 100 AS cpu_utilizado_por_base_datos,
            (SELECT VALUE FROM V$OSSTAT WHERE STAT_NAME = 'NUM_CPUS') AS cpu_total,
            CASE\s
                WHEN ((SELECT VALUE FROM V$SYSSTAT WHERE NAME = 'CPU used by this session') /\s
                      (SELECT VALUE FROM V$SYSSTAT WHERE NAME = 'DB time') * 100) > 50 THEN 'ALERTA: CPU > 50%'
                ELSE 'CPU en rango normal'
            END AS estado_cpu
        FROM DUAL
    """;

        try (Connection connection = connectionManager.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            if (resultSet.next()) {
                double cpuUsed = resultSet.getDouble("cpu_utilizado_por_base_datos");
                int cpuTotal = resultSet.getInt("cpu_total");
                String status = resultSet.getString("estado_cpu");
                return new CpuUsage(cpuUsed, cpuTotal, status);
            } else {
                LOGGER.log(Level.WARNING, "No se encontraron resultados para uso de CPU");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al obtener uso de CPU", e);
        }
        return new CpuUsage(0, 0, "No se pudo obtener el estado de la CPU");
    }


    // Método para monitorear uso de RAM
    public RamUsage collectRamUsage() {
        String query = """
            SELECT
                (SELECT SUM(VALUE) FROM V$SGA) / (1024 * 1024 * 1024) AS ram_utilizada_db_gb,
                (SELECT VALUE FROM V$OSSTAT WHERE STAT_NAME = 'PHYSICAL_MEMORY_BYTES') / (1024 * 1024 * 1024) AS ram_total_gb,
                CASE
                    WHEN ((SELECT SUM(VALUE) FROM V$SGA) /
                          (SELECT VALUE FROM V$OSSTAT WHERE STAT_NAME = 'PHYSICAL_MEMORY_BYTES')) > 0.5 THEN 'ALERTA: RAM utilizada > 50%'
                    ELSE 'RAM utilizada en rango normal'
                END AS estado_ram
            FROM DUAL
        """;

        try (Connection connection = connectionManager.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            if (resultSet.next()) {
                double ramUsed = resultSet.getDouble("ram_utilizada_db_gb");
                double ramTotal = resultSet.getDouble("ram_total_gb");
                String status = resultSet.getString("estado_ram");
                return new RamUsage(ramUsed, ramTotal, status);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al obtener uso de RAM", e);
        }
        return new RamUsage(0, 0, "No se pudo obtener el estado de la RAM");
    }
    // Método para monitorear uso de tablespaces
    public List<TablespaceUsage> collectTablespaceUsage() {
        String query = """
            SELECT
                df.tablespace_name,
                ROUND((df.total_space - NVL(fs.free_space, 0)) / df.total_space * 100, 2) AS porcentaje_espacio_utilizado,
                ROUND(NVL(fs.free_space, 0) / df.total_space * 100, 2) AS porcentaje_espacio_libre,
                CASE
                    WHEN ROUND((df.total_space - NVL(fs.free_space, 0)) / df.total_space * 100, 2) > 80 THEN 'ALERTA: ESPACIO > 80%'
                    ELSE 'Espacio en rango normal'
                END AS estado_espacio
            FROM
                (SELECT
                     tablespace_name,
                     SUM(bytes) / (1024 * 1024 * 1024) AS total_space
                 FROM
                     dba_data_files
                 GROUP BY
                     tablespace_name) df
            LEFT JOIN
                (SELECT
                     tablespace_name,
                     SUM(bytes) / (1024 * 1024 * 1024) AS free_space
                 FROM
                     dba_free_space
                 GROUP BY
                     tablespace_name) fs
            ON
                df.tablespace_name = fs.tablespace_name
        """;

        List<TablespaceUsage> results = new ArrayList<>();
        try (Connection connection = connectionManager.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                String tablespace = resultSet.getString("tablespace_name");
                double usedPercent = resultSet.getDouble("porcentaje_espacio_utilizado");
                double freePercent = resultSet.getDouble("porcentaje_espacio_libre");
                String status = resultSet.getString("estado_espacio");
                results.add(new TablespaceUsage(tablespace, usedPercent, freePercent, status));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al obtener uso de tablespaces", e);
        }
        return results;
    }

    // Método para obtener consultas más intensivas por latencia
    public List<QueryPerformance> collectTopQueriesByLatency() {
        String query = """
            SELECT
                SQL_ID,
                COUNT(*) AS frecuencia,
                AVG(ELAPSED_TIME / 1000000) AS latencia_promedio_seg,
                CASE
                    WHEN AVG(ELAPSED_TIME / 1000000) > 2 THEN 'ALERTA: Latencia > 2 segundos'
                    ELSE 'Latencia en rango normal'
                END AS estado_latencia
            FROM V$SQL
            WHERE ELAPSED_TIME IS NOT NULL
            GROUP BY SQL_ID
            ORDER BY frecuencia DESC
            FETCH FIRST 10 ROWS ONLY
        """;

        List<QueryPerformance> results = new ArrayList<>();
        try (Connection connection = connectionManager.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                String sqlId = resultSet.getString("SQL_ID");
                int frequency = resultSet.getInt("frecuencia");
                double avgLatency = resultSet.getDouble("latencia_promedio_seg");
                String status = resultSet.getString("estado_latencia");
                results.add(new QueryPerformance(sqlId, frequency, avgLatency, status));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al obtener consultas intensivas", e);
        }
        return results;
    }

    // Método para monitorear SWAP
    public SwapUsage collectSwapUsage() {
        try {
            Process process = Runtime.getRuntime().exec("wmic pagefile get AllocatedBaseSize, CurrentUsage /Value");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            double totalSwap = 0.0;
            double usedSwap = 0.0;

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("AllocatedBaseSize=")) {
                    double value = Double.parseDouble(line.split("=")[1].trim());
                    totalSwap += value;
                } else if (line.startsWith("CurrentUsage=")) {
                    double value = Double.parseDouble(line.split("=")[1].trim());
                    usedSwap += value;
                }
            }
            reader.close();
            process.waitFor();

            if (totalSwap == 0) {
                LOGGER.log(Level.WARNING, "El total de SWAP es cero, evitando división por cero");
                return new SwapUsage(0.0, 0.0, "SWAP no configurado");
            }

            double swapUsedPercentage = (usedSwap / totalSwap) * 100;
            double swapFreePercentage = 100 - swapUsedPercentage;
            String status = swapUsedPercentage > 80 ? "ALERTA: SWAP utilizado > 80%" : "SWAP en rango normal";

            return new SwapUsage(swapUsedPercentage, swapFreePercentage, status);

        } catch (IOException | InterruptedException e) {
            LOGGER.log(Level.SEVERE, "Error al obtener datos de SWAP", e);
            return new SwapUsage(0.0, 0.0, "Error al obtener el estado del SWAP");
        }
    }
    // Método para obtener las tablas más grandes
    public List<TableSize> collectLargestTables() {
        String query = """
            SELECT 
                TABLE_NAME,
                NUM_ROWS AS filas,
                BLOCKS * 8192 / (1024 * 1024 * 1024) AS tamano_gb
            FROM DBA_TABLES
            WHERE NUM_ROWS IS NOT NULL
            AND BLOCKS IS NOT NULL
            ORDER BY tamano_gb DESC
            FETCH FIRST 10 ROWS ONLY
        """;

        List<TableSize> largestTables = new ArrayList<>();
        try (Connection connection = connectionManager.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                String tableName = resultSet.getString("TABLE_NAME");
                long rows = resultSet.getLong("filas");
                double sizeGb = resultSet.getDouble("tamano_gb");

                // Agregar a la lista un nuevo objeto TableSize
                largestTables.add(new TableSize(tableName, rows, sizeGb));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al obtener las tablas más grandes", e);
        }
        return largestTables;
    }

    public List<RedoLogUsage> collectRedoLogUsage() {
        String query = """
        SELECT 
            GROUP# AS grupo,
            SEQUENCE# AS secuencia,
            ARCHIVED AS archivado,
            STATUS AS estado
        FROM V$LOG
    """;

        List<RedoLogUsage> redoLogList = new ArrayList<>();
        try (Connection connection = connectionManager.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                int group = resultSet.getInt("grupo");
                int sequence = resultSet.getInt("secuencia");
                String archived = resultSet.getString("archivado");
                String status = resultSet.getString("estado");
                redoLogList.add(new RedoLogUsage(group, sequence, archived, status));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return redoLogList;
    }

    public List<DiskIOUsage> collectDiskIOUsage() {
        String query = """
        SELECT 
            INST_ID,
            SUM(CASE WHEN NAME = 'physical read total bytes' THEN VALUE ELSE 0 END) / 1024 / 1024 AS lectura_mb,
            SUM(CASE WHEN NAME = 'physical write total bytes' THEN VALUE ELSE 0 END) / 1024 / 1024 AS escritura_mb,
            CASE 
                WHEN (SUM(CASE WHEN NAME = 'physical read total bytes' THEN VALUE ELSE 0 END) / 1024 / 1024) > 1000 THEN 'ALERTA: Lectura > 1000 MB'
                WHEN (SUM(CASE WHEN NAME = 'physical write total bytes' THEN VALUE ELSE 0 END) / 1024 / 1024) > 1000 THEN 'ALERTA: Escritura > 1000 MB'
                ELSE 'Lectura y Escritura en rango normal'
            END AS estado_io
        FROM 
            GV$SYSSTAT
        GROUP BY 
            INST_ID
    """;

        List<DiskIOUsage> results = new ArrayList<>();
        try (Connection connection = connectionManager.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                int instanceId = resultSet.getInt("INST_ID");
                double readMb = resultSet.getDouble("lectura_mb");
                double writeMb = resultSet.getDouble("escritura_mb");
                String status = resultSet.getString("estado_io");
                results.add(new DiskIOUsage(instanceId, readMb, writeMb, status));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }

    public List<ResourceIntensiveQuery> collectResourceIntensiveQueries() {
        String query = """
        SELECT 
            SQL_ID,
            EXECUTIONS,
            ROUND(ELAPSED_TIME / 1000000, 2) AS tiempo_total_segundos,
            ROUND(CPU_TIME / 1000000, 2) AS cpu_total_segundos,
            DISK_READS,
            BUFFER_GETS
        FROM V$SQL
        ORDER BY DISK_READS DESC
        FETCH FIRST 10 ROWS ONLY
    """;

        List<ResourceIntensiveQuery> results = new ArrayList<>();
        try (Connection connection = connectionManager.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                String sqlId = resultSet.getString("SQL_ID");
                int executions = resultSet.getInt("EXECUTIONS");
                double elapsedTime = resultSet.getDouble("tiempo_total_segundos");
                double cpuTime = resultSet.getDouble("cpu_total_segundos");
                long diskReads = resultSet.getLong("DISK_READS");
                long bufferGets = resultSet.getLong("BUFFER_GETS");

                results.add(new ResourceIntensiveQuery(sqlId, executions, elapsedTime, cpuTime, diskReads, bufferGets));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }

    public BackupInfo collectBackupInfo() {
        String query = """
        SELECT 
            MAX(bp.COMPLETION_TIME) AS ultima_fecha_backup,
            SUM(bp.BYTES) / (1024 * 1024 * 1024) AS tamano_total_backup_gb
        FROM 
            V$BACKUP_PIECE bp
    """;

        try (Connection connection = connectionManager.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            if (resultSet.next()) {
                String lastBackupDate = resultSet.getString("ultima_fecha_backup");
                Double totalBackupSizeGb = resultSet.getDouble("tamano_total_backup_gb");

                // Manejar posibles valores null
                if (resultSet.wasNull()) {
                    totalBackupSizeGb = 0.0; // Si el tamaño es nulo, asignar 0
                }
                if (lastBackupDate == null) {
                    lastBackupDate = "Sin respaldos disponibles"; // Si no hay fecha, indicar
                }

                return new BackupInfo(lastBackupDate, totalBackupSizeGb);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return new BackupInfo("Sin respaldos disponibles", 0.0); // Retornar valores predeterminados en caso de error
    }


    // Método para recolectar alertas críticas
    public List<AlertLog> collectCriticalAlerts() {
        String query = """
            SELECT
                TO_CHAR(ORIGINATING_TIMESTAMP, 'YYYY-MM-DD HH24:MI:SS') AS fecha,
                MESSAGE_TEXT AS descripcion_error,
                COUNT(*) AS conteo
            FROM V$DIAG_ALERT_EXT
            WHERE TO_CHAR(ORIGINATING_TIMESTAMP, 'YYYY-MM-DD') BETWEEN TO_CHAR(SYSDATE - 1, 'YYYY-MM-DD') AND TO_CHAR(SYSDATE, 'YYYY-MM-DD')
            AND (MESSAGE_TEXT LIKE '%ORA-%'
                 OR MESSAGE_TEXT LIKE '%Errors in file%'
                 OR MESSAGE_TEXT LIKE '%highscn criteria not met for file%')
            GROUP BY TO_CHAR(ORIGINATING_TIMESTAMP, 'YYYY-MM-DD HH24:MI:SS'),
                     MESSAGE_TEXT
            ORDER BY fecha DESC
        """;


        List<AlertLog> results = new ArrayList<>();
        try (Connection connection = connectionManager.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                String date = resultSet.getString("fecha");
                String description = resultSet.getString("descripcion_error");
                int count = resultSet.getInt("conteo");
                results.add(new AlertLog(date, description, count));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al recolectar alertas críticas", e);
        }
        return results;
    }

    // Método para monitorear sesiones activas
    public SessionUsage collectSessionUsage() {
        String query = """
            SELECT
                COUNT(*) AS conexiones_activas,
                COUNT(DISTINCT SID) AS sesiones_concurrentes
            FROM V$SESSION
            WHERE STATUS = 'ACTIVE'
        """;

        try (Connection connection = connectionManager.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            if (resultSet.next()) {
                int activeConnections = resultSet.getInt("conexiones_activas");
                int concurrentSessions = resultSet.getInt("sesiones_concurrentes");
                String status = activeConnections > 100 ? "ALERTA: Conexiones activas > 100" : "Conexiones en rango normal";
                return new SessionUsage(activeConnections, concurrentSessions, status);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al obtener el estado de las sesiones", e);
        }
        return new SessionUsage(0, 0, "No se pudo obtener el estado de las sesiones");
    }
}
