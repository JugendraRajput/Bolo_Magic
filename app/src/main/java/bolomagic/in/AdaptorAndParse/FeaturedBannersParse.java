package bolomagic.in.AdaptorAndParse;

public class FeaturedBannersParse {

    private final String imageURL;
    private final String productID;

    public FeaturedBannersParse(String imageURL, String productID) {
        this.imageURL = imageURL;
        this.productID = productID;
    }

    public String getImageURL() {
        return imageURL;
    }

    public String getProductID() {
        return productID;
    }
}
