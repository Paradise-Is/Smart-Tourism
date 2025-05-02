package com.example.smarttourism.entity;

public class SaleItem {
    //景点名
    private String sight_name;
    //景点销售额
    private String sight_sale;

    public SaleItem(String sight_name, String sight_sale) {
        this.sight_name = sight_name;
        this.sight_sale = sight_sale;
    }

    public String getSight_name() {
        return sight_name;
    }

    public String getSight_sale() {
        return sight_sale;
    }
}
