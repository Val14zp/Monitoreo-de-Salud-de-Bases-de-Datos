public class DiskIOUsage {
    private int instanceId;
    private double readMb;
    private double writeMb;
    private String status; // Agregado para manejar el estado

    public DiskIOUsage(int instanceId, double readMb, double writeMb, String status) {
        this.instanceId = instanceId;
        this.readMb = readMb;
        this.writeMb = writeMb;
        this.status = status;
    }

    public int getInstanceId() {
        return instanceId;
    }

    public double getReadMb() {
        return readMb;
    }

    public double getWriteMb() {
        return writeMb;
    }

    public String getStatus() {
        return status;
    }
}
