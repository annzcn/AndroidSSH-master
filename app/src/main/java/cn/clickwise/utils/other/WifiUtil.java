package cn.clickwise.utils.other;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.design.widget.AppBarLayout;

import java.util.List;

import cn.clickwise.R;
import cn.clickwise.views.ProgressView;

/**
 * Created by T420s on 2016/10/19.
 */
public class WifiUtil {
    public static int WifiLinkMaxSpeed = 72;//Mbps

    /**
     * 打开wifi
     *
     * @param wifiManager
     */
    public static void wifiOpen(WifiManager wifiManager) {
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
    }

    /**
     * 扫描wifi
     *
     * @param wifiManager
     */
    public static void wifiStartScan(WifiManager wifiManager) {
        wifiManager.startScan();
    }

    /**
     * 获取扫描结果
     *
     * @param wifiManage
     * @return
     */
    public static List<ScanResult> getScanResults(WifiManager wifiManage) {
        return wifiManage.getScanResults();
    }

    /**
     * 得到wifi配置好的信息
     *
     * @param wifiManage
     * @return
     */
    public static List<WifiConfiguration> getConfigureation(WifiManager wifiManage) {
        return wifiManage.getConfiguredNetworks();
    }

    /**
     * 获取连接的wifi信息
     *
     * @param wifiManager
     * @return
     */
    public static WifiInfo getWifiInfo(WifiManager wifiManager) {
        return wifiManager.getConnectionInfo();
    }

    /**
     * 得到wifi管理器
     *
     * @param context
     * @return
     */
    public static WifiManager getWifiManager(Context context) {
        return (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    }

    /**
     * 获取手机的IP地址
     *
     * @param context
     * @return
     */
    public static String getPhoneIpAddress(Context context) {
        String phoneIpAddress = null;
        WifiInfo wifiInfo = getWifiInfo(getWifiManager(context));
        if (wifiInfo != null) {
            phoneIpAddress = intToIpAddress(wifiInfo.getIpAddress());
        }
        return phoneIpAddress;
    }

    public static String intToIpAddress(int ipInt) {
        StringBuffer sb = new StringBuffer();
        sb.append(ipInt & 0xFF).append(".");
        sb.append((ipInt >> 8) & 0xFF).append(".");
        sb.append((ipInt >> 16) & 0xFF).append(".");
        sb.append((ipInt >> 24) & 0xFF);
        return sb.toString();
    }

    /**
     * 获取路由器的Mac地址
     *
     * @param context
     * @return
     */
    public static String getConnectedWifiMacAddress(Context context) {
        String connectedWifiMacAddress = null;
        WifiInfo wifiInfo = getWifiInfo(getWifiManager(context));
        if (wifiInfo != null) {
            connectedWifiMacAddress = wifiInfo.getBSSID();
        }
        return connectedWifiMacAddress;
    }

    /**
     * 获取路由器的SSID（名称）
     *
     * @param context
     * @return
     */
    public static String getWifiName(Context context) {
        String wifiName = null;
        WifiInfo wifiInfo = getWifiInfo(getWifiManager(context));
        if (wifiInfo != null) {
            wifiName = wifiInfo.getSSID();
            if (wifiName.contains("\"")) {
                wifiName = wifiName.substring(1, wifiName.length() - 1);
            }
        }
        return wifiName;
    }

    /**
     * 获取已连接路由器的Ip网关（网关地址）
     *
     * @return
     */
    public static String getWifiIpAddress(Context context) {
        DhcpInfo dhcpInfo = getWifiManager(context).getDhcpInfo();
        long gateway = dhcpInfo.gateway;
        return long2ip(gateway);
    }

    private static String long2ip(long ip) {
        StringBuffer sb = new StringBuffer();
        sb.append(String.valueOf((int) (ip & 0xff)));
        sb.append('.');
        sb.append(String.valueOf((int) ((ip >> 8) & 0xff)));
        sb.append('.');
        sb.append(String.valueOf((int) ((ip >> 16) & 0xff)));
        sb.append('.');
        sb.append(String.valueOf((int) ((ip >> 24) & 0xff)));
        return sb.toString();
    }

    /**
     * 获得当前连接Wifi的速度 Mbps
     *
     * @param context
     * @return
     */
    public static int getWifiLinkSpeed(Context context) {
        return getWifiInfo(getWifiManager(context)).getLinkSpeed();
    }

    public static String getWifiLinkSpeedStr(Context context) {
        return getWifiLinkSpeed(context) + getWifiLinkSpeedUnit();
    }

    public static String getWifiLinkSpeedUnit() {
        return WifiInfo.LINK_SPEED_UNITS;
    }

    /**
     * 获取Wifi信号质量
     *
     * @param context
     * @return
     */
    public static int getRssi(Context context) {
        return getWifiInfo(getWifiManager(context)).getRssi();
    }

    public static int getSignalLevel(Context context) {
        return WifiManager.calculateSignalLevel(getRssi(context), 5);
    }

    /**
     * 判断信息等级
     *
     * @param context
     * @return
     */
    public static String getWifiLevel(Context context) {
        int level = getSignalLevel(context);
        String wifiQuality = null;
        switch (level) {
            case 4:
                wifiQuality = "信号最好";
                break;
            case 3:
                wifiQuality = "信号较好";
                break;
            case 2:
                wifiQuality = "信号一般";
                break;
            case 1:
                wifiQuality = "信号较差";
                break;
            case 0:
                wifiQuality = "无信号";
                break;
        }
        return wifiQuality;
    }

    public static int calculateSignalLevel(int rssi, int numLevels) {

        int MIN_RSSI = -100;
        int MAX_RSSI = -55;
        int levels = 101;


        if (rssi <= MIN_RSSI) {
            return 0;
        } else if (rssi >= MAX_RSSI) {
            return levels - 1;
        } else {
            float inputRange = (MAX_RSSI - MIN_RSSI);
            float outputRange = (levels - 1);
            return (int) ((float) (rssi - MIN_RSSI) * outputRange / inputRange);
        }
    }

    /**
     * 打开或关闭Wifi  true为打开 false为关闭
     *
     * @param operate
     * @param context
     */
    public static void operateWifi(boolean operate, Context context) {
        WifiManager wifiManager = getWifiManager(context);
        if (operate && !wifiManager.isWifiEnabled()) {//打开Wifi
            wifiManager.setWifiEnabled(operate);
        } else if (!operate && wifiManager.isWifiEnabled()) {//关闭Wifi
            wifiManager.setWifiEnabled(operate);
        }
    }
}
