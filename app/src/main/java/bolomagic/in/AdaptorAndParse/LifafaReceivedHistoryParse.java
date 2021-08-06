package bolomagic.in.AdaptorAndParse;

public class LifafaReceivedHistoryParse {
    private final String ID;
    private final String senderProfilePic;
    private final String senderName;
    private final String message;
    private final String date;

    public LifafaReceivedHistoryParse(String ID, String senderProfilePic, String senderName, String message, String date) {
        this.ID = ID;
        this.senderProfilePic = senderProfilePic;
        this.senderName = senderName;
        this.message = message;
        this.date = date;
    }

    public String getID() {
        return ID;
    }

    public String getSenderProfilePic() {
        return senderProfilePic;
    }

    public String getSenderName() {
        return senderName;
    }

    public String getMessage() {
        return message;
    }

    public String getDate() {
        return date;
    }
}