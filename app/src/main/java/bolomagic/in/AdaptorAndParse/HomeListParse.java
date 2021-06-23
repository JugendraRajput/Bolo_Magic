package bolomagic.in.AdaptorAndParse;

public class HomeListParse {
    private final String eventID;
    private final String image1URL;
    private final String image2URL;
    private final String title;
    private final String message;
    private final String status;
    private final String appIcon;
    private final String appName;
    private final String appID;
    private final String appRating;
    private final String appDiscount;
    private final String appDiscountNewUser;

    public HomeListParse(String eventID, String image1URL, String image2URL, String title, String message, String status, String appIcon, String appName, String appID, String appRating, String appDiscount, String appDiscountNewUser) {
        this.eventID = eventID;
        this.image1URL = image1URL;
        this.image2URL = image2URL;
        this.title = title;
        this.message = message;
        this.status = status;
        this.appIcon = appIcon;
        this.appName = appName;
        this.appID = appID;
        this.appRating = appRating;
        this.appDiscount = appDiscount;
        this.appDiscountNewUser = appDiscountNewUser;
    }

    public String getEventID() {
        return eventID;
    }

    public String getImage1URL() {
        return image1URL;
    }

    public String getImage2URL() {
        return image2URL;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public String getStatus() {
        return status;
    }

    public String getAppIcon() {
        return appIcon;
    }

    public String getAppName() {
        return appName;
    }

    public String getAppID() {
        return appID;
    }

    public String getAppRating() {
        return appRating;
    }

    public String getAppDiscount() {
        return appDiscount;
    }

    public String getAppDiscountNewUser() {
        return appDiscountNewUser;
    }
}