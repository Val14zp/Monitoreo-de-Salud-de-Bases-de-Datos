public class DiskIOUsage {
    private int instanceId;
    private double readMb;
    private double writeMb;

    public DiskIOUsage(int instanceId, double readMb, double writeMb) {
        this.instanceId = instanceId;
        this.readMb = readMb;
        this.writeMb = writeMb;
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
}
