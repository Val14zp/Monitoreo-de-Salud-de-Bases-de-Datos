import java.sql.Timestamp;

public class BackupUsage {
    private Timestamp lastBackupDate;
    private double backupSizeGb;

    public BackupUsage(Timestamp lastBackupDate, double backupSizeGb) {
        this.lastBackupDate = lastBackupDate;
        this.backupSizeGb = backupSizeGb;
    }

    public Timestamp getLastBackupDate() {
        return lastBackupDate;
    }

    public double getBackupSizeGb() {
        return backupSizeGb;
    }
}
