import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

public class ConnectionManager {
    private Connection connection;
    private String url;
    private String username;
    private String password;

    public ConnectionManager(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    // Método para establecer la conexión con la base de datos
    public Connection connect() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                // Cargar el driver de Oracle
                Class.forName("oracle.jdbc.OracleDriver");
                // Establecer la conexión
                connection = DriverManager.getConnection(url, username, password);
                System.out.println("Conexión exitosa con la base de datos.");
            } catch (ClassNotFoundException e) {
                System.err.println("No se encontró el driver de Oracle.");
                e.printStackTrace();
            } catch (SQLException e) {
                System.err.println("Error al conectarse a la base de datos.");
                e.printStackTrace();
                throw e;
            }
        }
        return connection;
    }

    // Método para obtener la conexión actual
    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connect(); // Vuelve a conectar si la conexión está cerrada o nula
        }
        return connection;
    }

    // Método para cerrar la conexión
    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Conexión cerrada.");
            } catch (SQLException e) {
                System.err.println("Error al cerrar la conexión.");
                e.printStackTrace();
            }
        }
    }
}
