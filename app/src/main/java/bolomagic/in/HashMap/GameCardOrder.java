package bolomagic.in.HashMap;

import java.util.HashMap;
import java.util.Map;

public class GameCardOrder {

    public String id;
    public String quantity;
    public String bonus;
    public String prize;
    public String status;
    public String orderDate;

    public GameCardOrder(String id, String quantity, String bonus, String prize, String status, String orderDate) {
        this.id = id;
        this.quantity = quantity;
        this.bonus = bonus;
        this.prize = prize;
        this.status = status;
        this.orderDate = orderDate;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("Card ID", id);
        result.put("Prize", prize);
        result.put("Quantity", quantity);
        result.put("Bonus", bonus);
        result.put("Status", status);
        result.put("Order Date", orderDate);
        return result;
    }
}