import javax.swing.*;
import java.awt.*;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;

public class Dashboard extends JFrame {
    private DefaultCategoryDataset resourceDataset;
    private DefaultCategoryDataset diskDataset;
    private DefaultCategoryDataset memoryDataset;
    private DefaultCategoryDataset swapDataset;
    private DefaultCategoryDataset sessionsDataset;

    public Dashboard() {
        // Crear datasets para los gráficos
        resourceDataset = new DefaultCategoryDataset();
        diskDataset = new DefaultCategoryDataset();
        memoryDataset = new DefaultCategoryDataset();
        swapDataset = new DefaultCategoryDataset();
        sessionsDataset = new DefaultCategoryDataset();

        setTitle("Monitoreo de Base de Datos");
        setSize(800, 600);
        setLocationRelativeTo(null); // Centrar la ventana
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Crear datasets para los gráficos
        resourceDataset = new DefaultCategoryDataset();
        diskDataset = new DefaultCategoryDataset();

        // Crear gráficos
        JFreeChart resourceChart = ChartFactory.createBarChart(
                "Uso de Recursos",
                "Recurso",
                "Valor",
                resourceDataset
        );

        JFreeChart diskChart = ChartFactory.createBarChart(
                "Uso de Espacio en Disco",
                "Tablespace",
                "Espacio (MB)",
                diskDataset
        );

        JFreeChart memoryChart = ChartFactory.createBarChart(
                "Uso de Memoria RAM",
                "Tipo",
                "Memoria (MB)",
                memoryDataset
        );

        JFreeChart swapChart = ChartFactory.createBarChart(
                "Uso de SWAP",
                "Tipo",
                "SWAP (MB)",
                swapDataset
        );
        JFreeChart sessionChart = ChartFactory.createBarChart("Conexiones Activas", "Conexiones", "Número", sessionsDataset);

        // Crear paneles de gráficos
        ChartPanel resourceChartPanel = new ChartPanel(resourceChart);
        ChartPanel diskChartPanel = new ChartPanel(diskChart);
        ChartPanel memoryChartPanel = new ChartPanel(memoryChart);
        ChartPanel swapChartPanel = new ChartPanel(swapChart);
        ChartPanel sessionChartPanel = new ChartPanel(sessionChart);

// Configurar el layout del Dashboard
        setLayout(new GridLayout(2, 2)); // Dos filas, dos columnas

        add(resourceChartPanel);
        add(diskChartPanel);
        add(memoryChartPanel);
        add(swapChartPanel);
        add(sessionChartPanel);

    }

    // Método para actualizar el gráfico de uso de recursos
    public void updateResourceUsage(String name, long value) {
        resourceDataset.setValue(value, "Valor", name);
    }

    // Método para actualizar el gráfico de uso de espacio en disco
    public void updateDiskSpace(String tablespace, double usedSpace, double freeSpace) {
        diskDataset.setValue(usedSpace, "Usado (MB)", tablespace);
        diskDataset.setValue(freeSpace, "Libre (MB)", tablespace);
    }


    // Método para actualizar el gráfico de uso de memoria
    public void updateMemoryUsage(MemoryUsage memoryUsage) {
        memoryDataset.setValue(memoryUsage.getUsedMemoryMB(), "Usado (MB)", memoryUsage.getName());
        memoryDataset.setValue(memoryUsage.getFreeMemoryMB(), "Libre (MB)", memoryUsage.getName());
    }


    // Método para actualizar el gráfico de uso de SWAP
    public void updateSwapUsage(MemoryUsage swapUsage) {
        swapDataset.setValue(swapUsage.getUsedMemoryMB(), "Usado (MB)", swapUsage.getName());
        swapDataset.setValue(swapUsage.getFreeMemoryMB(), "Libre (MB)", swapUsage.getName());
    }
    // Método para actualizar el gráfico de conexiones activas
    public void updateActiveSessions(int activeSessions) {
        sessionsDataset.setValue(activeSessions, "Conexiones", "Activas");
    }

    // Modificar el método clearDatasets
    public void clearDatasets() {
        resourceDataset.clear();
        diskDataset.clear();
        memoryDataset.clear();
        swapDataset.clear();
        sessionsDataset.clear();
    }


}