package bolomagic.in.AdaptorAndParse;

public class LifafaCreatedHistoryParse {
    private final String ID;
    private final String imageURL;
    private final String count;
    private final String message;
    private final String totalAmount;

    public LifafaCreatedHistoryParse(String ID, String imageURL, String count, String message, String totalAmount) {
        this.ID = ID;
        this.imageURL = imageURL;
        this.count = count;
        this.message = message;
        this.totalAmount = totalAmount;
    }

    public String getID() {
        return ID;
    }

    public String getImageURL() {
        return imageURL;
    }

    public String getCount() {
        return count;
    }

    public String getMessage() {
        return message;
    }

    public String getTotalAmount() {
        return totalAmount;
    }
}