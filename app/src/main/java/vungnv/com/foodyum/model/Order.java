package vungnv.com.foodyum.model;

import java.util.HashMap;

public class Order {
    public int pos;
    public int posByUserClient;
    public String id;
    public String idUser;
    public String idMerchant;
    public String dateTime;
    public String items;
    public String quantity;
    public int status;
    public String price;
    public int waitingTime;
    public String notes;

    public Order() {
    }
    public Order(int pos,  String id, String idUser, String idMerchant, String dateTime, String items,String quantity,
                 int status, String price, int waitingTime, String notes) {
        this.pos = pos;
        this.id = id;
        this.idUser = idUser;
        this.idMerchant  = idMerchant;
        this.dateTime = dateTime;
        this.items = items;
        this.quantity = quantity;
        this.status = status;
        this.price = price;
        this.waitingTime = waitingTime;
        this.notes = notes;
    }

    public Order(int pos, int posByUserClient, String id, String idUser, String idMerchant, String dateTime, String items,String quantity,
                 int status, String price, int waitingTime, String notes) {
        this.pos = pos;
        this.posByUserClient = posByUserClient;
        this.id = id;
        this.idUser = idUser;
        this.idMerchant  = idMerchant;
        this.dateTime = dateTime;
        this.items = items;
        this.quantity = quantity;
        this.status = status;
        this.price = price;
        this.waitingTime = waitingTime;
        this.notes = notes;
    }


    public HashMap<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("pos", pos);
        result.put("posByUserClient", posByUserClient);
        result.put("id", id);
        result.put("idUser", idUser);
        result.put("idMerchant", idMerchant);
        result.put("dateTime", dateTime);
        result.put("items", items);
        result.put("quantity",quantity);
        result.put("status", status);
        result.put("price", price);
        result.put("waitingTime", waitingTime);
        result.put("notes", notes);

        return result;
    }
}
