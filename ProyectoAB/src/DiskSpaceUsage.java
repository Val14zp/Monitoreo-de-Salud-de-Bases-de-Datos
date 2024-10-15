public class DiskSpaceUsage {
    private String tablespace;
    private double usedSpace;
    private double freeSpace;

    public DiskSpaceUsage(String tablespace, double usedSpace, double freeSpace) {
        this.tablespace = tablespace;
        this.usedSpace = usedSpace;
        this.freeSpace = freeSpace;
    }

    public String getTablespace() {
        return tablespace;
    }

    public double getUsedSpace() {
        return usedSpace;
    }

    public double getFreeSpace() {
        return freeSpace;
    }
}

