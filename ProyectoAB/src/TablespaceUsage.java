public class TablespaceUsage {
    private String tablespaceName;
    private double usedPercentage;
    private double freePercentage;
    private String status; // Agregado para manejar el estado

    public TablespaceUsage(String tablespaceName, double usedPercentage, double freePercentage, String status) {
        this.tablespaceName = tablespaceName;
        this.usedPercentage = usedPercentage;
        this.freePercentage = freePercentage;
        this.status = status;
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

    public String getStatus() {
        return status;
    }
}
