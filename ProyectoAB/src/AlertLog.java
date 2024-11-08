public class AlertLog {
    private String date;
    private String description;
    private int count;

    public AlertLog(String date, String description, int count) {
        this.date = date;
        this.description = description;
        this.count = count;
    }

    public String getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public int getCount() {
        return count;
    }
}
