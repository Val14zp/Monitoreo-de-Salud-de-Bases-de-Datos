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

    public Dashboard() {
        // Crear datasets para los gráficos
        cpuDataset = new DefaultCategoryDataset();
        ramDataset = new DefaultCategoryDataset();
        swapDataset = new DefaultCategoryDataset();
        tablespaceDataset = new DefaultCategoryDataset();
        sessionsDataset = new DefaultCategoryDataset();
        queriesDataset = new DefaultCategoryDataset();
        alertsArea = new JTextArea(10, 30);
        alertsArea.setEditable(false);

        setTitle("Monitoreo de Base de Datos");
        setSize(1200, 800);
        setLocationRelativeTo(null); // Centrar la ventana
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Crear gráficos
        JFreeChart cpuChart = ChartFactory.createBarChart("Uso de CPU", "Tipo", "Porcentaje", cpuDataset);
        JFreeChart ramChart = ChartFactory.createBarChart("Uso de RAM", "Tipo", "Memoria (GB)", ramDataset);
        JFreeChart swapChart = ChartFactory.createBarChart("Uso de SWAP", "Tipo", "SWAP (GB)", swapDataset);
        JFreeChart tablespaceChart = ChartFactory.createBarChart("Uso de Tablespaces", "Tablespace", "Porcentaje", tablespaceDataset);
        JFreeChart sessionsChart = ChartFactory.createBarChart("Conexiones Activas", "Tipo", "Número", sessionsDataset);
        JFreeChart queriesChart = ChartFactory.createBarChart("Consultas Más Intensivas", "Consulta", "Latencia (s)", queriesDataset);

        // Crear paneles de gráficos
        ChartPanel cpuChartPanel = new ChartPanel(cpuChart);
        ChartPanel ramChartPanel = new ChartPanel(ramChart);
        ChartPanel swapChartPanel = new ChartPanel(swapChart);
        ChartPanel tablespaceChartPanel = new ChartPanel(tablespaceChart);
        ChartPanel sessionsChartPanel = new ChartPanel(sessionsChart);
        ChartPanel queriesChartPanel = new ChartPanel(queriesChart);

        // Configurar el panel de alertas
        JScrollPane alertsScrollPane = new JScrollPane(alertsArea);
        JPanel alertsPanel = new JPanel(new BorderLayout());
        alertsPanel.add(new JLabel("Alertas Críticas"), BorderLayout.NORTH);
        alertsPanel.add(alertsScrollPane, BorderLayout.CENTER);

        // Configurar el layout del Dashboard
        setLayout(new GridLayout(3, 2)); // Tres filas, dos columnas

        add(cpuChartPanel);
        add(ramChartPanel);
        add(swapChartPanel);
        add(tablespaceChartPanel);
        add(sessionsChartPanel);
        add(queriesChartPanel);

        // Aplicar el renderer personalizado a todos los gráficos
        applyCustomRenderer(cpuChart, cpuDataset);
        applyCustomRenderer(ramChart, ramDataset);
        applyCustomRenderer(swapChart, swapDataset);
        applyCustomRenderer(tablespaceChart, tablespaceDataset);
        applyCustomRenderer(sessionsChart, sessionsDataset);
        applyCustomRenderer(queriesChart, queriesDataset);

        // Añadir panel de alertas como ventana separada
        JFrame alertsFrame = new JFrame("Alertas Críticas");
        alertsFrame.setSize(500, 300);
        alertsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        alertsFrame.setLocationRelativeTo(null);
        alertsFrame.add(alertsPanel);
        alertsFrame.setVisible(true);
    }

    private void applyCustomRenderer(JFreeChart chart, DefaultCategoryDataset dataset) {
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setRenderer(new CustomBarRenderer(dataset, 1000));
    }

    // Método para actualizar el gráfico de uso de CPU
    public void updateCpuUsage(CpuUsage cpuUsage) {
        cpuDataset.setValue(cpuUsage.getCpuUsedPercentage(), "Usado (%)", "CPU Usada");
        cpuDataset.setValue(cpuUsage.getCpuTotal(), "Total (%)", "CPU Total");
    }

    // Método para actualizar el gráfico de uso de RAM
    public void updateRamUsage(RamUsage ramUsage) {
        ramDataset.setValue(ramUsage.getRamUsedGb(), "Usado (GB)", "RAM Usada");
        ramDataset.setValue(ramUsage.getRamTotalGb(), "Total (GB)", "RAM Total");
    }

    // Método para actualizar el gráfico de uso de SWAP
    public void updateSwapUsage(SwapUsage swapUsage) {
        swapDataset.setValue(swapUsage.getSwapUsedPercentage(), "Usado (%)", "SWAP Usado");
        swapDataset.setValue(swapUsage.getSwapFreePercentage(), "Libre (%)", "SWAP Libre");
    }

    // Método para actualizar el gráfico de uso de Tablespaces
    public void updateTablespaceUsage(String tablespaceName, double usedPercentage, double freePercentage) {
        tablespaceDataset.setValue(usedPercentage, "Usado (%)", tablespaceName);
        tablespaceDataset.setValue(freePercentage, "Libre (%)", tablespaceName);
    }

    // Método para actualizar el gráfico de conexiones activas
    public void updateSessionUsage(SessionUsage sessionUsage) {
        sessionsDataset.setValue(sessionUsage.getActiveConnections(), "Conexiones", "Activas");
        sessionsDataset.setValue(sessionUsage.getConcurrentSessions(), "Sesiones", "Concurrentes");
    }

    // Método para actualizar el gráfico de consultas más intensivas
    public void updateTopQueries(List<QueryPerformance> topQueries) {
        queriesDataset.clear();
        for (QueryPerformance query : topQueries) {
            queriesDataset.setValue(query.getAverageLatency(), "Latencia Promedio (s)", query.getSqlId());
        }
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
            // Llamar al método original para dibujar las barras
            super.drawItem(g2, state, dataArea, plot, domainAxis, rangeAxis, dataset, row, column, pass);

            // Obtener el valor del dataset
            Number value = dataset.getValue(row, column);
            if (value != null) {
                double barValue = value.doubleValue();

                // Coordenadas para dibujar la barra
                double barStart = domainAxis.getCategoryStart(column, getColumnCount(), dataArea, plot.getDomainAxisEdge());
                double barEnd = domainAxis.getCategoryEnd(column, getColumnCount(), dataArea, plot.getDomainAxisEdge());
                double barWidth = barEnd - barStart;
                double barX = barStart + (barWidth * row / getRowCount());
                double barY = rangeAxis.valueToJava2D(barValue, dataArea, plot.getRangeAxisEdge());

                // Posiciones de la barra en el área del gráfico
                Rectangle2D bar = new Rectangle2D.Double(barX, barY, barWidth * 0.8, dataArea.getMaxY() - barY);

                // Si el valor supera el umbral, dibujar una alerta personalizada
                if (barValue > alertThreshold) {
                    String alertText = "¡Alerta! Alto";
                    FontMetrics metrics = g2.getFontMetrics();
                    int textWidth = metrics.stringWidth(alertText);

                    // Dibujar texto encima de la barra
                    g2.setColor(Color.RED);
                    g2.setFont(new Font("Arial", Font.BOLD, 12));
                    g2.drawString(alertText, (int) (bar.getCenterX() - textWidth / 2), (int) (bar.getMinY() - 5));
                }
            }
        }
    }
}

