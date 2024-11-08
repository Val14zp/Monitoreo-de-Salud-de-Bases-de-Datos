import javax.swing.*;
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

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
        worker.execute();
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
