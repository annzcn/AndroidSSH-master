package cn.clickwise.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by T420s on 2016/11/8.
 */
public class RouterLocalInfoReturn {

    private String jsonrpc;
    private String state;
    private List<Result> result;

    public RouterLocalInfoReturn() {
    }

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

    public List<Result> getResult() {
        return result;
    }

    public void setResult(List<Result> result) {
        this.result = result;
    }

    public static class Result {
        private String acmac;
        private String routename;
        private String sw_version;
        private String hw_version;
        private String longtitude;
        private String latitude;
        private String online_stats;
        private String last_heartbeat_time;
        private String shopname;
        private String phone;
        private String linker;
        private String aid;
        private String name;
        private String online_time;

        public Result() {
        }

        public String getOnline_time() {
            return online_time;
        }

        public void setOnline_time(String online_time) {
            this.online_time = online_time;
        }

        public String getAcmac() {
            return acmac;
        }

        public void setAcmac(String acmac) {
            this.acmac = acmac;
        }

        public String getRoutename() {
            return routename;
        }

        public void setRoutename(String routename) {
            this.routename = routename;
        }

        public String getSw_version() {
            return sw_version;
        }

        public void setSw_version(String sw_version) {
            this.sw_version = sw_version;
        }

        public String getHw_version() {
            return hw_version;
        }

        public void setHw_version(String hw_version) {
            this.hw_version = hw_version;
        }

        public String getLongtitude() {
            return longtitude;
        }

        public void setLongtitude(String longtitude) {
            this.longtitude = longtitude;
        }

        public String getLatitude() {
            return latitude;
        }

        public void setLatitude(String latitude) {
            this.latitude = latitude;
        }

        public String getOnline_stats() {
            return online_stats;
        }

        public void setOnline_stats(String online_stats) {
            this.online_stats = online_stats;
        }

        public String getLast_heartbeat_time() {
            return last_heartbeat_time;
        }

        public void setLast_heartbeat_time(String last_heartbeat_time) {
            this.last_heartbeat_time = last_heartbeat_time;
        }

        public String getShopname() {
            return shopname;
        }

        public void setShopname(String shopname) {
            this.shopname = shopname;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getLinker() {
            return linker;
        }

        public void setLinker(String linker) {
            this.linker = linker;
        }

        public String getAid() {
            return aid;
        }

        public void setAid(String aid) {
            this.aid = aid;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
