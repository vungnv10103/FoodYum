package vungnv.com.foodyum.model;

public class Asset {
    public String total_price_lv1;
    public String total_price_lv2;
    public String transport_fee_lv1;
    public String transport_fee_lv2;
    public String transport_fee_lv3;

    public Asset() {
    }

    public Asset(String total_price_lv1, String total_price_lv2, String transport_fee_lv1, String transport_fee_lv2, String transport_fee_lv3) {
        this.total_price_lv1 = total_price_lv1;
        this.total_price_lv2 = total_price_lv2;
        this.transport_fee_lv1 = transport_fee_lv1;
        this.transport_fee_lv2 = transport_fee_lv2;
        this.transport_fee_lv3 = transport_fee_lv3;
    }
}
