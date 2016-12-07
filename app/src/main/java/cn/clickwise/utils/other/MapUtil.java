package cn.clickwise.utils.other;

import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.inner.GeoPoint;
import com.baidu.mapapi.utils.CoordinateConverter;

/**
 * Created by lvyang on 2016/11/23.
 */
public class MapUtil {
    public static final String TAG_OFFLINE_SUM = "offlineSumTag";
    public static final String TAG_LOCATION_SELECT = "locationTag";

    public static void initLocationClient(LocationClient locationClient, int locationSpan) {
        LocationClientOption option = new LocationClientOption();
        //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系,这样才可以显示在地图上
        //int span = 10000;//隔10s扫描位置一次
        if (locationSpan >= 1000) {
            option.setScanSpan(locationSpan);//扫面间隔 可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        }
        option.disableCache(true);//使用缓存数据
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤GPS仿真结果，默认需要
        option.setNeedDeviceDirect(true);//设备方法
        locationClient.setLocOption(option);
    }

    /**
     * GPS坐标转换为百度坐标
     *
     * @param latitude
     * @param longitude
     * @return
     */
    public static LatLng gpsToBaidu(double latitude, double longitude) {
        CoordinateConverter converter = new CoordinateConverter();
        converter.from(CoordinateConverter.CoordType.GPS);
        converter.coord(new LatLng(latitude, longitude));
        return converter.convert();
    }

    public static void setUserMapCenter(BaiduMap mMap, double lat, double lon, int zoomLevel) {
        MapStatus mapStatus = new MapStatus.Builder()
                .target(new LatLng(lat, lon))
                .zoom(zoomLevel)
                .build();
        MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mapStatus);
        mMap.setMapStatus(mapStatusUpdate);
    }
}
