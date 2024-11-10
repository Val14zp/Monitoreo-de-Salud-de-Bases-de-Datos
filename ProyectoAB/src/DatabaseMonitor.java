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

    // Monitoreo de la base de datos
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
            private List<ResourceUsage> resourceUsageData;
            private List<DiskSpaceUsage> diskSpaceData;
            private MemoryUsage databaseMemoryUsage =  new MemoryUsage("",0,0);;
            private MemoryUsage systemMemoryUsage = new MemoryUsage("",0,0);
            private MemoryUsage swapUsage;
            private int activeSessions;

            @Override
            protected Void doInBackground() throws Exception {
                // Recolectar datos de uso de recursos
                resourceUsageData = monitoringService.collectResourceUsageData();
                // Recolectar datos de uso de espacio en disco
                diskSpaceData = monitoringService.collectDiskSpaceData();
                // Recolectar datos de memoria de la instancia de Oracle
                databaseMemoryUsage = monitoringService.collectDatabaseMemoryUsage();

                // Recolectar datos de SWAP
                //swapUsage = monitoringService.collectSwapUsage();

                // Recolectar datos de conexiones activas
                //activeSessions = monitoringService.collectActiveSessions();

                return null;
            }

            @Override
            protected void done() {
                try {
                    get(); // Manejar excepciones
                    // Actualizar los gráficos en el EDT
                    dashboard.clearDatasets();

                    // Actualizar gráfico de uso de recursos
                    for (ResourceUsage ru : resourceUsageData) {
                        dashboard.updateResourceUsage(ru.getName(), ru.getValue());
                    }

                    // Actualizar gráfico de uso de espacio en disco
                    for (DiskSpaceUsage ds : diskSpaceData) {
                        dashboard.updateDiskSpace(ds.getTablespace(), ds.getUsedSpace(), ds.getFreeSpace());
                    }

                    // Actualizar gráfico de uso de memoria
                    dashboard.updateMemoryUsage(databaseMemoryUsage);
                    dashboard.updateMemoryUsage(systemMemoryUsage);

                    // Actualizar gráfico de uso de SWAP
                    dashboard.updateSwapUsage(swapUsage);

                    // Actualizar el gráfico o valor de sesiones activas
                    dashboard.updateActiveSessions(activeSessions);

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
        worker.execute();
    }



    // Detener el monitoreo y cerrar la conexión
    public void stopMonitoring() {
        if (timer != null) {
            timer.stop();
        }
        connectionManager.closeConnection();
        System.out.println("Monitoreo detenido y conexión cerrada.");
    }
}
