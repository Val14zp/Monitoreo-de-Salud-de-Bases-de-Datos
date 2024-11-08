public class SwapUsage {
    private double swapUsedPercentage;
    private double swapFreePercentage;

    public SwapUsage(double swapUsedPercentage, double swapFreePercentage) {
        this.swapUsedPercentage = swapUsedPercentage;
        this.swapFreePercentage = swapFreePercentage;
    }

    public double getSwapUsedPercentage() {
        return swapUsedPercentage;
    }

    public double getSwapFreePercentage() {
        return swapFreePercentage;
    }
}
