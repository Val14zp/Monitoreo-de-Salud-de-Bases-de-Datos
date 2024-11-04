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

    public List<ResourceUsage> collectResourceUsageData() {
        List<ResourceUsage> results = new ArrayList<>();
        String query = "SELECT * FROM V$SYSSTAT WHERE NAME IN ('CPU used by this session', 'physical reads', 'physical writes')";

        Map<String, String> nameTranslations = new HashMap<>();
        nameTranslations.put("CPU used by this session", "CPU usada por esta sesión");
        nameTranslations.put("physical reads", "lecturas físicas");
        nameTranslations.put("physical writes", "escrituras físicas");

        boolean retry = true;
        int attempts = 0;

        while (retry && attempts < 3) {
            try (Connection connection = connectionManager.getConnection();
                 Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(query)) {

                while (resultSet.next()) {
                    String name = resultSet.getString("NAME");
                    long value = resultSet.getLong("VALUE");

                    String spanishName = nameTranslations.getOrDefault(name, name);

                    results.add(new ResourceUsage(spanishName, value));
                }
                retry = false; // Si la consulta fue exitosa, no es necesario reintentar

            } catch (SQLException e) {
                e.printStackTrace();
                attempts++;
                if (attempts >= 3) {
                    System.err.println("No se pudo recuperar la conexión después de varios intentos en collectResourceUsageData.");
                    retry = false;
                } else {
                    System.out.println("Intentando reconectar en collectResourceUsageData... (Intento " + attempts + ")");
                }
            }
        }
        return results;
    }

    public List<DiskSpaceUsage> collectDiskSpaceData() {
        List<DiskSpaceUsage> results = new ArrayList<>();
        String query = "SELECT df.tablespace_name, " +
                "(df.bytes - fs.bytes_free) / 1024 / 1024 AS used_space_mb, " +
                "fs.bytes_free / 1024 / 1024 AS free_space_mb " +
                "FROM dba_data_files df " +
                "JOIN (SELECT tablespace_name, SUM(bytes) AS bytes_free FROM dba_free_space GROUP BY tablespace_name) fs " +
                "ON df.tablespace_name = fs.tablespace_name";

        boolean retry = true;
        int attempts = 0;

        while (retry && attempts < 3) {
            try (Connection connection = connectionManager.getConnection();
                 Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(query)) {

                while (resultSet.next()) {
                    String tablespace = resultSet.getString("tablespace_name");
                    double usedSpace = resultSet.getDouble("used_space_mb");
                    double freeSpace = resultSet.getDouble("free_space_mb");
                    results.add(new DiskSpaceUsage(tablespace, usedSpace, freeSpace));
                }
                retry = false;

            } catch (SQLException e) {
                e.printStackTrace();
                attempts++;
                if (attempts >= 3) {
                    System.err.println("No se pudo recuperar la conexión después de varios intentos en collectDiskSpaceData.");
                    retry = false;
                } else {
                    System.out.println("Intentando reconectar en collectDiskSpaceData... (Intento " + attempts + ")");
                }
            }
        }
        return results;
    }

    public MemoryUsage collectDatabaseMemoryUsage() {
        String query = "SELECT component, current_size/1024/1024 AS size_mb FROM V$SGA_DYNAMIC_COMPONENTS";
        double totalMemory = 0.0;
        int attempts = 0;
        boolean retry = true;

        while (retry && attempts < 3) {
            try (Connection connection = connectionManager.getConnection();
                 Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(query)) {

                while (resultSet.next()) {
                    String component = resultSet.getString("component");
                    double sizeMB = resultSet.getDouble("size_mb");
                    totalMemory += sizeMB;
                }

                String pgaQuery = "SELECT value/1024/1024 AS size_mb FROM V$PGASTAT WHERE name = 'total PGA allocated'";
                double pgaSize = 0.0;

                try (Statement pgaStatement = connection.createStatement();
                     ResultSet pgaResultSet = pgaStatement.executeQuery(pgaQuery)) {

                    if (pgaResultSet.next()) {
                        pgaSize = pgaResultSet.getDouble("size_mb");
                    }
                }
                totalMemory += pgaSize;
                retry = false;

                return new MemoryUsage("Instancia Oracle", totalMemory, 0.0);

            } catch (SQLException e) {
                e.printStackTrace();
                attempts++;
                if (attempts >= 3) {
                    System.err.println("No se pudo recuperar la conexión después de varios intentos en collectDatabaseMemoryUsage.");
                    retry = false;
                } else {
                    System.out.println("Intentando reconectar en collectDatabaseMemoryUsage... (Intento " + attempts + ")");
                }
            }
        }
        return new MemoryUsage("Instancia Oracle", 0.0, 0.0); // Valores predeterminados en caso de fallo
    }

    public MemoryUsage collectSwapUsage() {
        double totalSwap = 0.0;
        double usedSwap = 0.0;

        try {
            Process process = Runtime.getRuntime().exec("wmic pagefile get AllocatedBaseSize, CurrentUsage /Value");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;

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

            double freeSwap = totalSwap - usedSwap;

            return new MemoryUsage("SWAP", usedSwap, freeSwap);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return new MemoryUsage("SWAP", 0.0, 0.0); // Valores predeterminados en caso de error
        }
    }

    public int collectActiveSessions() {
        String query = "SELECT COUNT(*) AS active_sessions FROM V$SESSION WHERE STATUS = 'ACTIVE'";
        int activeSessions = 0;

        boolean retry = true;
        int attempts = 0;

        while (retry && attempts < 3) {
            try (Connection connection = connectionManager.getConnection();
                 Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(query)) {

                if (resultSet.next()) {
                    activeSessions = resultSet.getInt("active_sessions");
                }
                retry = false;

            } catch (SQLException e) {
                e.printStackTrace();
                attempts++;
                if (attempts >= 3) {
                    System.err.println("No se pudo recuperar la conexión después de varios intentos en collectActiveSessions.");
                    retry = false;
                } else {
                    System.out.println("Intentando reconectar en collectActiveSessions... (Intento " + attempts + ")");
                }
            }
        }
        return activeSessions;
    }

}