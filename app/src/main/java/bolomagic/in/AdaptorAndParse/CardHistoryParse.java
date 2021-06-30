package bolomagic.in.AdaptorAndParse;

public class CardHistoryParse {
    private final String imageURL;
    private final String prize;
    private final String status;
    private final String date;
    private final String code;

    public CardHistoryParse(String imageURL, String prize, String status, String date, String code) {
        this.imageURL = imageURL;
        this.prize = prize;
        this.status = status;
        this.date = date;
        this.code = code;
    }

    public String getImageURL() {
        return imageURL;
    }

    public String getPrize() {
        return prize;
    }

    public String getStatus() {
        return status;
    }

    public String getDate() {
        return date;
    }

    public String getCode() {
        return code;
    }
}