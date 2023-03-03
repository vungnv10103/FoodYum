package vungnv.com.foodyum.model;

public class ListItemInOrder {
    public String idMerchant;
    public String quantity;
    public String name;
    public String price;
    public String notes;

    public ListItemInOrder() {
    }

    public ListItemInOrder(String idMerchant, String quantity, String name, String price, String notes) {
        this.idMerchant = idMerchant;
        this.quantity = quantity;
        this.name = name;
        this.price = price;
        this.notes = notes;
    }


}
