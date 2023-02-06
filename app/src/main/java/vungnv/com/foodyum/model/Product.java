package vungnv.com.foodyum.model;

public class Product {
    public int stt;
    public String idUser;
    public String type;
    public String img;
    public String name;
    public String description;
    public String timeDelay;
    public Double price;
    public Double discount;
    public Double rate;
    public int favourite;
    public int check;
    public int status;
    public String address;
    public String feedBack;
    public int quantity_sold;
    public int quantityTotal;

    public Product() {
    }

    public Product(int stt, String idUser, String type, String img, String name,
                   String description, String timeDelay, Double price, Double rate,
                   int favourite, int check, int status, String address, String feedBack,
                   int quantity_sold, int quantityTotal) {
        this.stt = stt;
        this.idUser = idUser;
        this.type = type;
        this.img = img;
        this.name = name;
        this.description = description;
        this.timeDelay = timeDelay;
        this.price = price;
        this.rate = rate;
        this.favourite = favourite;
        this.check = check;
        this.status = status;
        this.address = address;
        this.feedBack = feedBack;
        this.quantity_sold = quantity_sold;
        this.quantityTotal = quantityTotal;
    }
}
