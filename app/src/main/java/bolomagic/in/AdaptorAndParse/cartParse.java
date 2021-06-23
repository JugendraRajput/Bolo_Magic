package bolomagic.in.AdaptorAndParse;

public class cartParse {
    private final String cardID;
    private final String imageURL;
    private final String prize;
    private final String cashBack;
    private final String effectivePrize;
    private final String validity;

    public cartParse(String cardID, String imageURL, String prize, String cashBack, String effectivePrize, String validity) {
        this.cardID = cardID;
        this.imageURL = imageURL;
        this.prize = prize;
        this.cashBack = cashBack;
        this.effectivePrize = effectivePrize;
        this.validity = validity;
    }

    public String getCardID() {
        return cardID;
    }

    public String getImageURL() {
        return imageURL;
    }

    public String getPrize() {
        return prize;
    }

    public String getCashBack() {
        return cashBack;
    }

    public String getEffectivePrize() {
        return effectivePrize;
    }

    public String getValidity() {
        return validity;
    }
}