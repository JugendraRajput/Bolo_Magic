package bolomagic.in.AdaptorAndParse;

public class LifafaCreatedHistoryParse {
    private final String ID;
    private final String receiverName;
    private final String creationTime;
    private final String totalBalance;
    private final String availableAmount;
    private final String status;
    private final String link;

    public LifafaCreatedHistoryParse(String ID, String receiverName, String creationTime, String totalBalance, String availableAmount, String status, String link) {
        this.ID = ID;
        this.receiverName = receiverName;
        this.creationTime = creationTime;
        this.totalBalance = totalBalance;
        this.availableAmount = availableAmount;
        this.status = status;
        this.link = link;
    }

    public String getID() {
        return ID;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public String getCreationTime() {
        return creationTime;
    }

    public String getTotalBalance() {
        return totalBalance;
    }

    public String getAvailableAmount() {
        return availableAmount;
    }

    public String getStatus() {
        return status;
    }

    public String getLink() {
        return link;
    }
}