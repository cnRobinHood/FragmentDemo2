package com.xiezh.findlost.domain;

import java.io.Serializable;

public class UserInfo implements Serializable {
    private String id;
    private String userID;
    private String userName;
    private String userRealName;

    private int sex;//0为男 1 为女
    private int qq;
    private int phoneNum;

    private String headImag;
    private int state = 0;

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserRealName() {
        return userRealName;
    }

    public void setUserRealName(String userRealName) {
        this.userRealName = userRealName;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public int getQq() {
        return qq;
    }

    public void setQq(int qq) {
        this.qq = qq;
    }

    public int getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(int phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getHeadImag() {
        return headImag;
    }

    public void setHeadImag(String headImag) {
        this.headImag = headImag;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String toString() {
        return "UserInfo{" +
                "userID='" + userID + '\'' +
                ", userName='" + userName + '\'' +
                ", userRealName='" + userRealName + '\'' +
                ", sex=" + sex +
                ", qq=" + qq +
                ", phoneNum=" + phoneNum +
                ", headImag=" + headImag +
                '}';
    }
}

