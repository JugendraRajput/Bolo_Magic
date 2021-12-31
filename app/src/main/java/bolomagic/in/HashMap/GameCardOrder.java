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
    public String playerID;
    public String gameName;

    public GameCardOrder(String id, String quantity, String bonus, String prize, String status, String orderDate, String playerID, String gameName) {
        this.id = id;
        this.quantity = quantity;
        this.bonus = bonus;
        this.prize = prize;
        this.status = status;
        this.orderDate = orderDate;
        this.playerID = playerID;
        this.gameName = gameName;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("Card ID", id);
        result.put("Prize", prize);
        result.put("Quantity", quantity);
        result.put("Bonus", bonus);
        result.put("Status", status);
        result.put("Order Date", orderDate);
        result.put("Player ID", playerID);
        result.put("Game Name", gameName);
        return result;
    }
}