package com.akila.treasureplant.model;

public class OrderHistoryItem {
    private String id;
    private String title;
    private String price;
    private String quantity;
    private int review;

    public OrderHistoryItem(String id, String title, String price, String quantity, int review) {
        this.id = id;
        this.title = title;
        this.price = price;
        this.quantity = quantity;
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

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public int getReview() {
        return review;
    }

    public void setReview(int review) {
        this.review = review;
    }
}
