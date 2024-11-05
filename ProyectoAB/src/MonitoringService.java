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

        try (Connection connection = connectionManager.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                String name = resultSet.getString("NAME");
                long value = resultSet.getLong("VALUE");

                String spanishName = nameTranslations.getOrDefault(name, name);

                results.add(new ResourceUsage(spanishName, value));
            }
        } catch (SQLException e) {
            e.printStackTrace();
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

        try (Connection connection = connectionManager.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                String tablespace = resultSet.getString("tablespace_name");
                double usedSpace = resultSet.getDouble("used_space_mb");
                double freeSpace = resultSet.getDouble("free_space_mb");
                results.add(new DiskSpaceUsage(tablespace, usedSpace, freeSpace));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }
    public MemoryUsage collectDatabaseMemoryUsage() {
        String query = "SELECT component, current_size/1024/1024 AS size_mb FROM V$SGA_DYNAMIC_COMPONENTS";
        double totalMemory = 0.0;

        try (Connection connection = connectionManager.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                String component = resultSet.getString("component");
                double sizeMB = resultSet.getDouble("size_mb");
                totalMemory += sizeMB;
            }

            // Obtener el tamaño de la PGA
            String pgaQuery = "SELECT value/1024/1024 AS size_mb FROM V$PGASTAT WHERE name = 'total PGA allocated'";
            double pgaSize = 0.0;
            try (Statement pgaStatement = connection.createStatement();
                 ResultSet pgaResultSet = pgaStatement.executeQuery(pgaQuery)) {

                if (pgaResultSet.next()) {
                    pgaSize = pgaResultSet.getDouble("size_mb");
                }
            }

            totalMemory += pgaSize;

            // Obtener la memoria libre de la instancia (Oracle usa la memoria asignada)
            return new MemoryUsage("Instancia Oracle", totalMemory, 0.0);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
    public MemoryUsage collectSwapUsage() {
        double totalSwap = 0.0;
        double usedSwap = 0.0;

        try {
            Process process = Runtime.getRuntime().exec("wmic pagefile get AllocatedBaseSize, CurrentUsage /Value");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            Map<String, Double> swapValues = new HashMap<>();

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
        }

        return null;
    }
    public int collectActiveSessions() {
        String query = "SELECT COUNT(*) AS active_sessions FROM V$SESSION WHERE STATUS = 'ACTIVE'";
        int activeSessions = 0;

        try (Connection connection = connectionManager.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            if (resultSet.next()) {
                activeSessions = resultSet.getInt("active_sessions");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return activeSessions;
    }

    public DiskIOUsage fetchDiskIOUsage() throws SQLException {
        String sql = "SELECT VALUE FROM V$SYSSTAT WHERE NAME = 'physical reads' OR NAME = 'physical writes'";
        try (Connection connection = connectionManager.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            double readRate = 0.0;
            double writeRate = 0.0;

            while (rs.next()) {
                String statName = rs.getString("NAME");
                double statValue = rs.getDouble("VALUE");

                if ("physical reads".equals(statName)) {
                    readRate = statValue;
                } else if ("physical writes".equals(statName)) {
                    writeRate = statValue;
                }
            }
            return new DiskIOUsage(readRate, writeRate);
        }
    }

    public BackupStatus fetchBackupStatus() throws SQLException {
        String sql = "SELECT COMPLETION_TIME, OUTPUT_BYTES / (1024 * 1024) AS SIZE_MB " +
                "FROM V$RMAN_BACKUP_JOB_DETAILS ORDER BY COMPLETION_TIME DESC FETCH FIRST 1 ROWS ONLY";
        Date lastBackupDate = null;
        double totalSizeMB = 0.0;

        try (Connection connection = connectionManager.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                lastBackupDate = rs.getDate("COMPLETION_TIME");
                totalSizeMB = rs.getDouble("SIZE_MB");
            }
        }
        return new BackupStatus(lastBackupDate, totalSizeMB);
    }

    public ArrayList<Alert> fetchCriticalEvents() throws SQLException {
        String sql = "SELECT SEVERITY, ORIGINATING_TIMESTAMP, MESSAGE_TEXT " +
                "FROM V$ALERT_HISTORY ORDER BY ORIGINATING_TIMESTAMP DESC FETCH FIRST 10 ROWS ONLY";
        ArrayList<Alert> alerts = new ArrayList<>();

        try (Connection connection = connectionManager.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String severity = rs.getString("SEVERITY");
                Timestamp timestamp = rs.getTimestamp("ORIGINATING_TIMESTAMP");
                String description = rs.getString("MESSAGE_TEXT");

                alerts.add(new Alert(severity, timestamp, description));
            }
        }
        return alerts;
    }
}
