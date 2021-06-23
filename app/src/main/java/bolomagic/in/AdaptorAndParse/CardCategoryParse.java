package bolomagic.in.AdaptorAndParse;

public class CardCategoryParse {
    private final String categoryName;
    private final String imageURL;
    private final String maxCashBack;
    private final String giftsCount;

    public CardCategoryParse(String categoryName, String imageURL, String maxCashBack, String giftsCount) {
        this.categoryName = categoryName;
        this.imageURL = imageURL;
        this.maxCashBack = maxCashBack;
        this.giftsCount = giftsCount;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public String getImageURL() {
        return imageURL;
    }

    public String getMaxCashBack() {
        return maxCashBack;
    }

    public String getGiftsCount() {
        return giftsCount;
    }
}