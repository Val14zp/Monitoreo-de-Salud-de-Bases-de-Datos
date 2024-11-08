public class TablespaceUsage {
    private String tablespaceName;
    private double usedPercentage;
    private double freePercentage;

    public TablespaceUsage(String tablespaceName, double usedPercentage, double freePercentage) {
        this.tablespaceName = tablespaceName;
        this.usedPercentage = usedPercentage;
        this.freePercentage = freePercentage;
    }

    public String getTablespaceName() {
        return tablespaceName;
    }

    public double getUsedPercentage() {
        return usedPercentage;
    }

    public double getFreePercentage() {
        return freePercentage;
    }
}
