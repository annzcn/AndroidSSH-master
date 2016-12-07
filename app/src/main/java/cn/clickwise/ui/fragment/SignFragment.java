package cn.clickwise.ui.fragment;


import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.TextOptions;
import com.baidu.mapapi.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.clickwise.AppContext;
import cn.clickwise.R;
import cn.clickwise.base.BaseFragment;
import cn.clickwise.config.Constants;
import cn.clickwise.utils.other.MapUtil;
import cn.clickwise.utils.other.TDivce;
import cn.clickwise.utils.other.TimeUtil;
import cn.clickwise.utils.other.ToastUtil;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignFragment extends BaseFragment implements BDLocationListener {

    @BindView(R.id.tv_sign_date)
    TextView mTvSignDate;
    @BindView(R.id.tv_sign_enterprise)
    TextView mTvSignEnterprise;
    @BindView(R.id.tv_sign_location)
    TextView mTvSignLocation;
    @BindView(R.id.map_sign_show)
    MapView mMapSignShow;
    @BindView(R.id.tv_sign_time)
    TextView mTvSignTime;
    @BindView(R.id.card_sign_sign)
    CardView mCardSignSign;
    @BindView(R.id.tv_sign_isign)
    TextView mTvSignIsign;
    @BindView(R.id.rela_sign_signlayout)
    RelativeLayout mRelaSignSignlayout;
    private BaiduMap mSignMap;
    private LocationClient mLocationClient;
    private String mLocationDescribe;
    private Handler mHandler = new Handler();
    private double mLatitude;
    private double mLongitude;

    public SignFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View signFragView = inflater.inflate(R.layout.fragment_sign, container, false);
        ButterKnife.bind(this, signFragView);
        return signFragView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        setListener();
        initLocation();
        register();
        startLocation();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.card_sign_sign://签到
                //保存签到信息
                SharedPreferences userInfo = getActivity().getSharedPreferences(Constants.SAVE_LOGIN_INFO, 0);
                int iSign = userInfo.getInt(Constants.USER_IS_SIGN, Constants.FAIL_INT);
                if (iSign != Constants.USER_SIGNED) {
                    SharedPreferences.Editor edit = userInfo.edit();
                    edit.putInt(Constants.USER_IS_SIGN, Constants.USER_SIGNED);
                    edit.commit();
                    Drawable signedDrawable = getResources().getDrawable(R.drawable.sign_yes_16);
                    mTvSignIsign.setCompoundDrawablesWithIntrinsicBounds(signedDrawable, null, null, null);
                    mTvSignIsign.setText("今日你已签到");
                    if (mLocationDescribe != null) {
                        String username = userInfo.getString(Constants.USER_NAME, Constants.EMPTY);
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle("签到提示")
                                .setMessage("Dear " + username + "，签到成功!\r\n每天都是一个新的开始，加油!\r\n签到时间：" + TimeUtil.getTime() + "\r\n签到地点：" + mLocationDescribe);
                        final AlertDialog dialog = builder.show();//只有show创建的dialog才能dismiss
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                dialog.dismiss();
                                //finish();
                            }
                        }, 1500);
                    }
                    break;
                }
        }
    }

    //定位初始化
    private void initLocation() {
        mLocationClient = new LocationClient(AppContext.getContext());
        MapUtil.initLocationClient(mLocationClient, 0);
    }

    private void localTag(int mapTag, double latitude, double longitude, String locationDescribe) {
        BitmapDescriptor iconBitmap = BitmapDescriptorFactory.fromResource(mapTag);
        signRouterInMap(latitude, longitude, iconBitmap, locationDescribe);
    }

    /**
     * 在地图上显示路由器分布地点s
     */
    private void signRouterInMap(double lat, double lon, BitmapDescriptor iconBitmap, String locationDescribe) {
        LatLng latLng = new LatLng(lat, lon);
        OverlayOptions options = new MarkerOptions()
                .position(latLng)
                .icon(iconBitmap);
        //文字标注
        OverlayOptions textOptions = new TextOptions()
                .fontColor(Color.RED)
                .fontSize(20)
                .text(locationDescribe)
                .position(latLng);
        OverlayOptions textOptions2 = new TextOptions()
                .fontColor(Color.GREEN)
                .fontSize(20)
                .text("吕杨")
                .position(latLng);
        List<OverlayOptions> optionses = new ArrayList<>();
        optionses.add(textOptions);
        //optionses.add(textOptions2);
        optionses.add(options);
        //mSignMap.addOverlay(options);
        mSignMap.addOverlays(optionses);

    }

    @Override
    protected void initView() {
        mSignMap = mMapSignShow.getMap();
        mSignMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        //mSignMap.setTrafficEnabled(true);
        mTvSignDate.setText(" " + TimeUtil.getBigDate());
        mTvSignEnterprise.setText(getString(R.string.enterprise));
        mTvSignTime.setText(TimeUtil.getTime());
        //动态设置控件的高度
        int tDivceHeight = TDivce.getTDivceHeight(getActivity());
        ViewGroup.LayoutParams mapShowParams = mMapSignShow.getLayoutParams();
        mapShowParams.height = (int) (tDivceHeight * 1.f / 4.f);
        mMapSignShow.setLayoutParams(mapShowParams);
        ViewGroup.LayoutParams signLayoutParams = mRelaSignSignlayout.getLayoutParams();
        signLayoutParams.height = (int) (tDivceHeight * 2.f / 4.f);
        mRelaSignSignlayout.setLayoutParams(signLayoutParams);
        ViewGroup.LayoutParams signCardParams = mCardSignSign.getLayoutParams();
        float radius = tDivceHeight * 2.f / 4.f / 2.f;
        signCardParams.height = (int) radius;
        signCardParams.width = (int) radius;
        mCardSignSign.setRadius(radius / 2.f);
        mCardSignSign.setLayoutParams(signCardParams);
        //判断是否签到来更新UI
        SharedPreferences userInfo = getActivity().getSharedPreferences(Constants.SAVE_LOGIN_INFO, 0);
        int iSign = userInfo.getInt(Constants.USER_IS_SIGN, Constants.FAIL_INT);
        if (iSign == Constants.USER_SIGNED) {//签到
            Drawable isDrawable = getResources().getDrawable(R.drawable.sign_yes_16);
            mTvSignIsign.setText("今日你已签到");
            mTvSignIsign.setCompoundDrawablesWithIntrinsicBounds(isDrawable, null, null, null);
        } else {
            Drawable noDrawable = getResources().getDrawable(R.drawable.sign_no_16);
            mTvSignIsign.setText("今日你还未签到，赶紧签到吧！");
            mTvSignIsign.setCompoundDrawablesWithIntrinsicBounds(noDrawable, null, null, null);
        }
    }

    @Override
    protected void setListener() {
        mCardSignSign.setOnClickListener(this);
    }

    @Override
    protected void register() {
        mLocationClient.registerLocationListener(this);
    }

    private void startLocation() {
        mLocationClient.start();
    }

    @Override
    protected void unregister() {
        mLocationClient.unRegisterLocationListener(this);
    }

    @Override
    public void onReceiveLocation(BDLocation location) {
        if (location.getLocType() == BDLocation.TypeCriteriaException || location.getLocType() == BDLocation.TypeOffLineLocation || location.getLocType() == BDLocation.TypeNetWorkException) {
            ToastUtil.make("定位失败");
            return;
        }
        //位置不变UI不更新
        if (mLatitude != location.getLatitude() || mLongitude != location.getLongitude()) {
            mLatitude = location.getLatitude();
            mLongitude = location.getLongitude();
            mSignMap.clear();
            mLocationDescribe = location.getLocationDescribe();
            mTvSignLocation.setText(mLocationDescribe);
            localTag(R.mipmap.maptag_own_32, location.getLatitude(), location.getLongitude(), mLocationDescribe);
            MapUtil.setUserMapCenter(mSignMap, location.getLatitude(), location.getLongitude(), Constants.BAIDU_ZOOM_LEVEL_NORMAL);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapSignShow.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapSignShow.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mLocationClient.stop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapSignShow.onDestroy();
        unregister();
    }
}
