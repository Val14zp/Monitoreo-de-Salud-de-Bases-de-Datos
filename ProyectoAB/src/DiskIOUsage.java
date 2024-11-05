import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DiskIOUsage {
    private double readRate;  // in MB/s or appropriate units
    private double writeRate; // in MB/s or appropriate units

    public DiskIOUsage(double readRate, double writeRate) {
        this.readRate = readRate;
        this.writeRate = writeRate;
    }

    public double getReadRate() {
        return readRate;
    }

    public double getWriteRate() {
        return writeRate;
    }
}
