package cn.clickwise.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.clickwise.R;
import cn.clickwise.views.VHTableScrollView;

/**
 * Created by T420s on 2016/11/10.
 */
public class RouterScrolladapter extends RecyclerView.Adapter<RouterScrolladapter.RouterVHViewHolder> {
    private List<String> titles;
    private List<? extends Map<String, ?>> data;
    private Context mContext;
    public void setData(List<? extends Map<String, ?>> data, List<String> titles) {
        this.data = data;
        this.titles = titles;
        notifyDataSetChanged();
    }

    public RouterScrolladapter() {
        titles = new ArrayList<>();
        data = new ArrayList<>();
    }

    @Override
    public RouterVHViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayout = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row_text, parent, false);
        return new RouterVHViewHolder(itemLayout);
    }

    @Override
    public void onBindViewHolder(RouterVHViewHolder holder, int position) {
        View[] views = new View[titles.size()];
        int size = titles.size();
        for (int i = 0; i < size; i++) {
            if (i == 0) {
                continue;
            }
            View rowLayout = View.inflate(mContext, R.layout.item_item_row_adapter, null);
            TextView tvRowShow= (TextView) rowLayout.findViewById(R.id.tv_row_show);
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class RouterVHViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.liner_routerlocal_scroll)
        LinearLayout mLinerRouterlocalScroll;
        @BindView(R.id.vh_routerlocal_scroll)
        VHTableScrollView mVhRouterlocalScroll;

        public RouterVHViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
