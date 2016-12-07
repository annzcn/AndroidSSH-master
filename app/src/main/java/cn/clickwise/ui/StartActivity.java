package cn.clickwise.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import cn.clickwise.AppContext;
import cn.clickwise.R;
import cn.clickwise.bean.UserLoginReturn;
import cn.clickwise.config.Constants;
import cn.clickwise.service.AutoLocationService;
import cn.clickwise.service.OfflineRouterService;
import cn.clickwise.utils.other.NetWorkUtil;
import cn.clickwise.wxapi.RouteActivity;

public class StartActivity extends AppCompatActivity {
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.TEN_NO_LOGIN_TIME, 0);
        long chsTime = sharedPreferences.getLong(Constants.CHS_TIME, 0);
        //判断是否免登陆
        if (chsTime != 0 && System.currentTimeMillis() - chsTime <= Constants.TEN_TIME) {
            AppContext.isTenNoLogin = true;
            AppContext.isLogin = true;
        } else {
            AppContext.isTenNoLogin = false;
            AppContext.isLogin = false;
        }
        mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (AppContext.isTenNoLogin) {//十天内免登陆
                    //免登陆时地推人员启动地位服务
                    SharedPreferences userInfo = getSharedPreferences(Constants.SAVE_LOGIN_INFO, 0);
                    int userType = userInfo.getInt(Constants.USER_TYPR, Constants.FAIL_INT);
                    if (userType != Constants.FAIL_INT && userType == UserLoginReturn.AGENT_PUSH) {
                        //if (NetWorkUtil.isNetWork()) {//网络可用时才启动定位服务
                            startService(new Intent(StartActivity.this, AutoLocationService.class));
                            //启动掉线路由定位服务
                            startService(new Intent(StartActivity.this, OfflineRouterService.class));
                        //}
                    }
                    startActivity(new Intent(StartActivity.this, RouteActivity.class));
                    finish();
                } else {
                    startActivity(new Intent(StartActivity.this, LoginActivity.class));
                    finish();
                }
            }
        }, Constants.START_LAUCH_TIME);
    }
}
