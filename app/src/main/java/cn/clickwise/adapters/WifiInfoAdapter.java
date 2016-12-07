package cn.clickwise.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import cn.clickwise.R;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by T420s on 2016/10/27.
 */
public class WifiInfoAdapter extends RecyclerView.Adapter<WifiInfoAdapter.Holder> {
    private List<String> data;
    public void setData(List<String> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    public WifiInfoAdapter() {
        data=new ArrayList<>();
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.text_item, parent, false);
        return new Holder(inflate);
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        holder.mTv.setText(data.get(position));
    }

    @Override
    public int getItemCount() {
        int count=0;
        if (data != null) {
            count=data.size();
        }
        return count;
    }

    class Holder extends RecyclerView.ViewHolder{

        public   TextView mTv;

        public Holder(View itemView) {
            super(itemView);
            mTv = (TextView) itemView.findViewById(R.id.tv_item_cmd);
        }
    }
}
