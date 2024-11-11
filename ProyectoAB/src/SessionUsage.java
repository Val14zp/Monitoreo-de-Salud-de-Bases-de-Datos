public class SessionUsage {
    private int activeConnections;
    private int concurrentSessions;

    public SessionUsage(int activeConnections, int concurrentSessions, String status) {
        this.activeConnections = activeConnections;
        this.concurrentSessions = concurrentSessions;
    }

    public int getActiveConnections() {
        return activeConnections;
    }

    public int getConcurrentSessions() {
        return concurrentSessions;
    }

}
