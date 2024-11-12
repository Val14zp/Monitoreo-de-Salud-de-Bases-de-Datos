import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.List;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.CategoryItemRendererState;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

public class Dashboard extends JFrame {
    private DefaultCategoryDataset cpuDataset;
    private DefaultCategoryDataset ramDataset;
    private DefaultCategoryDataset swapDataset;
    private DefaultCategoryDataset tablespaceDataset;
    private DefaultCategoryDataset sessionsDataset;
    private DefaultCategoryDataset queriesDataset;
    private JTextArea alertsArea;
    private JTable largestTablesTable; // Para mostrar las tablas más grandes
    private JTable redoLogTable;
    private DefaultCategoryDataset diskIODataset;
    private DefaultCategoryDataset resourceIntensiveQueryDataset;
    private JTable resourceIntensiveQueryTable;
    private JTable backupTable;


    public Dashboard() {
        // Crear datasets para los gráficos
        cpuDataset = new DefaultCategoryDataset();
        ramDataset = new DefaultCategoryDataset();
        swapDataset = new DefaultCategoryDataset();
        tablespaceDataset = new DefaultCategoryDataset();
        sessionsDataset = new DefaultCategoryDataset();
        queriesDataset = new DefaultCategoryDataset();
        diskIODataset = new DefaultCategoryDataset();
        alertsArea = new JTextArea(10, 30);
        alertsArea.setEditable(false);

        // Crear gráficos
        JFreeChart cpuChart = ChartFactory.createBarChart("Uso de CPU", "Tipo", "Porcentaje", cpuDataset);
        JFreeChart ramChart = ChartFactory.createBarChart("Uso de RAM", "Tipo", "Memoria (GB)", ramDataset);
        JFreeChart swapChart = ChartFactory.createBarChart("Uso de SWAP", "Tipo", "SWAP (GB)", swapDataset);
        JFreeChart tablespaceChart = ChartFactory.createBarChart("Uso de Tablespaces", "Tablespace", "Porcentaje", tablespaceDataset);
        JFreeChart sessionsChart = ChartFactory.createBarChart("Conexiones Activas", "Tipo", "Número", sessionsDataset);
        JFreeChart queriesChart = ChartFactory.createBarChart("Consultas Más Intensivas", "Consulta", "Latencia (s)", queriesDataset);
        JFreeChart diskIOChart = ChartFactory.createBarChart("Disk IO Usage", "Instancia", "MB", diskIODataset);

        // Crear paneles de gráficos más pequeños
        ChartPanel cpuChartPanel = new ChartPanel(cpuChart, false, true, false, false, true);
        cpuChartPanel.setPreferredSize(new Dimension(300, 200));
        ChartPanel ramChartPanel = new ChartPanel(ramChart, false, true, false, false, true);
        ramChartPanel.setPreferredSize(new Dimension(300, 200));
        ChartPanel swapChartPanel = new ChartPanel(swapChart, false, true, false, false, true);
        swapChartPanel.setPreferredSize(new Dimension(300, 200));
        ChartPanel tablespaceChartPanel = new ChartPanel(tablespaceChart, false, true, false, false, true);
        tablespaceChartPanel.setPreferredSize(new Dimension(300, 200));
        ChartPanel sessionsChartPanel = new ChartPanel(sessionsChart, false, true, false, false, true);
        sessionsChartPanel.setPreferredSize(new Dimension(300, 200));
        ChartPanel queriesChartPanel = new ChartPanel(queriesChart, false, true, false, false, true);
        queriesChartPanel.setPreferredSize(new Dimension(300, 200));
        ChartPanel diskIOChartPanel = new ChartPanel(diskIOChart, false, true, false, false, true);
        diskIOChartPanel.setPreferredSize(new Dimension(300, 200));

        // Configurar la tabla de tablas más grandes
        String[] columnNames = {"Tabla", "Filas", "Tamaño (GB)"};
        Object[][] initialData = new Object[0][3]; // Sin datos iniciales
        largestTablesTable = new JTable(initialData, columnNames);
        JScrollPane largestTablesScrollPane = new JScrollPane(largestTablesTable);
        largestTablesScrollPane.setPreferredSize(new Dimension(300, 200));

        // Configurar la tabla de consultas intensivas
        String[] columnNames2 = {"SQL ID", "Ejecutadas", "Tiempo Total (s)", "Tiempo CPU (s)", "Lecturas de Disco", "Buffer Gets"};
        Object[][] initialData2 = new Object[0][6]; // Datos iniciales vacíos
        resourceIntensiveQueryTable = new JTable(initialData2, columnNames2);
        JScrollPane resourceIntensiveQueryScrollPane = new JScrollPane(resourceIntensiveQueryTable);
        resourceIntensiveQueryScrollPane.setPreferredSize(new Dimension(600, 200));

        // Configurar la tabla de redologs
        String[] redoLogColumns = {"Grupo", "Secuencia", "Archivado", "Estado"};
        Object[][] redoLogData = new Object[0][4]; // Sin datos iniciales
        redoLogTable = new JTable(redoLogData, redoLogColumns);
        JScrollPane redoLogScrollPane = new JScrollPane(redoLogTable);
        redoLogScrollPane.setPreferredSize(new Dimension(300, 200));

        String[] backupColumnNames = {"Última Fecha de Respaldo", "Tamaño Total (GB)"};
        Object[][] backupInitialData = new Object[0][2]; // Sin datos iniciales
        backupTable = new JTable(backupInitialData, backupColumnNames);
        JScrollPane backupScrollPane = new JScrollPane(backupTable);
        backupScrollPane.setPreferredSize(new Dimension(300, 100));

        // Crear un panel para la tabla de respaldo y agregarlo al layout
        JPanel backupPanel = new JPanel(new BorderLayout());
        backupPanel.add(new JLabel("Información de Respaldo"), BorderLayout.NORTH);
        backupPanel.add(backupScrollPane, BorderLayout.CENTER);

        // Configurar el panel de alertas
        JScrollPane alertsScrollPane = new JScrollPane(alertsArea);
        JPanel alertsPanel = new JPanel(new BorderLayout());
        alertsPanel.add(new JLabel("Alertas Críticas"), BorderLayout.NORTH);
        alertsPanel.add(alertsScrollPane, BorderLayout.CENTER);

        // Configurar el layout del Dashboard
        setTitle("Monitoreo de Base de Datos");
        setSize(800, 600); // Tamaño más pequeño de la ventana
        setLocationRelativeTo(null); // Centrar la ventana
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());
        JPanel chartsPanel = new JPanel(new GridLayout(0, 2)); // Número de filas determinado por la cantidad de componentes
        chartsPanel.add(cpuChartPanel);
        chartsPanel.add(ramChartPanel);
        chartsPanel.add(swapChartPanel);
        chartsPanel.add(tablespaceChartPanel);
        chartsPanel.add(sessionsChartPanel);
        chartsPanel.add(queriesChartPanel);
        chartsPanel.add(diskIOChartPanel);
        chartsPanel.add(largestTablesScrollPane); // Añadir la tabla de tablas más grandes
        chartsPanel.add(redoLogScrollPane);
        chartsPanel.add(resourceIntensiveQueryScrollPane); // Añadir la tabla de consultas intensivas
        chartsPanel.add(backupPanel);

