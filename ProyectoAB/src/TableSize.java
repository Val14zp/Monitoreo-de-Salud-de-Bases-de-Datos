public class TableSize {
    private String tableName;
    private long rows;
    private double sizeGb;

    public TableSize(String tableName, long rows, double sizeGb) {
        this.tableName = tableName;
        this.rows = rows;
        this.sizeGb = sizeGb;
    }

    public String getTableName() {
        return tableName;
    }

    public long getRows() {
        return rows;
    }

    public double getSizeGb() {
        return sizeGb;
    }

}
