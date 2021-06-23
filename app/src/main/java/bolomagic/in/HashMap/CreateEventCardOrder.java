package bolomagic.in.HashMap;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class CreateEventCardOrder {

    public String eventID;
    public String cardID;
    public String prize;
    public String discount;
    public String cardCount;
    public String status;
    public String orderDate;
    public String deliveryDate;

    public CreateEventCardOrder(String eventID, String cardID, String prize, String discount, String cardCount, String status, String orderDate, String deliveryDate) {
        this.eventID = eventID;
        this.cardID = cardID;
        this.prize = prize;
        this.discount = discount;
        this.cardCount = cardCount;
        this.status = status;
        this.orderDate = orderDate;
        this.deliveryDate = deliveryDate;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("Event ID", eventID);
        result.put("Card ID", cardID);
        result.put("Prize", prize);
        result.put("Discount", discount);
        result.put("Card Count", cardCount);
        result.put("Status", status);
        result.put("Order Date", orderDate);
        result.put("Delivery Date", deliveryDate);
        return result;
    }
}