        // Añadir el panel principal al JScrollPane
        JScrollPane mainScrollPane = new JScrollPane(chartsPanel);
        mainScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        mainPanel.add(mainScrollPane, BorderLayout.CENTER);
        mainPanel.add(alertsPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);

        // Aplicar el renderer personalizado a todos los gráficos (método hipotético)
        applyCustomRenderer(cpuChart, cpuDataset, 0);
        applyCustomRenderer(ramChart, ramDataset,  50);
        applyCustomRenderer(swapChart, swapDataset, 80);
        applyCustomRenderer(tablespaceChart, tablespaceDataset,50);
        applyCustomRenderer(sessionsChart, sessionsDataset,50);
        applyCustomRenderer(queriesChart, queriesDataset,50);
        applyCustomRenderer(diskIOChart, diskIODataset,50);
    }

    private void applyCustomRenderer(JFreeChart chart, DefaultCategoryDataset dataset, double alertThreshold) {
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setRenderer(new CustomBarRenderer(dataset, alertThreshold));
    }


    // Método para actualizar el gráfico de uso de SWAP
    public void updateSwapUsage(SwapUsage swapUsage) {
        swapDataset.setValue(swapUsage.getSwapUsedPercentage(), "Usado (%)", "SWAP Usado");
        swapDataset.setValue(swapUsage.getSwapFreePercentage(), "Libre (%)", "SWAP Libre");
    }

    // Método para actualizar el gráfico de conexiones activas
    public void updateSessionUsage(SessionUsage sessionUsage) {
        sessionsDataset.setValue(sessionUsage.getActiveConnections(), "Conexiones", "Activas");
        sessionsDataset.setValue(sessionUsage.getConcurrentSessions(), "Sesiones", "Concurrentes");
    }
    // Método para actualizar el gráfico de uso de RAM
    public void updateRamUsage(RamUsage ramUsage) {
        ramDataset.setValue(ramUsage.getRamUsedGb(), "Usado (GB)", "RAM Usada");
        ramDataset.setValue(ramUsage.getRamTotalGb(), "Total (GB)", "RAM Total");
        if (ramUsage.getStatus().contains("ALERTA")) {
            alertsArea.append(ramUsage.getStatus() + "\n");
        }
    }

    // Método para actualizar el gráfico de uso de Tablespaces
    public void updateTablespaceUsage(String tablespaceName, double usedPercentage, double freePercentage, String alertStatus) {
        tablespaceDataset.setValue(usedPercentage, "Usado (%)", tablespaceName);
        tablespaceDataset.setValue(freePercentage, "Libre (%)", tablespaceName);
        if (alertStatus.contains("ALERTA")) {
            alertsArea.append(alertStatus + " en " + tablespaceName + "\n");
        }
    }

    // Método para actualizar la tabla de las tablas más grandes
    public void updateLargestTables(List<TableSize> largestTables) {
        String[] columnNames = {"Tabla", "Filas", "Tamaño (GB)"};
        Object[][] data = new Object[largestTables.size()][3];
        for (int i = 0; i < largestTables.size(); i++) {
            TableSize table = largestTables.get(i);
            data[i][0] = table.getTableName();
            data[i][1] = table.getRows();
            data[i][2] = table.getSizeGb();
        }
        largestTablesTable.setModel(new javax.swing.table.DefaultTableModel(data, columnNames));
    }
    // Método para actualizar la tabla de los redologs
    public void updateRedoLogUsage(List<RedoLogUsage> redoLogs) {
        String[] columnNames = {"Grupo", "Secuencia", "Archivado", "Estado"};
        Object[][] data = new Object[redoLogs.size()][4];
        for (int i = 0; i < redoLogs.size(); i++) {
            RedoLogUsage redoLog = redoLogs.get(i);
            data[i][0] = redoLog.getGroup();
            data[i][1] = redoLog.getSequence();
            data[i][2] = redoLog.getArchived();
            data[i][3] = redoLog.getStatus();
        }
        redoLogTable.setModel(new javax.swing.table.DefaultTableModel(data, columnNames));
    }

    // Método para actualizar el gráfico de uso de CPU
    public void updateCpuUsage(CpuUsage cpuUsage) {
        cpuDataset.setValue(cpuUsage.getCpuUsedPercentage(), "Usado (%)", "CPU Usada");
        cpuDataset.setValue(cpuUsage.getCpuTotal(), "Total (%)", "CPU Total");
        if (cpuUsage.getStatus().contains("ALERTA")) {
            alertsArea.append(cpuUsage.getStatus() + "\n");
        }
    }
    // Método para actualizar el gráfico de consultas más intensivas
    public void updateTopQueries(List<QueryPerformance> queries) {
        queriesDataset.clear();
        for (QueryPerformance query : queries) {
            queriesDataset.setValue(query.getAverageLatency(), "Latencia Promedio (s)", query.getSqlId());
            if (query.getStatus().contains("ALERTA")) {
                alertsArea.append(query.getStatus() + " para SQL ID: " + query.getSqlId() + "\n");
            }
        }
    }


    public void updateDiskIOUsage(List<DiskIOUsage> diskIOUsageData) {
        diskIODataset.clear();
        for (DiskIOUsage diskIO : diskIOUsageData) {
            diskIODataset.setValue(diskIO.getReadMb(), "Lectura (MB)", "Instancia " + diskIO.getInstanceId());
            diskIODataset.setValue(diskIO.getWriteMb(), "Escritura (MB)", "Instancia " + diskIO.getInstanceId());
            if (diskIO.getReadMb() > 1000 || diskIO.getWriteMb() > 1000) {
                alertsArea.append("¡Alerta Crítica! IO de Disco excedió los 1000 MB en Instancia " + diskIO.getInstanceId() + ".\n");
            }
        }
    }

    public void updateBackupTable(BackupInfo backupInfo) {
        String[] columnNames = {"Última Fecha de Respaldo", "Tamaño Total (GB)"};
        Object[][] data = new Object[1][2];
        data[0][0] = backupInfo.getLastBackupDate(); // Fecha del respaldo
        data[0][1] = backupInfo.getTotalBackupSizeGb(); // Tamaño total en GB

        backupTable.setModel(new javax.swing.table.DefaultTableModel(data, columnNames));
    }


    public void updateResourceIntensiveQueries(List<ResourceIntensiveQuery> queries) {
        String[] columnNames = {"SQL ID", "Ejecutadas", "Tiempo Total (s)", "Tiempo CPU (s)", "Lecturas de Disco", "Buffer Gets"};
        Object[][] data = new Object[queries.size()][6];
        for (int i = 0; i < queries.size(); i++) {
            ResourceIntensiveQuery query = queries.get(i);
            data[i][0] = query.getSqlId();
            data[i][1] = query.getExecutions();
            data[i][2] = query.getElapsedTime();
            data[i][3] = query.getCpuTime();
            data[i][4] = query.getDiskReads();
            data[i][5] = query.getBufferGets();
        }
        resourceIntensiveQueryTable.setModel(new javax.swing.table.DefaultTableModel(data, columnNames));
    }


    // Método para mostrar alertas críticas
    public void updateCriticalAlerts(List<AlertLog> criticalAlerts) {
        alertsArea.setText(""); // Limpiar el área de texto
        for (AlertLog alert : criticalAlerts) {
            alertsArea.append(alert.getDate() + " - " + alert.getDescription() + " (x" + alert.getCount() + ")\n");
        }
    }

    // Método para limpiar todos los datasets
    public void clearDatasets() {
        cpuDataset.clear();
        ramDataset.clear();
        swapDataset.clear();
        tablespaceDataset.clear();
        sessionsDataset.clear();
        queriesDataset.clear();
        alertsArea.setText("");
    }

    class CustomBarRenderer extends BarRenderer {
        private final CategoryDataset dataset;
        private final double alertThreshold;

        public CustomBarRenderer(CategoryDataset dataset, double alertThreshold) {
            this.dataset = dataset;
            this.alertThreshold = alertThreshold;
        }

        @Override
        public void drawItem(Graphics2D g2, CategoryItemRendererState state, Rectangle2D dataArea,
                             CategoryPlot plot, CategoryAxis domainAxis, ValueAxis rangeAxis,
                             CategoryDataset dataset, int row, int column, int pass) {
            super.drawItem(g2, state, dataArea, plot, domainAxis, rangeAxis, dataset, row, column, pass);

            Number value = dataset.getValue(row, column);
            if (value != null && value.doubleValue() > alertThreshold) {
                String alertText = "¡ALERTA!";
                FontMetrics metrics = g2.getFontMetrics();
                int textWidth = metrics.stringWidth(alertText);
                g2.setColor(Color.RED);
                g2.setFont(new Font("Arial", Font.BOLD, 12));
                g2.drawString(alertText, (int) (dataArea.getCenterX() - textWidth / 2), (int) (dataArea.getMinY() - 5));
            }
        }
    }
}
