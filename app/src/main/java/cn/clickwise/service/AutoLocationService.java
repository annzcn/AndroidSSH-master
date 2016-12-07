package cn.clickwise.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.util.HashMap;
import java.util.Map;

import cn.clickwise.bean.PushUserLocation;
import cn.clickwise.bean.PushUserLocationReturn;
import cn.clickwise.config.Constants;
import cn.clickwise.config.RequestUrl;
import cn.clickwise.model.request.RequestManager;
import cn.clickwise.utils.other.MapUtil;
import cn.clickwise.utils.other.TimeUtil;
import cn.clickwise.utils.other.ToastUtil;

public class AutoLocationService extends Service implements BDLocationListener {
    private LocationClient mLocationClient;
    private PushUserLocation mPushUserLocation;

    public AutoLocationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        init();
        initLocation();
        register();
        startLocation();
    }

    private void init() {
        mPushUserLocation = new PushUserLocation();
    }

    private void startLocation() {
        mLocationClient.start();
    }

    private void register() {
        EventBus.getDefault().register(this);
        mLocationClient.registerLocationListener(this);
    }

    public void unregister() {
        EventBus.getDefault().unregister(this);
        mLocationClient.unRegisterLocationListener(this);
    }

    private void initLocation() {
        mLocationClient = new LocationClient(this);
        MapUtil.initLocationClient(mLocationClient, Constants.PUSH_USER_LOCATION_SPAN_TIME);
    }


    @Override
    public void onReceiveLocation(BDLocation bdLocation) {
        if (bdLocation.getLocType() == BDLocation.TypeCriteriaException || bdLocation.getLocType() == BDLocation.TypeOffLineLocation || bdLocation.getLocType() == BDLocation.TypeNetWorkException) {
            ToastUtil.make("定位失败");
            return;
        }
        //上传定位数据
        SharedPreferences userInfo = getSharedPreferences(Constants.SAVE_LOGIN_INFO, 0);
        String userId = userInfo.getString(Constants.USER_ID, null);
        if (userId != null) {
            mPushUserLocation.setAid(Integer.parseInt(userId));
            mPushUserLocation.setDate(TimeUtil.getBigDate());
            mPushUserLocation.setTime(TimeUtil.getTime());
            mPushUserLocation.setLatitude(bdLocation.getLatitude());
            mPushUserLocation.setLongitude(bdLocation.getLongitude());
            mPushUserLocation.setProvince(bdLocation.getProvince());
            mPushUserLocation.setCity(bdLocation.getCity());
            mPushUserLocation.setDistrict(bdLocation.getDistrict());
            mPushUserLocation.setStreet(bdLocation.getStreet());
            mPushUserLocation.setAddrStr(bdLocation.getAddrStr());
            mPushUserLocation.setDescrible(bdLocation.getLocationDescribe());
            RequestManager.getPushUserLocation(RequestUrl.URL_push_location_up, mPushUserLocation);
        }
    }

    /**
     * 上传定位数据返回结果
     *
     * @param pushUserLocationReturn
     */
    @Subscriber
    public void onPushUserLocationResultMainEvent(PushUserLocationReturn pushUserLocationReturn) {
        //ToastUtil.make(pushUserLocationReturn.getState());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mLocationClient.stop();
        unregister();
    }
}
