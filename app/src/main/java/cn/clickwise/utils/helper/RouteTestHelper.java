package cn.clickwise.utils.helper;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import cn.clickwise.AppContext;
import cn.clickwise.config.Constants;
import cn.clickwise.utils.other.LogUtil;
import cn.clickwise.utils.other.WifiUtil;
import cn.clickwise.utils.sshutils.ExecTaskCallbackHandler;
import cn.clickwise.utils.sshutils.SessionController;

/**
 * Created by T420s on 2016/10/28.
 */
//路由器测试步骤
//第一步：连接Wifi并输入日志信息
//第二步：读取Wifi信息
//第三步：ping地址
//第四步：登录SSH
//第五步：测试Linux命令
//第六步：ping地址
//第七步：分享到微信
//第八步：测试完毕
//整个流程都保存到日志
public class RouteTestHelper {
    private static String TAG = "RouteTestHelper";
    public static int pingOutputLimit = 10;

    /**
     * 第二步：读取Wifi信息
     *
     * @param context
     * @return
     */
    public static List<String> getWifiInfo_second(Context context) {
        List<String> mWifiInfo = null;
        mWifiInfo = new ArrayList<>();
        mWifiInfo.add("WiFi名称（SSID）：" + WifiUtil.getWifiName(context));
        mWifiInfo.add("WiFi Mac地址：" + WifiUtil.getConnectedWifiMacAddress(context));
        mWifiInfo.add("手机IP地址：" + WifiUtil.getPhoneIpAddress(context));
        mWifiInfo.add("网关IP地址：" + WifiUtil.getWifiIpAddress(context));
        return mWifiInfo;
    }

    //保存到日志
    public static void saveLogInfo(List<String> saveStr) {
        if (saveStr != null && saveStr.size() > 0) {
            int size = saveStr.size();
            for (int i = 0; i < size; i++) {
                AppContext.wifiBuffer.append(saveStr.get(i) + "\r\n");
                //SDHelper.saveFileToSD(SDHelper.getFile(SDHelper.filePath, SDHelper.fileName, SDHelper.fileType), saveStr.get(i) + "\r\n");
            }
        }
    }

    /**
     * 第三步：ping地址
     *
     * @return
     */
    public static List<String> getPingCommandBeforeLogin_third(Context context) {
        List<String> pingCommandBeforeLogin = new ArrayList<>();
        pingCommandBeforeLogin.add(WifiUtil.getWifiIpAddress(context));
        pingCommandBeforeLogin.add("202.112.20.131");
        pingCommandBeforeLogin.add("101.200.211.40");
        pingCommandBeforeLogin.add("www.baidu.com");
        pingCommandBeforeLogin.add("www.sina.com.cn");
        return pingCommandBeforeLogin;
    }

    /**
     * 第四步：登录SSH
     *
     * @return
     */
    public static List<String> getLoginSSHInfo_fourth(Context context) {
        List<String> loginSSHInfo = new ArrayList<>();
        loginSSHInfo.add("root");
        loginSSHInfo.add(WifiUtil.getWifiIpAddress(context));
        loginSSHInfo.add("22");
        loginSSHInfo.add("WifiAdx0penwrt");
        return loginSSHInfo;
    }

