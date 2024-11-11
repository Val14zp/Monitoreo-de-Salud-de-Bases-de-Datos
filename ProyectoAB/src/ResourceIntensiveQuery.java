public class ResourceIntensiveQuery {
    private String sqlId;
    private int executions;
    private double elapsedTime;
    private double cpuTime;
    private long diskReads;
    private long bufferGets;

    public ResourceIntensiveQuery(String sqlId, int executions, double elapsedTime, double cpuTime, long diskReads, long bufferGets) {
        this.sqlId = sqlId;
        this.executions = executions;
        this.elapsedTime = elapsedTime;
        this.cpuTime = cpuTime;
        this.diskReads = diskReads;
        this.bufferGets = bufferGets;
    }

    public String getSqlId() {
        return sqlId;
    }

    public int getExecutions() {
        return executions;
    }

    public double getElapsedTime() {
        return elapsedTime;
    }

    public double getCpuTime() {
        return cpuTime;
    }

    public long getDiskReads() {
        return diskReads;
    }

    public long getBufferGets() {
        return bufferGets;
    }
}
