package cn.clickwise.utils.other;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;

import cn.clickwise.config.RequestUrl;
import cn.clickwise.interf.ICheckNet;
import cn.clickwise.utils.helper.RouteTestHelper;

/**
 * Created by T420s on 2016/10/24.
 */
public class NetWorkUtil {
    private static ConnectivityManager ConnManager;

    /**
     * ping 网址判断网络是否可用
     *
     * @return
     */
    public static void isNetWork(final ICheckNet iCheckNet) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //必须加空格，否则就连在一起了 eg ping -c 1www.baidu.com
                    Process process = Runtime.getRuntime().exec("ping -c 1 -w 1 " + RequestUrl.URL_BAIDU);
                    int state = process.waitFor();
                    /*BufferedReader reader = new BufferedReader(new InputStreamReader(
                            process.getInputStream()));
                    StringBuffer output = new StringBuffer();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        output.append(line + "\r\n");
                    }
                    String strReturn = output.toString();
                    ToastUtil.make(strReturn,0);
                    Log.d("rrr", "run: strReturn-->" + strReturn);*/
                    if (state == 0) {//表示网络可用
                        iCheckNet.available();
                    } else {
                        iCheckNet.notAvailable();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 此方法判断不准确
     *
     * @param context
     * @return
     */
    public static boolean isNetWorks(Context context) {
        boolean flag = false;
        //得到网络连接信息
        NetworkInfo activeNetworkInfo = getAvailableNetworkInfo(getConnManager(context));
        //去进行判断网络是否连接
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
            if (activeNetworkInfo.getState() == NetworkInfo.State.CONNECTED) {
                flag = true;
            }
        }
        return flag;
    }

    /**
     * 检测Wifi是否可用
     *
     * @param context
     * @return
     */
    public static boolean isWifiNetWork(Context context) {
        boolean isWifiNetWork = false;
        NetworkInfo availableNetworkInfo = getAvailableNetworkInfo(getConnManager(context));
        if (availableNetworkInfo != null && availableNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            isWifiNetWork = true;
        }
        return isWifiNetWork;
    }

    /**
     * 检测GPRS是否可用
     *
     * @param context
     * @return
     */
    public static boolean isMobileNetWork(Context context) {
        boolean isMobileNetWork = false;
        NetworkInfo availableNetworkInfo = getAvailableNetworkInfo(getConnManager(context));
        if (availableNetworkInfo != null && availableNetworkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
            isMobileNetWork = true;
        }
        return isMobileNetWork;
    }

    /**
     * 获得网络连接信息
     *
     * @param manager
     * @return
     */
    private static NetworkInfo getAvailableNetworkInfo(ConnectivityManager manager) {
        if (manager != null) {
            return manager.getActiveNetworkInfo();
        }
        return null;
    }

    /**
     * 获取ConnManager
     *
     * @param context
     * @return
     */
    private static ConnectivityManager getConnManager(Context context) {
        if (ConnManager == null) {
            ConnManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        }
        return ConnManager;
    }

    /**
     * 判断Wifi是否连接
     *
     * @param context
     * @return
     */
    public static boolean isWifiConn(Context context) {
        boolean isWifiConn = false;
        //得到网络连接信息
        NetworkInfo activeNetworkInfo = getAvailableNetworkInfo(getConnManager(context));
        if (activeNetworkInfo != null && activeNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI && activeNetworkInfo.isAvailable()) {
            isWifiConn = true;
        }
        return isWifiConn;
    }

    /**
     * 判断是否在Wifi状态下且Wifi是否联通
     *
     * @param context
     * @return
     */
    /*public static boolean isOnlyWifiConn(Context context) {
        boolean isOnlyWifiConn = false;
        if (isWifiState(context)) {
            NetworkInfo.State wifiState = getConnManager(context).getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
            if (wifiState == NetworkInfo.State.CONNECTED) {
                isOnlyWifiConn = true;
            }
        }
        returns isOnlyWifiConn;
    }*/

    /**
     * 打开或关闭GPRS数据流量 true 代表打开 false代表关闭
     * 应用了反射技术
     *
     * @param operate
     * @param context
     */
    public static void operateGprs(boolean operate, Context context) {
        ConnectivityManager connManager = getConnManager(context);
        Class<? extends ConnectivityManager> aClass = connManager.getClass();
        Class<?>[] argClass = new Class[1];
        argClass[0] = boolean.class;

        try {
            Method method = aClass.getMethod("setMobileDataEnabled", argClass);
            method.invoke(connManager, operate);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
