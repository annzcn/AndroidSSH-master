package cn.clickwise.ui;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.clickwise.R;
import cn.clickwise.adapters.RouterLocalBaseAdapter;
import cn.clickwise.base.BaseActivity;
import cn.clickwise.bean.RouterLocalInfoReturn;
import cn.clickwise.config.Constants;
import cn.clickwise.utils.other.CalculateUtil;
import cn.clickwise.utils.other.MapUtil;
import cn.clickwise.utils.other.ToastUtil;
import cn.clickwise.views.VHTableScrollView;

public class RouterDetailActivity extends BaseActivity implements AdapterView.OnItemClickListener {
    @BindView(R.id.tbar_router_title)
    Toolbar mTbarRouterTitle;
    @BindView(R.id.linear_routerlocal_scroll)
    LinearLayout mLinearRouterlocalScroll;
    @BindView(R.id.vh_routerlocal_scroll)
    VHTableScrollView mVhRouterlocalScroll;
    @BindView(R.id.lv_router_detail)
    ListView mLvRouterDetail;
    private Map<String, Object> mRequestStrMap;
    //装入所有的HScrollView
    protected List<VHTableScrollView> mHScrollViews = new ArrayList<>();
    public HorizontalScrollView mTouchView;
    private RouterLocalBaseAdapter mRouterLocalBaseAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_router_detail);
        ButterKnife.bind(this);
        register();
        initView();
        setListener();
        initCtrl();
        setAdapters();
    }

    @Override
    protected void register() {
        EventBus.getDefault().registerSticky(this);
    }

    public void onScrollChanged(int l, int t, int oldl, int oldt) {
        for (VHTableScrollView scrollView : mHScrollViews) {
            //防止重复滑动
            if (mTouchView != scrollView)
                scrollView.smoothScrollTo(l, t);
        }
    }

    @Override
    protected void initView() {
        mTbarRouterTitle.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        mTbarRouterTitle.setNavigationIcon(R.drawable.returns);
        mTbarRouterTitle.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void setListener() {
        mLvRouterDetail.setOnItemClickListener(this);
        mLvRouterDetail.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ToastUtil.make("内部类点击了");
            }
        });
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    protected void initCtrl() {
        mRouterLocalBaseAdapter = new RouterLocalBaseAdapter(this, mHScrollViews, mLvRouterDetail);
    }


    @Override
    protected void setAdapters() {
        //mRecRouterDetail.setAdapter(mRouterLocalAdapter);
    }

    @Subscriber(tag = MapUtil.TAG_OFFLINE_SUM)
    public void onReceiveOfflineRouterSumStickyEvent(ArrayList<RouterLocalInfoReturn.Result> result) {
        mTbarRouterTitle.setTitle(getString(R.string.router_offline_detail));
        showResult(result);
    }

    @Subscriber(tag = MapUtil.TAG_LOCATION_SELECT)
    public void onReceiveSelectRouterStickyEvent(ArrayList<RouterLocalInfoReturn.Result> result) {
        mTbarRouterTitle.setTitle(getString(R.string.router_distribution_detail));
        showResult(result);
    }

    private void showResult(ArrayList<RouterLocalInfoReturn.Result> result) {
        if (mRequestStrMap == null) {
            return;
        }
        //当前位置
        double latitudeOwn = (double) mRequestStrMap.get(Constants.MAP_LATITUDE);
        double longitudeOwn = (double) mRequestStrMap.get(Constants.MAP_LONGITUDE);
        List<String> titles = new ArrayList<>();
        titles.add(Constants.ROUTERLOCAL_MAC);
        titles.add(Constants.ROUTERLOCAL_STATE);
        titles.add(Constants.ROUTERLOCAL_DURATION_TIME);
        titles.add(Constants.ROUTERLOCAL_DISTANCE);
        titles.add(Constants.ROUTERLOCAL_AGENT);
        titles.add(Constants.ROUTERLOCAL_MERCHANT);
        titles.add(Constants.ROUTERLOCAL_REGION);
        titles.add(Constants.ROUTERLOCAL_LINKER);
        titles.add(Constants.ROUTERLOCAL_PHONE);
        titles.add(Constants.ROUTERLOCAL_TASK);
        //titles.add("积分");
        /**
         * 生成数据结构过程
         */
        //标题
        int colSize = titles.size();
        for (int i = 0; i < colSize; i++) {
            View titleLayout = LayoutInflater.from(this).inflate(R.layout.row_title, null);
            TextView tvTitle = (TextView) titleLayout.findViewById(R.id.tv_rowtitle_title);
            tvTitle.setText(titles.get(i));
            mLinearRouterlocalScroll.addView(titleLayout);
        }
        //添加头滑动事件
        mHScrollViews.add(mVhRouterlocalScroll);
        //重构数据结构
        List<Map<String, String>> mapList = new ArrayList<>();
        int rowSize = result.size();
        for (int row = 0; row < rowSize; row++) {
            Map<String, String> map = new HashMap<>();
            for (int col = 0; col < colSize; col++) {
                map.put(titles.get(col), checkInputStr(longitudeOwn, latitudeOwn, result, titles.get(col), row));
            }
            mapList.add(map);
        }
        mLvRouterDetail.setAdapter(mRouterLocalBaseAdapter);
        mRouterLocalBaseAdapter.setData(mapList, titles);
    }

    private String checkInputStr(double longitudeOwn, double latitudeOwn, ArrayList<RouterLocalInfoReturn.Result> data, String title, int position) {
        String inputStr = null;
        switch (title) {
            case Constants.ROUTERLOCAL_AGENT:
                inputStr = data.get(position).getName();
                break;
            case Constants.ROUTERLOCAL_REGION:
                inputStr = "北京";
                break;
            case Constants.ROUTERLOCAL_MERCHANT:
                inputStr = data.get(position).getShopname();
                break;
            case Constants.ROUTERLOCAL_MAC:
                inputStr = data.get(position).getAcmac();
                break;
            case Constants.ROUTERLOCAL_STATE:
                inputStr = "online".equals(data.get(position).getOnline_stats()) ? Constants.ROUTERLOCAL_STATE_ONLINE : Constants.ROUTERLOCAL_STATE_OFFLINE;
                break;
            case Constants.ROUTERLOCAL_DISTANCE:
                inputStr = (int) (CalculateUtil.getDistance(longitudeOwn, latitudeOwn, Double.parseDouble(data.get(position).getLongtitude()), Double.parseDouble(data.get(position).getLatitude()))) + "m";
                break;
            case Constants.ROUTERLOCAL_LINKER:
                inputStr = data.get(position).getLinker();
                break;
            case Constants.ROUTERLOCAL_PHONE:
                inputStr = data.get(position).getPhone();
                break;
            case Constants.ROUTERLOCAL_DURATION_TIME:
                inputStr = data.get(position).getOnline_time();
                break;
            case Constants.ROUTERLOCAL_TASK:
                inputStr = Constants.ROUTERLOCAL_RECEIVE_TASK;
        }
        return inputStr;
    }

    @Subscriber
    public void onReceiveStickyEvent(Map<String, Object> requestMap) {
        mRequestStrMap = requestMap;
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregister();
    }

    @Override
    protected void unregister() {
        EventBus.getDefault().removeStickyEvent(this.getClass());
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ToastUtil.make("item点击了"+position);
        view.findViewById(R.id.list_adapter_task_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtil.make("点击了");
            }
        });
    }
}
