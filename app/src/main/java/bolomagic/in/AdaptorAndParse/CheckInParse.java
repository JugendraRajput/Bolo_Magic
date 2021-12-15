package bolomagic.in.AdaptorAndParse;

public class CheckInParse {

    private final String day;
    private String imageURL;

    public CheckInParse(String imageURL, String day) {
        this.imageURL = imageURL;
        this.day = day;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getDay() {
        return day;
    }
}