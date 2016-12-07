package cn.clickwise.ui;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.TextOptions;
import com.baidu.mapapi.model.LatLng;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import cn.clickwise.AppContext;
import cn.clickwise.R;
import cn.clickwise.base.BaseActivity;
import cn.clickwise.bean.AllPushLocationReturn;
import cn.clickwise.config.Constants;
import cn.clickwise.config.RequestUrl;
import cn.clickwise.model.request.RequestManager;
import cn.clickwise.utils.other.MapUtil;
import cn.clickwise.utils.other.ToastUtil;

public class PushMapActivity extends BaseActivity implements BDLocationListener, BaiduMap.OnMarkerClickListener {

    @butterknife.BindView(R.id.tbar_push_title)
    android.support.v7.widget.Toolbar mTbarPushTitle;
    @butterknife.BindView(R.id.map_push_show)
    com.baidu.mapapi.map.MapView mMapPushShow;
    private BaiduMap mPushMap;
    private LocationClient mLocationClient;
    private double mOwnLongitude;
    private double mOwnLatitude;
    private Map<String, Object> mJsonMap;
    private String mUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_push_map);
        ButterKnife.bind(this);
        init();
        initView();
        setListener();
        //显示地图
        showPushMap();
        //定位
        initLocation();
        register();
        startLocation();
    }

    private void init() {
        mJsonMap = new HashMap<>();
        SharedPreferences userInfo = getSharedPreferences(Constants.SAVE_LOGIN_INFO, 0);
        mUserId = userInfo.getString(Constants.USER_ID, null);
        if (mUserId != null) {
            mJsonMap.put("aid", mUserId);
        }
    }

    //开启定位
    private void startLocation() {
        mLocationClient.start();
    }

    //定位初始化
    private void initLocation() {
        mLocationClient = new LocationClient(getApplicationContext());
        MapUtil.initLocationClient(mLocationClient, Constants.REQUEST_PUSH_USER_LOCATION_SPAN_TIME);
    }

    @Override
    protected void register() {
        //注册位置监听函数
        mLocationClient.registerLocationListener(this);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void unregister() {
        //注销
        mLocationClient.unRegisterLocationListener(this);
        EventBus.getDefault().unregister(this);
    }

    private void showPushMap() {
        mPushMap = mMapPushShow.getMap();
        mPushMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        mPushMap.setTrafficEnabled(true);
    }

    @Override
    protected void initView() {
        mTbarPushTitle.setTitle(R.string.push_distribution);
        mTbarPushTitle.setNavigationIcon(R.drawable.returns);
        mTbarPushTitle.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void setListener() {

    }

    private void localTag(int mapTag, double latitude, double longitude, String pushrName) {
        BitmapDescriptor iconBitmap = BitmapDescriptorFactory.fromResource(mapTag);
        setMarker(latitude, longitude, iconBitmap, pushrName);
    }

    /**
     * 在地图上显示路由器分布地点
     */
    private void setMarker(double lat, double lon, BitmapDescriptor iconBitmap, String pushrName) {
        LatLng latLng = new LatLng(lat, lon);//GPS坐标转换为百度坐标
        OverlayOptions iconOptions = new MarkerOptions()
                .position(latLng)
                .icon(iconBitmap);
        //文字标注
        OverlayOptions textOptions1 = new TextOptions()
                .fontColor(getResources().getColor(R.color.colorPrimary))
                .text(pushrName)
                .fontSize(30)
                .zIndex(50)
                .position(latLng);
       /* OverlayOptions textOptions2 = new TextOptions()
                .fontColor(Color.BLACK)
                .text("13520245438")
                .position(latLng);*/
        List<OverlayOptions> optionses = new ArrayList<>();
        optionses.add(textOptions1);
        //optionses.add(textOptions2);
        optionses.add(iconOptions);
        mPushMap.addOverlays(optionses);
    }

    @Subscriber
    public void onAllPushUserLocationResultMainEvent(AllPushLocationReturn allPushLocationReturn) {
        List<AllPushLocationReturn.ResultBean> result = allPushLocationReturn.getResult();
        if (result != null) {
            mPushMap.clear();
            //标注自己
            localTag(R.mipmap.maptag_own_32, mOwnLatitude, mOwnLongitude, "我");
            //标注其他人
            int size = result.size();
            for (int i = 0; i < size; i++) {
                AllPushLocationReturn.ResultBean resultBean = result.get(i);
                if (!resultBean.getAid().equals(mUserId)) {//不绘制自己
                    localTag(R.mipmap.maptag_online_24, Double.parseDouble(resultBean.getLatitude()), Double.parseDouble(resultBean.getLongitude()), resultBean.getName());
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapPushShow.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapPushShow.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mLocationClient.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregister();
        mMapPushShow.onDestroy();
    }

    @Override
    public void onReceiveLocation(BDLocation bdLocation) {
        if (bdLocation.getLocType() == BDLocation.TypeCriteriaException || bdLocation.getLocType() == BDLocation.TypeOffLineLocation || bdLocation.getLocType() == BDLocation.TypeNetWorkException) {
            ToastUtil.make("定位失败");
            return;
        }
        //位置没变就不再请求数据了
        if (mOwnLongitude != bdLocation.getLongitude() || mOwnLatitude != bdLocation.getLatitude()) {
            mTbarPushTitle.setSubtitle("当前位置：" + bdLocation.getLocationDescribe());
            //设置地图中心点
            MapUtil.setUserMapCenter(mPushMap, bdLocation.getLatitude(), bdLocation.getLongitude(), Constants.BAIDU_ZOOM_LEVEL_NORMAL);
            mOwnLongitude = bdLocation.getLongitude();
            mOwnLatitude = bdLocation.getLatitude();
            //请求数据
            if (mJsonMap.size() > 0) {
                RequestManager.getAllPushUserLocation(RequestUrl.URL_all_push_location_down, mJsonMap);
            }
        }
    }

    @Override
    public void onClick(View v) {

    }

    /**
     * Marker的点击事件
     *
     * @param marker
     * @return
     */
    @Override
    public boolean onMarkerClick(Marker marker) {
        ToastUtil.make(marker.getTitle());
        return false;
    }
}
