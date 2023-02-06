package vungnv.com.foodyum.model;

public class ItemCart {
    public int stt;
    public String id;
    public String name;
    public String idUser;
    public String dateTime;
    public int status;
    public int quantity;
    public Double price;

    public ItemCart() {
    }

    public ItemCart(int stt, String id, String name, String idUser, String dateTime, int status, int quantity, Double price) {
        this.stt = stt;
        this.id = id;
        this.name = name;
        this.idUser = idUser;
        this.dateTime = dateTime;
        this.status = status;
        this.quantity = quantity;
        this.price = price;
    }
}
