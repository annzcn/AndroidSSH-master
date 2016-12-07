package cn.clickwise;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.baidu.mapapi.SDKInitializer;

import cn.clickwise.config.Constants;
import cn.clickwise.wxapi.RouteActivity;

/**
 * Created by T420s on 2016/10/20.
 */
public class AppContext extends Application {
    //测试连接状态
    public static boolean isTestConnFlag = false;
    public static boolean isDialogLogShow = false;
    public static Context context;

    //是否登录成功后第一次运行
    public static boolean isLoginSuccessFirstExecute = true;

    //是否登录成功后第一次运行
    public static boolean isLoginFailFirstExecute = true;

    //是否在在测试当中
    public static boolean isTestProgressing = false;

    //是否点击了分享
    public static boolean isClickShare = false;

    public static boolean isDiagnoseSuccess = false;

    //为了避免进程完毕后多次分享
    public static boolean isFirstShowShareDialog = false;

    public static boolean isFisrtLocal = true;
    public static StringBuilder wifiBuffer;
    public static StringBuilder pingBeforeBuffer;
    public static StringBuilder pingCommandBuffer;
    public static StringBuilder sshAll;

    public static boolean isLogin = false;

    //十天内免登录
    public static boolean isTenNoLogin = false;

    @Override
    public void onCreate() {
        //Log.d("ttt", "AppContext onCreate: ");
        super.onCreate();
        context = this;
        if (wifiBuffer == null) {
            wifiBuffer = new StringBuilder();
        }
        if (pingBeforeBuffer == null) {
            pingBeforeBuffer = new StringBuilder();
        }
        if (pingCommandBuffer == null) {
            pingCommandBuffer = new StringBuilder();
        }
        if (sshAll == null) {
            sshAll = new StringBuilder();
        }
        SDKInitializer.initialize(getApplicationContext());
        //EventBus.getDefault().register(this);
    }

    public static Context getContext() {
        return context;
    }
}
