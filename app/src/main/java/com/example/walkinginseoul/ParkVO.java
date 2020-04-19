package com.example.walkinginseoul;

import android.widget.ImageView;

public class ParkVO {
    protected int img;
    protected String title;
    protected String address;

    public ParkVO() {}

    public ParkVO(int img, String title, String address) {
        this.img = img;
        this.title = title;
        this.address = address;
    }

    public int getImg() {
        return img;
    }

    public void setImg(int img) {
        this.img = img;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
