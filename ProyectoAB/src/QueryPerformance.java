public class QueryPerformance {
    private String sqlId;
    private int frequency;
    private double averageLatency;
    private String status; // Agregado para manejar el estado

    public QueryPerformance(String sqlId, int frequency, double averageLatency, String status) {
        this.sqlId = sqlId;
        this.frequency = frequency;
        this.averageLatency = averageLatency;
        this.status = status;
    }

    public String getSqlId() {
        return sqlId;
    }

    public int getFrequency() {
        return frequency;
    }

    public double getAverageLatency() {
        return averageLatency;
    }

    public String getStatus() {
        return status;
    }


}
