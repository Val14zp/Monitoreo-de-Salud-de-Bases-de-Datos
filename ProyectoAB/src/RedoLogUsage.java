public class RedoLogUsage {
    private int group;
    private int sequence;
    private String archived;
    private String status;

    public RedoLogUsage(int group, int sequence, String archived, String status) {
        this.group = group;
        this.sequence = sequence;
        this.archived = archived;
        this.status = status;
    }

    public int getGroup() {
        return group;
    }

    public int getSequence() {
        return sequence;
    }

    public String getArchived() {
        return archived;
    }

    public String getStatus() {
        return status;
    }
}
