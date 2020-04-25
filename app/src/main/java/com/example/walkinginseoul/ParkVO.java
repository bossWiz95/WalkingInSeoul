package com.example.walkinginseoul;

import android.widget.ImageView;

public class ParkVO {
    protected String img;
    protected String title;
    protected String address;
    protected String phone;
    protected String department;
    protected String openDate;
    protected String area;
    protected String detail;
    protected String way;
    protected String latitude;
    protected String longitude;

    // 이미지 : 10 제목 : 2, 주소 : 12, 전화번호 : 13
    // 관리부서 : 16, 개원일 : 5, 면적 : 4, 공원 개요 : 3, 오시는길 : 9
    // 위도 : 15, 경도 : 14

    public ParkVO() {}

    public ParkVO(String img, String title, String address, String phone, String department, String openDate, String area, String detail, String way, String latitude, String longitude) {
        this.img = img;
        this.title = title;
        this.address = address;
        this.phone = phone;
        this.department = department;
        this.openDate = openDate;
        this.area = area;
        this.detail = detail;
        this.way = way;
        this.latitude = latitude;
        this.longitude = longitude;
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

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getOpenDate() {
        return openDate;
    }

    public void setOpenDate(String openDate) {
        this.openDate = openDate;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getWay() {
        return way;
    }

    public void setWay(String way) {
        this.way = way;
    }
}
