import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Parámetros de conexión a la base de datos
        String url = "jdbc:oracle:thin:@localhost:1521:XE"; // Cambia según tu configuración
        String username = "sys as sysdba"; // Cambia por tu nombre de usuario
        String password = "Fanicio22"; // Cambia por tu contraseña

        // Crear e iniciar la GUI en el hilo de despacho de eventos de Swing
        SwingUtilities.invokeLater(() -> {
            Dashboard dashboard = new Dashboard();
            dashboard.setVisible(true);

            // Crear el monitor de la base de datos
            DatabaseMonitor monitor = new DatabaseMonitor(url, username, password, dashboard);
            monitor.startMonitoring();

            // Agregar un listener para detener el monitoreo al cerrar la ventana
            dashboard.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                    monitor.stopMonitoring();
                    System.exit(0);
                }
            });
        });
    }
}
