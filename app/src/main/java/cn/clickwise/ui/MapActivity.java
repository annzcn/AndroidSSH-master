package cn.clickwise.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.BDNotifyListener;
import com.baidu.location.LocationClient;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.TextOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchOption;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.clickwise.AppContext;
import cn.clickwise.R;
import cn.clickwise.adapters.MenuAdapter;
import cn.clickwise.base.BaseActivity;
import cn.clickwise.bean.RouterLocalInfoReturn;
import cn.clickwise.config.Constants;
import cn.clickwise.config.RequestUrl;
import cn.clickwise.model.request.RequestManager;
import cn.clickwise.utils.other.MapUtil;
import cn.clickwise.utils.other.ToastUtil;
import cn.clickwise.utils.overlayutil.PoiOverlay;

public class MapActivity extends BaseActivity implements BDLocationListener, OnGetPoiSearchResultListener {
    @BindView(R.id.map_map_show)
    MapView mMapMapShow;
    @BindView(R.id.tbar_map_title)
    Toolbar mTbarMapTitle;
    @BindView(R.id.tab_map_title)
    TabLayout mTabMapTitle;
    @BindView(R.id.fbtn_map_routerdetail)
    FloatingActionButton mFbtnMapRouterdetail;
    @BindView(R.id.abar_map_layout)
    AppBarLayout mAbarMapLayout;
    private BaiduMap mMap;
    private LocationClient mLocationClient;
    private locationNotifyListener mNotifyListener;
    private Vibrator mVibrator;
    private PoiSearch mPoiSearch;
    private List<String> mTitles;
    private PopupWindow mPopupWindow;
    private MenuAdapter mAdapter;
    private List<String> mMenuTitles;
    private Map<String, Object> mRequestStrMap;
    private ArrayList<RouterLocalInfoReturn.Result> mResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        init();
        initView();
        initCtrl();
        setListener();
        //显示普通地图
        showMap();
        //setMapTag();
        //定位
        initLocation();
        register();
        startLocation();
    }

    private void init() {
        //振荡器
        mVibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        //POI检索
        mPoiSearch = PoiSearch.newInstance();
        //筛选数据
        mMenuTitles = new ArrayList<>();
        mRequestStrMap = new HashMap<>();
    }

    /**
     * 方圆POI检索
     *
     * @param latitude
     * @param longitude
     * @param keyWord
     * @param searchRadius
     * @param page
     */
    private void nearBySearch(double latitude, double longitude, String keyWord, int searchRadius, int page) {
        PoiNearbySearchOption nearbySearchOption = new PoiNearbySearchOption();
        nearbySearchOption.location(new LatLng(latitude, longitude));
        nearbySearchOption.keyword(keyWord);
        nearbySearchOption.radius(searchRadius);
        nearbySearchOption.pageNum(page);
        mPoiSearch.searchNearby(nearbySearchOption);
    }

    /**
     * POI检索结果
     */
    @Override
    public void onGetPoiResult(PoiResult poiResult) {
        if (poiResult == null || poiResult.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
            ToastUtil.make("无搜索结果");
            return;
        }
        if (poiResult.error == SearchResult.ERRORNO.NO_ERROR) {
            //显示在地图上
            mMap.clear();
            PoiOverlay poiOverlay = new DrawPoiOverlay(mMap);
            mMap.setOnMarkerClickListener(poiOverlay);
            poiOverlay.setData(poiResult);
            poiOverlay.addToMap();
            poiOverlay.zoomToSpan();
        }
    }

    @Override
    public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {

    }

    @Override
    public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

    }

    class DrawPoiOverlay extends PoiOverlay {

        /**
         * 构造函数
         *
         * @param baiduMap 该 PoiOverlay 引用的 BaiduMap 对象
         */
        public DrawPoiOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public boolean onPoiClick(int i) {
            PoiInfo poiInfo = getPoiResult().getAllPoi().get(i);
            //检索poi搜索结果
            mPoiSearch.searchPoiDetail(new PoiDetailSearchOption().poiUid(poiInfo.uid));
            return true;
        }
    }

    class locationNotifyListener extends BDNotifyListener {
        @Override
        public void onNotify(BDLocation bdLocation, float v) {
            //震动提醒已到设定位置附近 震动提醒1s
            mVibrator.vibrate(1000);
        }
    }

    @Override
    protected void register() {
        //注册位置监听函数
        mLocationClient.registerLocationListener(this);
        //注册位置提醒函数
        mNotifyListener = new locationNotifyListener();
        mLocationClient.registerNotify(mNotifyListener);
    }

    /**
     * 设置位置提醒,用于类导航服务
     *
     * @param lat   纬度
     * @param lon   经度
     * @param range 距离范围
     * @param type  坐标系类型
     */
    private void setNotifyRouteLocation(double lat, double lon, float range, String type) {
        mNotifyListener.SetNotifyLocation(lat, lon, range, type);
    }

    @Override
    protected void unregister() {
        //注销
        mLocationClient.unRegisterLocationListener(this);
        mLocationClient.removeNotifyEvent(mNotifyListener);
        EventBus.getDefault().unregister(this);
    }

    //开启定位
    private void startLocation() {
        AppContext.isFisrtLocal = true;
        mLocationClient.start();
    }

    //定位初始化
    private void initLocation() {
        mLocationClient = new LocationClient(getApplicationContext());
        MapUtil.initLocationClient(mLocationClient, 1000 * 60);
    }

    /**
     * 不断返回返回定位结果
     *
     * @param location
     */
    @Override
    public void onReceiveLocation(BDLocation location) {
        if (location.getLocType() == BDLocation.TypeCriteriaException || location.getLocType() == BDLocation.TypeOffLineLocation || location.getLocType() == BDLocation.TypeNetWorkException) {
            ToastUtil.make("定位失败");
            return;
        }
        //定位标注
        if (AppContext.isFisrtLocal) {//初次进入默认搜索
            mRequestStrMap.put(Constants.MAP_LATITUDE, location.getLatitude());
            mRequestStrMap.put(Constants.MAP_LONGITUDE, location.getLongitude());
            //mRequestStrMap.put("range", 2000);
            mRequestStrMap.put("state", "all");
            mRequestStrMap.put("aid", "0");
            RequestManager.getRouterLocalBean(RequestUrl.URL_routerLocalPostRequest, mRequestStrMap);
            //标识中心点
            MapUtil.setUserMapCenter(mMap, location.getLatitude(), location.getLongitude(), Constants.BAIDU_ZOOM_LEVEL_NORMAL);
            //显示位置
            if (location.getLocationDescribe() != null) {
                mTbarMapTitle.setSubtitle("当前位置：" + location.getLocationDescribe());
            }
            AppContext.isFisrtLocal = false;
        }
        if (location.getLatitude() != (double) (mRequestStrMap.get(Constants.MAP_LATITUDE)) || location.getLongitude() != (double) mRequestStrMap.get(Constants.MAP_LONGITUDE)) {
            //localTag(R.mipmap.maptag_own_32, location.getLatitude(), location.getLongitude());
            mRequestStrMap.put(Constants.MAP_LATITUDE, location.getLatitude());
            mRequestStrMap.put(Constants.MAP_LONGITUDE, location.getLongitude());
            //标识中心点
            MapUtil.setUserMapCenter(mMap, location.getLatitude(), location.getLongitude(), Constants.BAIDU_ZOOM_LEVEL_NORMAL);
            //显示位置
            if (location.getLocationDescribe() != null) {
                mTbarMapTitle.setSubtitle("当前位置：" + location.getLocationDescribe());
            }
        }
    }

    private void localTag(int mapTag, double latitude, double longitude, String shopName) {
        BitmapDescriptor iconBitmap = BitmapDescriptorFactory.fromResource(mapTag);
        signRouterInMap(latitude, longitude, iconBitmap, shopName);
    }

    /**
     * 在地图上显示路由器分布地点
     */
    private void signRouterInMap(double lat, double lon, BitmapDescriptor iconBitmap, String shopName) {
        //LatLng latLng = new LatLng(lat, lon);
        LatLng latLng = MapUtil.gpsToBaidu(lat, lon);//GPS坐标转换为百度坐标
        OverlayOptions options = new MarkerOptions()
                .position(latLng)
                .icon(iconBitmap);
        if (shopName != null) {
            //文字标注
            OverlayOptions textOptions = new TextOptions()
                    .fontColor(getResources().getColor(R.color.colorPrimary))
                    .fontSize(18)
                    .text(shopName)
                    .position(latLng);
            mMap.addOverlay(textOptions);
        }
        mMap.addOverlay(options);
    }

    private void showMap() {
        mMap = mMapMapShow.getMap();
        //普通地图
        mMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        //设置实时交通图
        mMap.setTrafficEnabled(true);
        //打开室内地图
        mMap.setIndoorEnable(true);
    }

    @Override
    protected void initView() {
        mTbarMapTitle.setTitle(getString(R.string.router_distribution));
        mTbarMapTitle.setSubtitle("");
        mTbarMapTitle.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        mTbarMapTitle.setNavigationIcon(R.drawable.returns);
        mTbarMapTitle.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mTitles = new ArrayList<>();
        mTitles.add("代理商");
        //mTitles.add("地区");
        //titles.add("商户");
        mTitles.add("路由状态");
        mTitles.add("范围");
        int size = mTitles.size();
        for (int i = 0; i < size; i++) {
            mTabMapTitle.addTab(mTabMapTitle.newTab().setText(mTitles.get(i)));
        }
    }

    private Context getContext() {
        return MapActivity.this;
    }

    @Override
    protected void initCtrl() {
        mAdapter = new MenuAdapter(getContext());
    }


    @Override
    protected void setListener() {
        mFbtnMapRouterdetail.setOnClickListener(this);
        mPoiSearch.setOnGetPoiSearchResultListener(this);
        mTabMapTitle.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(final TabLayout.Tab tab) {
                if (mPopupWindow != null && mPopupWindow.isShowing()) {
                    mPopupWindow.dismiss();
                    mPopupWindow = null;
                }
                View popView = View.inflate(getContext(), R.layout.pop_listview, null);
                ListView lvMenu = (ListView) popView.findViewById(R.id.lv_main_choose);
                lvMenu.setAdapter(mAdapter);
                final int position = tab.getPosition();
                if (position == Constants.MENU_AGENT) {
                    mMenuTitles.clear();
                    mMenuTitles.add("本人");
                    mMenuTitles.add("所有");
                } else if (position == Constants.MENU_ROUTER_STATE) {
                    mMenuTitles.clear();
                    mMenuTitles.add("在线");
                    mMenuTitles.add("离线");
                    mMenuTitles.add("全部");
                } else if (position == Constants.MENU_RANGE) {
                    mMenuTitles.clear();
                    mMenuTitles.add("100m");
                    mMenuTitles.add("500m");
                    mMenuTitles.add("1000m");
                    mMenuTitles.add("5000m");
                } /*else if (position == Constants.MENU_REGION) {
                    mMenuTitles.clear();
                    //mMenuTitles.add("北京");
                }*/

                mAdapter.setData(mMenuTitles);
                mPopupWindow = new PopupWindow(popView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                mPopupWindow.setFocusable(true);
                mPopupWindow.setClippingEnabled(true);
                mPopupWindow.showAsDropDown(mTabMapTitle);
                lvMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                        mMap.clear();
                        //AppContext.isFisrtLocal = true;
                        String menuChoose = (String) parent.getItemAtPosition(pos);
                        switch (position) {
                            case Constants.MENU_AGENT:
                                switch (pos) {
                                    case 0:
                                        SharedPreferences userInfo = getSharedPreferences(Constants.SAVE_LOGIN_INFO, 0);
                                        String userPwd = userInfo.getString(Constants.USER_PWD, null);
                                        if (userPwd != null) {
                                            mRequestStrMap.put("aid", userPwd);
                                        }
                                        break;
                                    case 1:
                                        mRequestStrMap.put("aid", "0");//0表示全部
                                        break;
                                }
                                break;
                           /* case Constants.MENU_REGION:
                                break;*/
                            case Constants.MENU_ROUTER_STATE:
                                switch (pos) {
                                    case 0:
                                        mRequestStrMap.put("state", "online");
                                        break;
                                    case 1:
                                        mRequestStrMap.put("state", "offline");
                                        break;
                                    case 2:
                                        mRequestStrMap.put("state", "all");
                                        break;
                                }
                                break;
                            case Constants.MENU_RANGE:
                                switch (pos) {
                                    case 0:
                                        mRequestStrMap.put("range", 100);
                                        break;
                                    case 1:
                                        mRequestStrMap.put("range", 500);
                                        break;
                                    case 2:
                                        mRequestStrMap.put("range", 1000);
                                        break;
                                    case 3:
                                        mRequestStrMap.put("range", 5000);
                                        break;
                                }
                                break;
                        }
                        //post请求数据
                        RequestManager.getRouterLocalBean(RequestUrl.URL_routerLocalPostRequest, mRequestStrMap);
                        tab.setText(menuChoose);
                        mPopupWindow.dismiss();
                    }
                });
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Subscriber
    public void requestRouterLocalResultMainEvent(RouterLocalInfoReturn routerLocalInfoBean) {
        if (routerLocalInfoBean.getState().equals(Constants.REQUEST_ROUTERLOCAL_RESULT_SUCCESS)) {
            //post请求成功
            List<RouterLocalInfoReturn.Result> result = routerLocalInfoBean.getResult();
            if (result != null && result.size() > 0) {
                mResult = (ArrayList<RouterLocalInfoReturn.Result>) result;
                final int size = mResult.size();
                signTag(size, false);
                /*mMap.setOnMapStatusChangeListener(new BaiduMap.OnMapStatusChangeListener() {
                    @Override
                    public void onMapStatusChangeStart(MapStatus mapStatus) {

                    }

                    @Override
                    public void onMapStatusChange(MapStatus mapStatus) {

                    }

                    @Override
                    public void onMapStatusChangeFinish(MapStatus mapStatus) {
                        //ToastUtil.make(mapStatus.zoom + "");
                        if (mapStatus.zoom >= Constants.BAIDU_ZOOM_LEVEL_SHOW) {
                            signTag(size, true);
                        } else {
                            signTag(size, false);
                        }
                    }
                });*/
            } else {
                ToastUtil.make("抱歉，无搜索结果");
                //防止没有数据进入详情页
                if (mResult != null) {
                    mResult.clear();
                    mResult = null;
                }
            }

        } else {
            ToastUtil.make(/*routerLocalInfoBean.getResult()*/"数据请求失败");
        }
    }

    private void signTag(int size, boolean isChange) {
        //重新标识前置清空原先标识
        mMap.clear();
        //标识当前位置
        localTag(R.mipmap.maptag_own_32, (double) mRequestStrMap.get(Constants.MAP_LATITUDE), (double) mRequestStrMap.get(Constants.MAP_LONGITUDE), "我");
        //标识路由位置
        for (int i = 0; i < size; i++) {//离线标注红色
            RouterLocalInfoReturn.Result resultBean = mResult.get(i);
            String shopName = null;
            if (isChange) {
                shopName = resultBean.getShopname();
                //标识当前位置
                localTag(R.mipmap.maptag_own_32, (double) mRequestStrMap.get(Constants.MAP_LATITUDE), (double) mRequestStrMap.get(Constants.MAP_LONGITUDE), "我");
            } else {
                localTag(R.mipmap.maptag_own_32, (double) mRequestStrMap.get(Constants.MAP_LATITUDE), (double) mRequestStrMap.get(Constants.MAP_LONGITUDE), null);
            }
            if ("offline".equals(mResult.get(i).getOnline_stats())) {
                localTag(R.mipmap.maptag_offline_24, Double.parseDouble(resultBean.getLatitude()), Double.parseDouble(mResult.get(i).getLongtitude()), shopName);
            } else {//在线标注绿色
                localTag(R.mipmap.maptag_online_24, Double.parseDouble(resultBean.getLatitude()), Double.parseDouble(mResult.get(i).getLongtitude()), shopName);
            }
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.fbtn_map_routerdetail://跳转详情页
                if (mResult != null && mRequestStrMap.size() > 0 && mResult.size() > 0) {
                    EventBus.getDefault().postSticky(mRequestStrMap);
                    EventBus.getDefault().postSticky(mResult, MapUtil.TAG_LOCATION_SELECT);
                    startActivity(new Intent(this, RouterDetailActivity.class));
                }
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapMapShow.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapMapShow.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mLocationClient.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapMapShow.onDestroy();
        unregister();
        mLocationClient.stop();
    }

}
