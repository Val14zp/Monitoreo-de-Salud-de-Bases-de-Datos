public class MemoryUsage {
    private String name = " ";
    private double usedMemoryMB = 0.0;
    private double freeMemoryMB = 0.0;

    public MemoryUsage(String name, double usedMemoryMB, double freeMemoryMB) {
        this.name = name;
        this.usedMemoryMB = usedMemoryMB;
        this.freeMemoryMB = freeMemoryMB;
    }

    public String getName() {
        return name;
    }

    public double getUsedMemoryMB() {
        return usedMemoryMB;
    }

    public double getFreeMemoryMB() {
        return freeMemoryMB;
    }
}
