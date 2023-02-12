package vungnv.com.foodyum.model;

import java.util.HashMap;

public class Order {
    public int stt;
    public String id;
    public String idUser;
    public String idMerchant;
    public String dateTime;
    public String items;
    public int quantity;
    public int status;
    public double price;
    public String notes;

    public Order() {
    }

    public Order(String id, String idUser,String dateTime, String items,int quantity, int status, double price, String notes) {
        this.id = id;
        this.idUser = idUser;
        this.dateTime = dateTime;
        this.items = items;
        this.quantity = quantity;
        this.status = status;
        this.price = price;
        this.notes = notes;
    }

    public Order(String idOrder, String idUser, String idMerchant, String toString, String name, int quantity, int i, Double price, String notes) {
        this.id = idOrder;
        this.idUser = idUser;
        this.dateTime = dateTime;
        this.items = items;
        this.quantity = quantity;
        this.price = price;
        this.notes = notes;
    }

    public HashMap<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("idUser", idUser);
        result.put("dateTime", dateTime);
        result.put("items", items);
        result.put("quantity",quantity);
        result.put("status", status);
        result.put("price", price);
        result.put("notes", notes);

        return result;
    }
}
