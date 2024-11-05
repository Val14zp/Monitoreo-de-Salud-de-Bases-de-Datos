import java.util.Date;

public class Alert {
    private String severity;
    private Date timestamp;
    private String description;

    public Alert(String severity, Date timestamp, String description) {
        this.severity = severity;
        this.timestamp = timestamp;
        this.description = description;
    }

    public String getSeverity() {
        return severity;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "[" + severity + "] " + timestamp + ": " + description;
    }
}
