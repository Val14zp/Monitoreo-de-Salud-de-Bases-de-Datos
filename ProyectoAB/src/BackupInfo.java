public class BackupInfo {
    private String lastBackupDate;
    private Double totalBackupSizeGb;

    public BackupInfo(String lastBackupDate, Double totalBackupSizeGb) {
        this.lastBackupDate = lastBackupDate;
        this.totalBackupSizeGb = totalBackupSizeGb;
    }

    public String getLastBackupDate() {
        return lastBackupDate;
    }

    public Double getTotalBackupSizeGb() {
        return totalBackupSizeGb;
    }
}
