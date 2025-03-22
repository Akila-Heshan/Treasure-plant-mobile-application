package com.akila.treasureplant.model;

public class ProductItem {
    private String id;
    private String title;
    private String price;
    private String sold;
    private int review;


    public ProductItem(String id, String title, String price, String sold, int review) {
        this.id = id;
        this.title = title;
        this.price = price;
        this.sold = sold;
        this.review = review;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getSold() {
        return sold;
    }

    public void setSold(String sold) {
        this.sold = sold;
    }

    public int getReview() {
        return review;
    }

    public void setReview(int review) {
        this.review = review;
    }
}
