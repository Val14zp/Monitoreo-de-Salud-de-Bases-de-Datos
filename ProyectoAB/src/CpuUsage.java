public class CpuUsage {
    private double cpuUsedPercentage;
    private int cpuTotal;
    private String status; // Agregado para manejar el estado

    public CpuUsage(double cpuUsedPercentage, int cpuTotal, String status) {
        this.cpuUsedPercentage = cpuUsedPercentage;
        this.cpuTotal = cpuTotal;
        this.status = status;
    }

    public double getCpuUsedPercentage() {
        return cpuUsedPercentage;
    }

    public int getCpuTotal() {
        return cpuTotal;
    }

    public String getStatus() {
        return status;
    }

}
