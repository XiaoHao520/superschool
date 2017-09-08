package com.superschool.entity;

/**
 * Created by XIAOHAO on 2017/5/26.
 */

public class BossInfo {
    private String bossUserID;
    private String storeName;
    private String bossIdType;
    private String bossId;
    private String storeLatitude;
    private String storeLongitude;
    private String school;
    private String storeType;
    private String storeInfo;
    private String storeID;
    private String storeBossIdType;


    public String getStoreBossIdType() {
        return storeBossIdType;
    }

    public void setStoreBossIdType(String storeBossIdType) {
        this.storeBossIdType = storeBossIdType;
    }

    public String getStoreID() {
        return storeID;
    }

    public void setStoreID(String storeID) {
        this.storeID = storeID;
    }

    public String getBossUserID() {
        return bossUserID;
    }

    public void setBossUserID(String bossUserID) {
        this.bossUserID = bossUserID;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getBossIdType() {
        return bossIdType;
    }

    public void setBossIdType(String bossIdType) {
        this.bossIdType = bossIdType;
    }

    public String getBossId() {
        return bossId;
    }

    public void setBossId(String bossId) {
        this.bossId = bossId;
    }

    public String getStoreLatitude() {
        return storeLatitude;
    }

    public void setStoreLatitude(String storeLatitude) {
        this.storeLatitude = storeLatitude;
    }

    public String getStoreLongitude() {
        return storeLongitude;
    }

    public void setStoreLongitude(String storeLongitude) {
        this.storeLongitude = storeLongitude;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getStoreType() {
        return storeType;
    }

    public void setStoreType(String storeType) {
        this.storeType = storeType;
    }

    public String getStoreInfo() {
        return storeInfo;
    }

    public void setStoreInfo(String storeInfo) {
        this.storeInfo = storeInfo;
    }

    @Override
    public String toString() {
        return "BossInfo{" +
                "bossUserID='" + bossUserID + '\'' +
                ", storeName='" + storeName + '\'' +
                ", bossIdType='" + bossIdType + '\'' +
                ", bossId='" + bossId + '\'' +
                ", storeLatitude='" + storeLatitude + '\'' +
                ", storeLongitude='" + storeLongitude + '\'' +
                ", school='" + school + '\'' +
                ", storeType='" + storeType + '\'' +
                ", storeInfo='" + storeInfo + '\'' +
                '}';
    }
}
