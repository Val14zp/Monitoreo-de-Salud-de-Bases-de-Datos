import javax.swing.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DatabaseMonitor {
    private ConnectionManager connectionManager;
    private MonitoringService monitoringService;
    private Dashboard dashboard;
    private Timer timer;

    public DatabaseMonitor(String url, String username, String password, Dashboard dashboard) {
        this.dashboard = dashboard;
        connectionManager = new ConnectionManager(url, username, password);
        try {
            // Establecer la conexión con la base de datos
            connectionManager.connect();
            System.out.println("Conexión exitosa con la base de datos.");
        } catch (SQLException e) {
            System.err.println("No se pudo conectar a la base de datos.");
            e.printStackTrace();
        }
        monitoringService = new MonitoringService(connectionManager);
    }

    // Inicia el monitoreo periódico
    public void startMonitoring() {
        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateData();
            }
        });
        timer.start();
    }

    // Método para actualizar los datos y gráficos
    private void updateData() {
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            private CpuUsage cpuUsage;
            private RamUsage ramUsage;
            private SwapUsage swapUsage;
            private List<TablespaceUsage> tablespaceData;
            private List<QueryPerformance> topQueries;
            private List<AlertLog> criticalAlerts;
            private SessionUsage sessionUsage;

            @Override
            protected Void doInBackground() throws Exception {
                // Recolectar datos de uso de CPU
                cpuUsage = monitoringService.collectCpuUsage();

                // Recolectar datos de uso de RAM
                ramUsage = monitoringService.collectRamUsage();

                // Recolectar datos de SWAP
                swapUsage = monitoringService.collectSwapUsage();

                // Recolectar datos de uso de tablaspaces
                tablespaceData = monitoringService.collectTablespaceUsage();

                // Recolectar las consultas más intensivas
                topQueries = monitoringService.collectTopQueriesByLatency();

                // Recolectar alertas críticas
                criticalAlerts = monitoringService.collectCriticalAlerts();

                // Recolectar datos de sesiones activas
                sessionUsage = monitoringService.collectSessionUsage();

                return null;
            }

            @Override
            protected void done() {
                try {
                    get(); // Manejar excepciones
                    // Actualizar la interfaz gráfica con los datos recolectados
                    dashboard.clearDatasets();

                    // Actualizar gráfico de uso de CPU
                    dashboard.updateCpuUsage(cpuUsage);

                    // Actualizar gráfico de uso de RAM
                    dashboard.updateRamUsage(ramUsage);

                    // Actualizar gráfico de uso de SWAP
                    dashboard.updateSwapUsage(swapUsage);

                    // Actualizar gráfico de tablaspaces
                    for (TablespaceUsage ts : tablespaceData) {
                        dashboard.updateTablespaceUsage(ts.getTablespaceName(), ts.getUsedPercentage(), ts.getFreePercentage());
                    }

                    // Actualizar las consultas más intensivas
                    dashboard.updateTopQueries(topQueries);

                    // Mostrar alertas críticas
                    dashboard.updateCriticalAlerts(criticalAlerts);

                    // Actualizar sesiones activas
                    dashboard.updateSessionUsage(sessionUsage);

                    writeToFile(cpuUsage, ramUsage, swapUsage, tablespaceData, topQueries, criticalAlerts, sessionUsage);

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
        worker.execute();
    }
    private void writeToFile(CpuUsage cpuUsage, RamUsage ramUsage, SwapUsage swapUsage,
                             List<TablespaceUsage> tablespaceData, List<QueryPerformance> topQueries,
                             List<AlertLog> criticalAlerts, SessionUsage sessionUsage) {
        String fileName = "historico.txt";


        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true))) {
            writer.write("-----------------------------------------------------------Registro de Monitoreo:----------------------------------------------------------- \n");

            // Registrar CPU Usage
            writer.write("CPU Usage: " + cpuUsage.getCpuUsedPercentage() + "% usado de " + cpuUsage.getCpuTotal() + "% total\n");

            // Registrar RAM Usage
            writer.write("RAM Usage: " + ramUsage.getRamUsedGb() + "GB usado de " + ramUsage.getRamTotalGb() + "GB total\n");

            // Registrar SWAP Usage
            writer.write("SWAP Usage: " + swapUsage.getSwapUsedPercentage() + "% usado, " + swapUsage.getSwapFreePercentage() + "% libre\n");

            // Registrar Tablespace Usage
            writer.write("Tablespace Usage:\n");
            for (TablespaceUsage ts : tablespaceData) {
                writer.write("  - " + ts.getTablespaceName() + ": " + ts.getUsedPercentage() + "% usado, " + ts.getFreePercentage() + "% libre\n");
            }

            // Registrar Query Performance
            writer.write("Top Queries by Latency:\n");
            for (QueryPerformance query : topQueries) {
                writer.write("  - SQL ID: " + query.getSqlId() + ", Frecuencia: " + query.getFrequency() + ", Latencia Promedio: " + query.getAverageLatency() + "s\n");
            }

            // Registrar Critical Alerts
            writer.write("-----------------------------------------------------------Alertas:-----------------------------------------------------------\n");
            for (AlertLog alert : criticalAlerts) {
                writer.write("  - Fecha: " + alert.getDate() + ", Descripción: " + alert.getDescription() + ", Conteo: " + alert.getCount() + "\n");
            }

            // Registrar Session Usage
            writer.write("Session Usage: " + sessionUsage.getActiveConnections() + " conexiones activas, " + sessionUsage.getConcurrentSessions() + " sesiones concurrentes\n");

            writer.write("-----------------------------------------------------------Fin del Registro-----------------------------------------------------------\n\n");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // Detiene el monitoreo y cierra la conexión
    public void stopMonitoring() {
        if (timer != null) {
            timer.stop();
        }
        connectionManager.closeConnection();
        System.out.println("Monitoreo detenido y conexión cerrada.");
    }
}
