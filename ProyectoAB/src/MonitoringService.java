import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MonitoringService {
    private ConnectionManager connectionManager;

    public MonitoringService(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    public String collectServerHost() {
        String query = "SELECT sys_context('USERENV','SERVER_HOST') FROM dual";
        try (Connection connection = connectionManager.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            if (resultSet.next()) {
                return resultSet.getString(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Unknown Host";
    }

    public CpuUsage collectCpuUsage() {
        String query = """
            SELECT 
                (SELECT VALUE FROM V$SYSSTAT WHERE NAME = 'CPU used by this session') / 
                (SELECT VALUE FROM V$SYSSTAT WHERE NAME = 'DB time') * 100 AS cpu_utilizado_por_base_datos,
                (SELECT VALUE FROM V$OSSTAT WHERE STAT_NAME = 'NUM_CPUS') AS cpu_total
            FROM DUAL
        """;

        try (Connection connection = connectionManager.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            if (resultSet.next()) {
                double cpuUsed = resultSet.getDouble("cpu_utilizado_por_base_datos");
                int cpuTotal = resultSet.getInt("cpu_total");
                return new CpuUsage(cpuUsed, cpuTotal);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new CpuUsage(0, 0);
    }

    public RamUsage collectRamUsage() {
        String query = """
            SELECT
                (SELECT SUM(VALUE) FROM V$SGA) / (1024 * 1024 * 1024) AS ram_utilizada_db_gb,
                (SELECT VALUE FROM V$OSSTAT WHERE STAT_NAME = 'PHYSICAL_MEMORY_BYTES') / (1024 * 1024 * 1024) AS ram_total_gb
            FROM DUAL
        """;

        try (Connection connection = connectionManager.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            if (resultSet.next()) {
                double ramUsed = resultSet.getDouble("ram_utilizada_db_gb");
                double ramTotal = resultSet.getDouble("ram_total_gb");
                return new RamUsage(ramUsed, ramTotal);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new RamUsage(0, 0);
    }

    public List<TablespaceUsage> collectTablespaceUsage() {
        String query = """
            SELECT 
                df.tablespace_name,
                ROUND((df.total_space - NVL(fs.free_space, 0)) / df.total_space * 100, 2) AS porcentaje_espacio_utilizado,
                ROUND(NVL(fs.free_space, 0) / df.total_space * 100, 2) AS porcentaje_espacio_libre
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
                results.add(new TablespaceUsage(tablespace, usedPercent, freePercent));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }
    public SwapUsage collectSwapUsage() {
        String query = """
            SELECT 
                NVL((SELECT VALUE FROM V$OSSTAT WHERE STAT_NAME = 'SWAP_USED'), 0) / 
                NVL((SELECT VALUE FROM V$OSSTAT WHERE STAT_NAME = 'SWAP_TOTAL'), 1) * 100 AS porcentaje_swap_utilizado,
                100 - (NVL((SELECT VALUE FROM V$OSSTAT WHERE STAT_NAME = 'SWAP_USED'), 0) / 
                       NVL((SELECT VALUE FROM V$OSSTAT WHERE STAT_NAME = 'SWAP_TOTAL'), 1) * 100) AS porcentaje_swap_disponible
            FROM DUAL
        """;

        try (Connection connection = connectionManager.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            if (resultSet.next()) {
                double usedSwap = resultSet.getDouble("porcentaje_swap_utilizado");
                double freeSwap = resultSet.getDouble("porcentaje_swap_disponible");
                return new SwapUsage(usedSwap, freeSwap);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new SwapUsage(0, 0);
    }

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

        List<TableSize> results = new ArrayList<>();
        try (Connection connection = connectionManager.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                String tableName = resultSet.getString("TABLE_NAME");
                long rows = resultSet.getLong("filas");
                double sizeGb = resultSet.getDouble("tamano_gb");
                results.add(new TableSize(tableName, rows, sizeGb));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
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

        List<RedoLogUsage> results = new ArrayList<>();
        try (Connection connection = connectionManager.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                int group = resultSet.getInt("grupo");
                int sequence = resultSet.getInt("secuencia");
                String archived = resultSet.getString("archivado");
                String status = resultSet.getString("estado");
                results.add(new RedoLogUsage(group, sequence, archived, status));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }

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
                return new SessionUsage(activeConnections, concurrentSessions);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new SessionUsage(0, 0);
    }

    public List<QueryPerformance> collectTopQueriesByLatency() {
        String query = """
            SELECT 
                SQL_ID,
                COUNT(*) AS frecuencia,
                AVG(ELAPSED_TIME / 1000000) AS latencia_promedio_seg
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
                results.add(new QueryPerformance(sqlId, frequency, avgLatency));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }

    public List<DiskIOUsage> collectDiskIOUsage() {
        String query = """
            SELECT 
                INST_ID,
                SUM(CASE WHEN NAME = 'physical read total bytes' THEN VALUE ELSE 0 END) / 1024 / 1024 AS lectura_mb,
                SUM(CASE WHEN NAME = 'physical write total bytes' THEN VALUE ELSE 0 END) / 1024 / 1024 AS escritura_mb
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
                results.add(new DiskIOUsage(instanceId, readMb, writeMb));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }

    public BackupUsage collectBackupUsage() {
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
                Timestamp lastBackupDate = resultSet.getTimestamp("ultima_fecha_backup");
                double backupSizeGb = resultSet.getDouble("tamano_total_backup_gb");
                return new BackupUsage(lastBackupDate, backupSizeGb);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new BackupUsage(null, 0);
    }

    public List<AlertLog> collectCriticalAlerts() {
        String query = """
            SELECT 
                TO_CHAR(ORIGINATING_TIMESTAMP, 'YYYY-MM-DD HH24:MI:SS') AS fecha,
                MESSAGE_TEXT AS descripcion_error,
                COUNT(*) AS conteo
            FROM 
                V$DIAG_ALERT_EXT
            WHERE 
                TO_CHAR(ORIGINATING_TIMESTAMP, 'YYYY-MM-DD') BETWEEN TO_CHAR(SYSDATE - 1, 'YYYY-MM-DD') AND TO_CHAR(SYSDATE, 'YYYY-MM-DD')
                AND (MESSAGE_TEXT LIKE '%ORA-%' 
                     OR MESSAGE_TEXT LIKE '%Errors in file%' 
                     OR MESSAGE_TEXT LIKE '%highscn criteria not met for file%'
                     OR MESSAGE_TEXT LIKE '%Warning%')
            GROUP BY 
                TO_CHAR(ORIGINATING_TIMESTAMP, 'YYYY-MM-DD HH24:MI:SS'),
                MESSAGE_TEXT
            ORDER BY 
                fecha DESC
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
            e.printStackTrace();
        }
        return results;
    }
}
