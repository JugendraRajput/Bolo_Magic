package bolomagic.in.AdaptorAndParse;

public class HomePopUpCardParse {
    private final String cardID;
    private final String imageURL;
    private final String name;
    private String prize;
    private String discount;
    private final String availability;
    private final String maxCount;
    private String cartCount;
    private final int index;

    public HomePopUpCardParse(String cardID, String imageURL, String name, String prize, String discount, String availability, String maxCount, String cartCount, int index) {
        this.cardID = cardID;
        this.imageURL = imageURL;
        this.name = name;
        this.prize = prize;
        this.discount = discount;
        this.availability = availability;
        this.maxCount = maxCount;
        this.cartCount = cartCount;
        this.index = index;
    }

    public void setPrize(String prize) {
        this.prize = prize;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public void setCartCount(String cartCount) {
        this.cartCount = cartCount;
    }

    public String getCardID() {
        return cardID;
    }

    public String getImageURL() {
        return imageURL;
    }

    public String getName() {
        return name;
    }

    public String getPrize() {
        return prize;
    }

    public String getDiscount() {
        return discount;
    }

    public String getAvailability() {
        return availability;
    }

    public String getMaxCount() {
        return maxCount;
    }

    public String getCartCount() {
        return cartCount;
    }

    public int getIndex() {
        return index;
    }
}