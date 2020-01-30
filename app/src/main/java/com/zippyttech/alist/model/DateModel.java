package com.zippyttech.alist.model;

import com.zippyttech.alist.common.Item;

public class DateModel implements Item {
    private int id, date;
    private String StringDate;

    public DateModel() {
    }

    public DateModel(int date, String stringDate) {
        this.date = date;
        StringDate = stringDate;
    }

    public DateModel(int id, int date, String stringDate) {
        this.id = id;
        this.date = date;
        StringDate = stringDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public String getStringDate() {
        return StringDate;
    }

    public void setStringDate(String stringDate) {
        StringDate = stringDate;
    }

    @Override
    public int getViewType() {
        return 2;
    }
}
