package bolomagic.in.AdaptorAndParse;

public class LifafaThemeParse {
    private final String themeID;
    private final String shortImageURL;
    private final String fullImageURL;
    private final String title;
    private String isSelected;

    public LifafaThemeParse(String themeID, String shortImageURL, String fullImageURL, String title, String isSelected) {
        this.themeID = themeID;
        this.shortImageURL = shortImageURL;
        this.fullImageURL = fullImageURL;
        this.title = title;
        this.isSelected = isSelected;
    }

    public String getThemeID() {
        return themeID;
    }

    public String getShortImageURL() {
        return shortImageURL;
    }

    public String getFullImageURL() {
        return fullImageURL;
    }

    public String getTitle() {
        return title;
    }

    public String getIsSelected() {
        return isSelected;
    }

    public void setIsSelected(String isSelected) {
        this.isSelected = isSelected;
    }
}