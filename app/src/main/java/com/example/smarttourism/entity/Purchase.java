package com.example.smarttourism.entity;

public class Purchase {
    private int purchase_id;
    //购买者用户名
    private String purchase_username;
    //景点标识
    private int sight_id;
    //购买数量
    private String quantity;
    //购买单价
    private String price;
    //合计金额
    private String total;
    //购买日期
    private String purchase_date;

    public Purchase(int purchase_id, String purchase_username, int sight_id, String quantity, String price, String total, String purchase_date) {
        this.purchase_id = purchase_id;
        this.purchase_username = purchase_username;
        this.sight_id = sight_id;
        this.quantity = quantity;
        this.price = price;
        this.total = total;
        this.purchase_date = purchase_date;
    }

    public int getPurchase_id() {
        return purchase_id;
    }

    public String getPurchase_username() {
        return purchase_username;
    }

    public int getSight_id() {
        return sight_id;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getPrice() {
        return price;
    }

    public String getTotal() {
        return total;
    }

    public String getPurchase_date() {
        return purchase_date;
    }
}
