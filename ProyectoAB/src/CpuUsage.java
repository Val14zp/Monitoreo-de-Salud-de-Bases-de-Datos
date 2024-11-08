public class CpuUsage {
    private double cpuUsedPercentage;
    private int cpuTotal;

    public CpuUsage(double cpuUsedPercentage, int cpuTotal) {
        this.cpuUsedPercentage = cpuUsedPercentage;
        this.cpuTotal = cpuTotal;
    }

    public double getCpuUsedPercentage() {
        return cpuUsedPercentage;
    }

    public int getCpuTotal() {
        return cpuTotal;
    }
}
