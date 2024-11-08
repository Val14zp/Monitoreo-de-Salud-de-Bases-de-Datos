public class RamUsage {
    private double ramUsedGb;
    private double ramTotalGb;

    public RamUsage(double ramUsedGb, double ramTotalGb) {
        this.ramUsedGb = ramUsedGb;
        this.ramTotalGb = ramTotalGb;
    }

    public double getRamUsedGb() {
        return ramUsedGb;
    }

    public double getRamTotalGb() {
        return ramTotalGb;
    }
}
