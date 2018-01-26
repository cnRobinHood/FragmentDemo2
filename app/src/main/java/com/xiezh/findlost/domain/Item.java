package com.xiezh.findlost.domain;

import android.support.annotation.NonNull;

import java.io.Serializable;

/**
 * Created by xiezh on 2017/10/31.
 */

public class Item implements Serializable, Comparable {
    private int itemID;
    private String createByID; //创建者的id
    private String createUserHeadImage;//创建者的头像
    private String userName;

    private String remark;
    private String iamge_id; //以,分割，写的是路径下的名字
    private String date;
    private int status = 0;//item的状态0还在1不在了

    public Item() {

    }

    public Item(String createByID, String createUserHeadImage, String userName, String remark, String iamge_id, String date) {
        this.createByID = createByID;
        this.createUserHeadImage = createUserHeadImage;
        this.userName = userName;
        this.remark = remark;
        this.iamge_id = iamge_id;
        this.date = date;
    }

    public int getItemID() {
        return itemID;
    }

    public void setItemID(int itemID) {
        this.itemID = itemID;
    }

    public String getCreateByID() {
        return createByID;
    }

    public void setCreateByID(String createByID) {
        this.createByID = createByID;
    }

    public String getCreateUserHeadImage() {
        return createUserHeadImage;
    }

    public void setCreateUserHeadImage(String createUserHeadImage) {
        this.createUserHeadImage = createUserHeadImage;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getIamge_id() {
        return iamge_id;
    }

    public void setIamge_id(String iamge_id) {
        this.iamge_id = iamge_id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public String toString() {
        return "Item{" +
                "itemID=" + itemID +
                ", createByID='" + createByID + '\'' +
                ", createUserHeadImage='" + createUserHeadImage + '\'' +
                ", userName='" + userName + '\'' +
                ", remark='" + remark + '\'' +
                ", iamge_id='" + iamge_id + '\'' +
                ", date='" + date + '\'' +
                '}';
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public int compareTo(@NonNull Object o) {

        Item t1 = this;
        Item t2 = (Item) o;

        if (t1.getItemID() > t2.getItemID()) {
            return -1;
        } else if (t1.getItemID() < t2.getItemID()) {
            return 1;
        } else {
            return 0;
        }


    }
}
