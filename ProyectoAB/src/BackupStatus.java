import java.util.Date;

public class BackupStatus {
    private Date lastBackupDate;
    private double totalBackupSizeMB;  // Total size in MB

    public BackupStatus(Date lastBackupDate, double totalBackupSizeMB) {
        this.lastBackupDate = lastBackupDate;
        this.totalBackupSizeMB = totalBackupSizeMB;
    }

    public Date getLastBackupDate() {
        return lastBackupDate;
    }

    public double getTotalBackupSizeMB() {
        return totalBackupSizeMB;
    }
}
