package vungnv.com.foodyum.model;

public class ItemCart {
    public int stt;
    public String id;
    public String name;
    public String idUser;
    public String idMerchant;
    public String dateTime;
    public int status;
    public String quantity;
    public Double price;
    public String notes;

    public ItemCart() {
    }

    public ItemCart(int stt, String id, String name, String idUser, String idMerchant,String dateTime, int status, String quantity, double price, String notes) {
        this.stt = stt;
        this.id = id;
        this.name = name;
        this.idUser = idUser;
        this.idMerchant = idMerchant;
        this.dateTime = dateTime;
        this.status = status;
        this.quantity = quantity;
        this.price = price;
        this.notes = notes;
    }
}
