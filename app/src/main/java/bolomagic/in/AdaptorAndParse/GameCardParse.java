package bolomagic.in.AdaptorAndParse;

public class GameCardParse {

    String id;
    String offerPercent;
    String prize;
    String quantity;
    String unitType;
    String icon_url;

    public GameCardParse(String id, String offerPercent, String prize, String quantity, String unitType, String icon_url) {
        this.id = id;
        this.offerPercent = offerPercent;
        this.prize = prize;
        this.quantity = quantity;
        this.unitType = unitType;
        this.icon_url = icon_url;
    }

    public String getId() {
        return id;
    }

    public String getOfferPercent() {
        return offerPercent;
    }

    public String getPrize() {
        return prize;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getUnitType() {
        return unitType;
    }

    public String getIcon_url() {
        return icon_url;
    }
}