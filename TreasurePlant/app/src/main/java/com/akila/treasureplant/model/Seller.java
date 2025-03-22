package com.akila.treasureplant.model;

public class Seller {
    private String id;
    private String name;
    private boolean block;
    private String mobile;
    private String latitude;
    private String longitude;

    public Seller(String id, String name, boolean block, String mobile, String latitude, String longitude) {
        this.id = id;
        this.name = name;
        this.block = block;
        this.mobile = mobile;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isBlock() {
        return block;
    }

    public void setBlock(boolean block) {
        this.block = block;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
}
