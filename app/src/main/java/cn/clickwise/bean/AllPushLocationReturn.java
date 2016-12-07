package cn.clickwise.bean;

import com.google.gson.Gson;

import java.util.List;

/**
 * Created by lvyang on 2016/11/29.
 */
public class AllPushLocationReturn {

    /**
     * jsonrpc : 2.0
     * state : success
     * result : [{"aid":"47","date":"2016-11-30","time":"9:34:22","latitude":"111.111","longitude":"40.222","province":"北京市","city":"北京市","district":"海淀区","describle":"蓝靛厂路88号"}]
     */

    private String jsonrpc;
    private String state;
    private List<ResultBean> result;

    public String getJsonrpc() {
        return jsonrpc;
    }

    public void setJsonrpc(String jsonrpc) {
        this.jsonrpc = jsonrpc;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public List<ResultBean> getResult() {
        return result;
    }

    public void setResult(List<ResultBean> result) {
        this.result = result;
    }

    public static class ResultBean {
        /**
         * aid : 47
         * date : 2016-11-30
         * time : 9:34:22
         * latitude : 111.111
         * longitude : 40.222
         * province : 北京市
         * city : 北京市
         * district : 海淀区
         * describle : 蓝靛厂路88号
         */

        private String aid;
        private String date;
        private String time;
        private String latitude;
        private String longitude;
        private String province;
        private String city;
        private String district;
        private String describle;
        private String street;
        private String addrStr;
        private String phone;
        private String name;

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

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

        public static ResultBean objectFromData(String str) {

            return new Gson().fromJson(str, ResultBean.class);
        }

        public String getAid() {
            return aid;
        }

        public void setAid(String aid) {
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
}
