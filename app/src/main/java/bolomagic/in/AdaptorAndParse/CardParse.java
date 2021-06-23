package bolomagic.in.AdaptorAndParse;

public class CardParse {
    private final String cardID;
    private final String imageURL;
    private final String prize;
    private final String cashBack;
    private final String effectivePrize;
    private final String validity;
    private final String buttonText;

    public CardParse(String cardID, String imageURL, String prize, String cashBack, String effectivePrize, String validity, String buttonText) {
        this.cardID = cardID;
        this.imageURL = imageURL;
        this.prize = prize;
        this.cashBack = cashBack;
        this.effectivePrize = effectivePrize;
        this.validity = validity;
        this.buttonText = buttonText;
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

    public String getButtonText() {
        return buttonText;
    }
}