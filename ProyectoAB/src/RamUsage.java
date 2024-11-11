public class RamUsage {
    private double ramUsedGb;
    private double ramTotalGb;
    private String status; // Agregado para manejar el estado

    public RamUsage(double ramUsedGb, double ramTotalGb, String status) {
        this.ramUsedGb = ramUsedGb;
        this.ramTotalGb = ramTotalGb;
        this.status = status;
    }

    public double getRamUsedGb() {
        return ramUsedGb;
    }

    public double getRamTotalGb() {
        return ramTotalGb;
    }

    public String getStatus() {
        return status;
    }
}
