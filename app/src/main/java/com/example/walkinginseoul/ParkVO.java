package com.example.walkinginseoul;

import android.widget.ImageView;

public class ParkVO {
    protected String img;
    protected String img2;
    protected String title;
    protected String address;

    public ParkVO() {}

    public ParkVO(String img, String img2, String title, String address) {
        this.img = img;
        this.img2 = img2;
        this.title = title;
        this.address = address;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getImg2() {
        return img2;
    }

    public void setImg2(String img2) {
        this.img2 = img2;
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
