package bolomagic.in.AdaptorAndParse;

public class CheckInParse {

    private String imageURL;

    private final String day;

    public CheckInParse(String imageURL, String day) {
        this.imageURL = imageURL;
        this.day = day;
    }

    public String getImageURL() {
        return imageURL;
    }

    public String getDay() {
        return day;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }
}