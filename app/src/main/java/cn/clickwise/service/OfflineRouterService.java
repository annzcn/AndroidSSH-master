package cn.clickwise.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.clickwise.R;
import cn.clickwise.bean.RouterLocalInfoReturn;
import cn.clickwise.config.Constants;
import cn.clickwise.config.RequestUrl;
import cn.clickwise.model.request.RequestManager;
import cn.clickwise.ui.RouterDetailActivity;
import cn.clickwise.utils.other.MapUtil;
import cn.clickwise.utils.other.ToastUtil;

public class OfflineRouterService extends Service implements BDLocationListener {
    private Map<String, Object> requestMap;
    private LocationClient mLocationClient;
    private static final int REQUESTCODE = 001;
    private String mLocationDescribe;
    private ClickReceiver mClickReceiver;
    private ArrayList<RouterLocalInfoReturn.Result> mResult;

    public OfflineRouterService() {
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
        requestMap = new HashMap<>();
    }

    //使用startService启动
    //另一种启动：bindService绑定启动
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    private void register() {
        mLocationClient.registerLocationListener(this);
        EventBus.getDefault().register(this);
        //注册广播
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.RECEIVER_CLICK);
        mClickReceiver = new ClickReceiver();
        registerReceiver(mClickReceiver, intentFilter);
    }

    private void unregister() {
        EventBus.getDefault().unregister(this);
        mLocationClient.unRegisterLocationListener(this);
        unregisterReceiver(mClickReceiver);
    }

    private void startLocation() {
        mLocationClient.start();
    }

    private void initLocation() {
        mLocationClient = new LocationClient(this);
        MapUtil.initLocationClient(mLocationClient, 1000 * 60);
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    @Override
    public void onReceiveLocation(BDLocation bdLocation) {
        if (bdLocation.getLocType() == BDLocation.TypeCriteriaException || bdLocation.getLocType() == BDLocation.TypeOffLineLocation || bdLocation.getLocType() == BDLocation.TypeNetWorkException) {
            ToastUtil.make("定位失败");
            return;
        }
        mLocationDescribe = bdLocation.getLocationDescribe();
        requestMap.put("state", "offline");
        requestMap.put("range", 2000);
        requestMap.put(Constants.MAP_LATITUDE, bdLocation.getLatitude());
        requestMap.put(Constants.MAP_LONGITUDE, bdLocation.getLongitude());
        requestMap.put(Constants.MAP_DESCRIBE, bdLocation.getLocationDescribe());
        //给主页发送定位数据
        EventBus.getDefault().post(requestMap, Constants.SERVICE_TO_ROUTE_LOCATION);
        RequestManager.getOfflineRouterResult(RequestUrl.URL_routerLocalPostRequest, requestMap);
    }

    //定位结果
    @Subscriber
    public void offlineRouterResultMainEvent(List<RouterLocalInfoReturn.Result> result) {
        if (result != null && result.size() > 0) {
            mResult = (ArrayList<RouterLocalInfoReturn.Result>) result;
            int offlineRouterSum = mResult.size();
            if (offlineRouterSum > 0) {
                showNotification(offlineRouterSum);
            }
        } else {
            ToastUtil.make("数据请求失败");
        }
    }

    class ClickReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Constants.RECEIVER_CLICK.equals(intent.getAction()) && requestMap != null && mResult != null && mResult.size() > 0 && requestMap.size() > 0) {
                EventBus.getDefault().postSticky(requestMap);
                EventBus.getDefault().postSticky(mResult, MapUtil.TAG_OFFLINE_SUM);
                Intent newIntent = new Intent(context, RouterDetailActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//必须加addFlags否则不能跳转Activity
                context.startActivity(newIntent);
            }
        }
    }

    private void showNotification(int offlineRouterSum) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Bitmap largeIconBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.offline_router_72);
//        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.item_notifi);
//        remoteViews.setImageViewResource(R.id.img_notifi_largeicon, R.mipmap.offline_router_72);
//        remoteViews.setTextViewText(R.id.tv_notifi_title, "附近掉线路由提示");
//        remoteViews.setTextViewText(R.id.tv_notifi_text, "您附近有" + offlineRouterSum + "台掉线路由器");
//        remoteViews.setTextViewText(R.id.tv_notifi_info, mLocationDescribe != null ? "当前位置：" + mLocationDescribe : "");
//        remoteViews.setTextViewText(R.id.tv_notifi_time, TimeUtil.getTime());
       /* NotificationCompat.BigTextStyle  bigTextStyle = new NotificationCompat.BigTextStyle ();
        //bigTextStyle.addLine("您附近有" + offlineRouterSum + "台掉线路由器");
        bigTextStyle.setBigContentTitle("您附近有" + offlineRouterSum + "台掉线路由器");
        bigTextStyle.setSummaryText("您附近有" + offlineRouterSum + "台掉线路由器");*/
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder./*setContent(remoteViews)*/
                setContentTitle("小伙伴，附近" + offlineRouterSum + "台路由掉线")
                .setContentIntent(getDefaultIntent(PendingIntent.FLAG_UPDATE_CURRENT))//跳转Receiver执行逻辑操作
                .setLargeIcon(largeIconBitmap)
//                .setStyle(bigTextStyle)
                .setContentText(mLocationDescribe != null ? "当前位置：" + mLocationDescribe : "")
                .setContentInfo("详情")
                .setTicker("附近路由掉线提示")
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .setOngoing(true)
                .setSmallIcon(R.mipmap.remind_32)//注意：不设置Icon Notification不显示
                .setPriority(Notification.PRIORITY_HIGH)//设置优先级
                .setDefaults(Notification.DEFAULT_ALL);//默认三色灯提醒
        Notification notification = builder.build();
//        notification.bigContentView=remoteViews;
        notification.flags = /*Notification.FLAG_ONGOING_EVENT | Notification.FLAG_FOREGROUND_SERVICE | */Notification.FLAG_SHOW_LIGHTS | Notification.FLAG_ONLY_ALERT_ONCE | Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(0, notification);
    }

    public PendingIntent getDefaultIntent(int flag) {
        //Intent intent=new Intent(this, ClickReceiver.class);
        Intent intent = new Intent(Constants.RECEIVER_CLICK);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, flag);   //.getActivity(this, REQUESTCODE, intent, flag);
        return pendingIntent;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mLocationClient.stop();
        unregister();
    }

}
