package com.zippyttech.alist.model;

public class ImageModel {
    int id, idItem;
    private String title;
    private String Image64,tag;

    public ImageModel() {
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdItem() {
        return idItem;
    }

    public void setIdItem(int idItem) {
        this.idItem = idItem;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage64() {
        return Image64;
    }

    public void setImage64(String image64) {
        Image64 = image64;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