    /**
     * 第五步：测试Linux命令
     *
     * @return
     */
    public static List<String> getTestCommand_fifth() {
        List<String> testCommand = new ArrayList<>();
        testCommand.add("top -b -n 1");
        testCommand.add("ps");
        testCommand.add("cat /proc/meminfo");
        testCommand.add("ubus list");
        testCommand.add("uptime");
        testCommand.add("mount");
        testCommand.add("lsmod");
        testCommand.add("df -h");
        testCommand.add("date");
        testCommand.add("uname -a");
        testCommand.add("cat /etc/openwrt_release");
        testCommand.add("dmesg");
        testCommand.add("logread");
        testCommand.add("zcat /etc/backup/logs/log.tar.g");
        testCommand.add("iwinfo");
        testCommand.add("ubus -t 3 call wpa_lua get_wps");
        testCommand.add("w wlan0 info");
        testCommand.add("iw office0 info");
        testCommand.add("iw wlan0 get traffic_control");
        testCommand.add("iw office0 get traffic_control");
        testCommand.add("iw wlan_uplink station dump");
        testCommand.add("cat /var/run/hostapd-phy0.conf");
        testCommand.add("cat /var/run/wpa_supplicant-wlan_uplink.conf");
        testCommand.add("ubus -t 3 call captive get");
        testCommand.add("brctl show");
        testCommand.add("swconfig dev switch0 show");
        testCommand.add("ubus -t 3 call netifd_lua show_switch");
        testCommand.add("ifconfig -a");
        testCommand.add("ip addr list");
        testCommand.add("cat /var/dhcp.leases");
        testCommand.add("cat /var/etc/dnsmasq.conf");
        testCommand.add("cat /var/resolv.conf.auto");
        testCommand.add("nslookup cloudfi.lan 127.0.0.1");
        testCommand.add("nslookup cloudfi.wlan 127.0.0.1");
        testCommand.add("nslookup www.cloudfi.cn 127.0.0.1");
        testCommand.add("iptables -t nat -nvL");
        testCommand.add("iptables -t filter -nvL");
        testCommand.add("iptables -t mangle -nvL");
        testCommand.add("iptables -t raw -nvL");
        testCommand.add("ipset list");
        testCommand.add("ubus -t 3 call mgmtd get_timers");
        testCommand.add("ubus -t 3 call mgmtd conf.status");
        testCommand.add("ubus -t 3 call mgmtd conf.get");
        testCommand.add("ubus -t 3 call mgmtd state.status");
        testCommand.add("ubus -t 3 call mgmtd netmon.get_host_resolv");
        testCommand.add("ubus -t 3 call rpc.mgmtd dump_rpc");
        testCommand.add("ubus -t 3 call rpc.upgrd dump_rpc");
        testCommand.add("ubus -t 3 call upgrd statu");
        testCommand.add("ubus -t 3 call privoxy_lua get_wechatwifi_stats");
        testCommand.add("ubus -t 3 call privoxy get_client");
        testCommand.add("ubus -t 3 call privoxy get_client_wechat_ids");
        testCommand.add("uci -p /var/state show");
        testCommand.add("uci -p /var/state changes");
        return testCommand;
    }

    public static List<String> getLoginAfterCommand() {
        List<String> loginAfterCommand = new ArrayList<>();
        loginAfterCommand.addAll(getPingCommandAfterLogin_sixth());
        loginAfterCommand.addAll(getTestCommand_fifth());
        return loginAfterCommand;
    }

    /**
     * 第六步：登陆后ping地址
     *
     * @return
     */
    public static List<String> getPingCommandAfterLogin_sixth() {
        List<String> pingCommandAfterLogin = new ArrayList<>();
        pingCommandAfterLogin.add("ping www.cloudfi.cn -c" + pingOutputLimit);
        pingCommandAfterLogin.add("ping 101.200.211.40 -c" + pingOutputLimit);
// http:192.168.203.220/projects/cloudfi-router/wiki/Ar71xx_router_diagnostics_command
        return pingCommandAfterLogin;
    }

    /**
     * 执行Linux命令
     *
     * @param commands
     * @param mExecuteHandler
     * @param callback
     */
    public static void executeCommand(List<String> commands, Handler mExecuteHandler, ExecTaskCallbackHandler callback) {
        if (commands != null && commands.size() > 0) {
            int size = commands.size();
            for (int i = 0; i < size; i++) {
                //AppContext.pingCommandBuffer.append("\r\n" + Constants.LOG_LINE + RouteTestHelper.getTestCommand_fifth().get(i) + Constants.LOG_LINE+"\r\n" );
                SessionController.getSessionController().executeCommand(mExecuteHandler, callback, commands.get(i));
            }
        } else {
            LogUtil.log(TAG, "executeCommand");
        }
    }

    /**
     * 不登录路由器进行ping
     *
     * @param url
     * @return
     */
    public static String pingForAndroid(String url) {
        String str = "";
        try {
            Process process = Runtime.getRuntime().exec(
                    "/system/bin/ping -c 10 " + url);//必须加空格
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    process.getInputStream()));
            StringBuffer output = new StringBuffer(10 * 1024);
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line + "\r\n");
            }
            str = output.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return str;
    }

    public static void executeCommand(final List<String> urls, final Context context) {
        if (urls != null && urls.size() > 0) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    int size = urls.size();
                    for (int i = 0; i < size; i++) {
                        String content = pingForAndroid(urls.get(i));
                        AppContext.pingBeforeBuffer.append("\r\n" + Constants.LOG_LINE + urls.get(i) + Constants.LOG_LINE + "\r\n");
                        AppContext.pingBeforeBuffer.append(content);
                        //SDHelper.saveFileToSD(SDHelper.getFile(SDHelper.filePath, "测试", SDHelper.fileType), content);
                    }
                }
            }).start();
        }
    }

}
