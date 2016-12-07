package cn.clickwise.model.request;

import android.app.ProgressDialog;
import android.util.Log;

import org.simple.eventbus.EventBus;

import java.util.Map;

import cn.clickwise.bean.AllPushLocationReturn;
import cn.clickwise.bean.PushUserLocation;
import cn.clickwise.bean.PushUserLocationReturn;
import cn.clickwise.bean.RouterLocalInfoReturn;
import cn.clickwise.bean.UserLoginBackgroundReturn;
import cn.clickwise.bean.UserLoginReturn;
import cn.clickwise.config.Constants;
import cn.clickwise.interf.IAllPushLocationCallBack;
import cn.clickwise.interf.ICheckLogin;
import cn.clickwise.interf.ICheckNet;
import cn.clickwise.interf.IPushUserLocationCallBack;
import cn.clickwise.interf.IRouterLocalInfoCallBack;
import cn.clickwise.interf.IUserLoginBackgroundCallBack;
import cn.clickwise.interf.IUserLoginCallBack;

/**
 * Created by T420s on 2016/11/9.
 */
public class RequestManager {
    private static ICheckNet mICheckNet;

    public static void setICheckLogin(ICheckNet iCheckNet) {
        RequestManager.mICheckNet = iCheckNet;
    }

    /**
     * RouterLocalResultCode post请求
     *
     * @param url
     * @param requestMap
     */
    public static void getRouterLocalBean(String url, Map<String, Object> requestMap) {
        new RequestRouterLocalInfo(requestMap, new IRouterLocalInfoCallBack() {
            @Override
            public void routerLocalResult(RouterLocalInfoReturn routerLocalInfoBean) {
                EventBus.getDefault().post(routerLocalInfoBean);
            }
        }).execute(url);
    }

    public static void getOfflineRouterResult(String url, Map<String, Object> requestMap) {
        new RequestRouterLocalInfo(requestMap, new IRouterLocalInfoCallBack() {
            @Override
            public void routerLocalResult(RouterLocalInfoReturn routerLocalInfoBean) {
                EventBus.getDefault().post(routerLocalInfoBean.getResult());
            }
        }).execute(url);
    }

    public static void getLoginResult(ProgressDialog progressDialog, String url, Map<String, String> requestMap) {
        new RequestLogin(progressDialog, requestMap, new IUserLoginCallBack() {
            @Override
            public void userLoginResult(UserLoginReturn userLoginBean) {
                EventBus.getDefault().post(userLoginBean);
            }
        }).execute(url);
    }

    public static void getCheckLoginResult(ProgressDialog progressDialog, String url, Map<String, String> requestMap, final ICheckLogin iCheckLogin) {
        new RequestLogin(progressDialog, requestMap, new IUserLoginCallBack() {
            @Override
            public void userLoginResult(UserLoginReturn userLoginBean) {
                if (userLoginBean != null && userLoginBean.getState().equals(Constants.REQUEST_ROUTERLOCAL_RESULT_SUCCESS)) {
                    iCheckLogin.checkLoginSuccess();
                } else {
                    iCheckLogin.checkLoginFail();
                }
            }
        }).execute(url);
    }

    public static void getLoginBackground(String url, Map<String, String> map) {
        new RequestLoginBackground(map, new IUserLoginBackgroundCallBack() {
            @Override
            public void userLoginBackgroundResult(UserLoginBackgroundReturn userLoginBackgroundBean) {
                EventBus.getDefault().post(userLoginBackgroundBean);
            }
        }).execute(url);
    }

    /**
     * 上传地推人员位置信息
     *
     * @param url
     * @param pushUserLocation
     */
    public static void getPushUserLocation(String url, PushUserLocation pushUserLocation) {
        new RequestPushUserLocation(pushUserLocation, new IPushUserLocationCallBack() {
            @Override
            public void userLocationResult(PushUserLocationReturn pushUserLocationReturn) {
                EventBus.getDefault().post(pushUserLocationReturn);
            }
        }).execute(url);
    }

    /**
     * 返回所有地推人员的实时位置信息
     *
     * @param url
     * @param map
     */
    public static void getAllPushUserLocation(String url, Map<String, Object> map) {
        new RequestAllPushLocation(map, new IAllPushLocationCallBack() {
            @Override
            public void allPushLocationResult(AllPushLocationReturn allPushLocationReturn) {
                EventBus.getDefault().post(allPushLocationReturn);
            }
        }).execute(url);
    }
}
