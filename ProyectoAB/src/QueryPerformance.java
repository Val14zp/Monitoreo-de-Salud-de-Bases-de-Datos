public class QueryPerformance {
    private String sqlId;
    private int frequency;
    private double averageLatency;

    public QueryPerformance(String sqlId, int frequency, double averageLatency) {
        this.sqlId = sqlId;
        this.frequency = frequency;
        this.averageLatency = averageLatency;
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
}
