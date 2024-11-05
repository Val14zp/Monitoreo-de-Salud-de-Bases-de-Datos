import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

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
    private DefaultCategoryDataset diskIODataset;
    private JLabel lastBackupLabel;
    private JLabel backupSizeLabel;
    private JTextArea alertArea;

    public Dashboard() {
        // Crear datasets para los gráficos
        resourceDataset = new DefaultCategoryDataset();
        diskDataset = new DefaultCategoryDataset();
        memoryDataset = new DefaultCategoryDataset();
        swapDataset = new DefaultCategoryDataset();
        sessionsDataset = new DefaultCategoryDataset();
        diskIODataset = new DefaultCategoryDataset();

        lastBackupLabel = new JLabel("Last Backup: ");
        backupSizeLabel = new JLabel("Backup Size: ");

        alertArea = new JTextArea(10, 50); // 10 rows, 50 columns
        alertArea.setEditable(false);

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
        JFreeChart diskIOChart = ChartFactory.createLineChart(
                "Disk IO Rates",
                "Time",
                "Rate (MB/s)",
                diskIODataset
        );

        // Crear paneles de gráficos
        ChartPanel resourceChartPanel = new ChartPanel(resourceChart);
        ChartPanel diskChartPanel = new ChartPanel(diskChart);
        ChartPanel memoryChartPanel = new ChartPanel(memoryChart);
        ChartPanel swapChartPanel = new ChartPanel(swapChart);
        ChartPanel sessionChartPanel = new ChartPanel(sessionChart);
        ChartPanel diskIOChartPanel = new ChartPanel(diskIOChart);
        JScrollPane scrollPane = new JScrollPane(alertArea);

// Configurar el layout del Dashboard
        setLayout(new GridLayout(2, 2)); // Dos filas, dos columnas

        add(resourceChartPanel);
        add(diskChartPanel);
        add(memoryChartPanel);
        add(swapChartPanel);
        add(sessionChartPanel);
        add(diskIOChartPanel);
        add(lastBackupLabel);
        add(backupSizeLabel);
        add(scrollPane);

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
    //Método para actualizar el gráfico de Disk IO data
    public void updateDiskIOUsage(double readRate, double writeRate) {
        diskIODataset.setValue(readRate, "Read Rate (MB/s)", "Current");
        diskIODataset.setValue(writeRate, "Write Rate (MB/s)", "Current");
    }
    //Método para actualizar el gráfico de BackupStatus
    public void updateBackupStatus(BackupStatus backupStatus) {
        lastBackupLabel.setText("Last Backup: " + backupStatus.getLastBackupDate().toString());
        backupSizeLabel.setText("Backup Size: " + String.format("%.2f MB", backupStatus.getTotalBackupSizeMB()));
    }
    //Método para actualizar el gráfico Alertas
    public void updateAlerts(ArrayList<Alert> alerts) {
        alertArea.setText(""); // Clear previous alerts
        for (Alert alert : alerts) {
            alertArea.append(alert.toString() + "\n"); // Append each alert
        }
    }
    // Método para actualizar el gráfico de uso de SWAP
    /*public void updateSwapUsage(MemoryUsage swapUsage) {
        swapDataset.setValue(swapUsage.getUsedMemoryMB(), "Usado (MB)", swapUsage.getName());
        swapDataset.setValue(swapUsage.getFreeMemoryMB(), "Libre (MB)", swapUsage.getName());
    }
    // Método para actualizar el gráfico de conexiones activas
    public void updateActiveSessions(int activeSessions) {
        sessionsDataset.setValue(activeSessions, "Conexiones", "Activas");
    }
*/



    // Modificar el método clearDatasets
    public void clearDatasets() {
        resourceDataset.clear();
        diskDataset.clear();
        memoryDataset.clear();
        swapDataset.clear();
        sessionsDataset.clear();
    }


}
