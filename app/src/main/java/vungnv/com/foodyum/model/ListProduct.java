package vungnv.com.foodyum.model;

import java.util.ArrayList;

public class ListProduct {
    private String nameProduct;
    private ArrayList<Product> list;

    public ListProduct(String nameProduct, ArrayList<Product> list) {
        this.nameProduct = nameProduct;
        this.list = list;
    }

    public String getNameProduct() {
        return nameProduct;
    }

    public void setNameProduct(String nameProduct) {
        this.nameProduct = nameProduct;
    }

    public ArrayList<Product> getList() {
        return list;
    }

    public void setList(ArrayList<Product> list) {
        this.list = list;
    }
}
