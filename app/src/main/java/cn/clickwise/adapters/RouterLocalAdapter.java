package cn.clickwise.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.clickwise.R;
import cn.clickwise.bean.RouterLocalInfoReturn;
import cn.clickwise.config.Constants;
import cn.clickwise.utils.other.CalculateUtil;

/**
 * Created by T420s on 2016/11/9.
 */
public class RouterLocalAdapter extends RecyclerView.Adapter<RouterLocalAdapter.RouterViewHolder> {
    private List<RouterLocalInfoReturn.Result> data;
    private Map<String, Object> mRequestStrMap;

    public RouterLocalAdapter() {
        data = new ArrayList<>();
    }

    public void setData(List<RouterLocalInfoReturn.Result> data, Map<String, Object> mRequestStrMap) {
        this.data = data;
        this.mRequestStrMap = mRequestStrMap;
        notifyDataSetChanged();
    }

    @Override
    public RouterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View routerLayout = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_router_detail, parent, false);
        return new RouterViewHolder(routerLayout);
    }

    @Override
    public void onBindViewHolder(RouterViewHolder holder, int position) {
        if (position == 0) {//标题
            holder.mTvRouterAgent.setText("代理商");
            holder.mTvRouterRegion.setText("地区");
            holder.mTvRouterMerchant.setText("商户名");
            holder.mTvRouterMac.setText("Mac地址");
            holder.mTvRouterState.setText("状态");
            holder.mTvRouterRange.setText("范围");
            holder.mTvRouterGrade.setText("积分");
        } else if (position > 0) {
            double latitudeOwn = (double) mRequestStrMap.get(Constants.MAP_LATITUDE);
            double longitudeOwn = (double) mRequestStrMap.get(Constants.MAP_LONGITUDE);
            RouterLocalInfoReturn.Result result = data.get(position);
            holder.mTvRouterAgent.setText(result.getName());
            holder.mTvRouterRegion.setText("北京");
            holder.mTvRouterMerchant.setText(result.getShopname());
            holder.mTvRouterMac.setText(result.getAcmac());
            holder.mTvRouterState.setText(result.getOnline_time());
            holder.mTvRouterRange.setText(CalculateUtil.getDistance(longitudeOwn, latitudeOwn, Double.parseDouble(result.getLongtitude()), Double.parseDouble(result.getLatitude())) + "m");
            holder.mTvRouterGrade.setText("");
        }
    }

    @Override
    public int getItemCount() {
        int size = 0;
        if (data != null) {
            size = data.size();
        }
        return size;
    }

    class RouterViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_router_agent)
        TextView mTvRouterAgent;
        @BindView(R.id.tv_router_region)
        TextView mTvRouterRegion;
        @BindView(R.id.tv_router_merchant)
        TextView mTvRouterMerchant;
        @BindView(R.id.tv_router_mac)
        TextView mTvRouterMac;
        @BindView(R.id.tv_router_state)
        TextView mTvRouterState;
        @BindView(R.id.tv_router_range)
        TextView mTvRouterRange;
        @BindView(R.id.tv_router_grade)
        TextView mTvRouterGrade;

        public RouterViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
