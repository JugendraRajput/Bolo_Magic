package bolomagic.in.AdaptorAndParse;

public class LifafaReceivedHistoryParse {
    private final String ID;
    private final String receiverName;
    private final String receivedTime;
    private final String won;
    private final String status;

    public LifafaReceivedHistoryParse(String ID, String receiverName, String receivedTime, String won, String status) {
        this.ID = ID;
        this.receiverName = receiverName;
        this.receivedTime = receivedTime;
        this.won = won;
        this.status = status;
    }

    public String getID() {
        return ID;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public String getReceivedTime() {
        return receivedTime;
    }

    public String getWon() {
        return won;
    }

    public String getStatus() {
        return status;
    }
}