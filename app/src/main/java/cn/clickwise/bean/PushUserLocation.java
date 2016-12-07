package cn.clickwise.bean;

import com.google.gson.Gson;

/**
 * Created by lvyang on 2016/11/29.
 */
public class PushUserLocation {

    /**
     * aid : 49
     * date : 2016-11-30
     * time : 9:34:22
     * latitude : 111.111
     * longitude : 40.222
     * province : 北京市
     * city : 北京市
     * district : 海淀区
     * describle : 蓝靛厂路88号
     */

    private int aid;
    private String date;
    private String time;
    private double latitude;
    private double longitude;
    private String province;
    private String city;
    private String district;
    private String describle;
    private String street;
    private String addrStr;

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getAddrStr() {
        return addrStr;
    }

    public void setAddrStr(String addrStr) {
        this.addrStr = addrStr;
    }

    public static PushUserLocation objectFromData(String str) {

        return new Gson().fromJson(str, PushUserLocation.class);
    }

    public int getAid() {
        return aid;
    }

    public void setAid(int aid) {
        this.aid = aid;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getDescrible() {
        return describle;
    }

    public void setDescrible(String describle) {
        this.describle = describle;
    }
}
