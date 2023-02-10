package vungnv.com.foodyum.model;

public class Coupon {
    public int stt;
    public String id;
    public String img;
    public Double discount;
    public String expiry;

    public Coupon(int stt, String id, String img, Double discount, String expiry) {
        this.stt = stt;
        this.id = id;
        this.img = img;
        this.discount = discount;
        this.expiry = expiry;
    }

    public Coupon() {
    }

}
