package cn.clickwise.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.clickwise.R;

/**
 * Created by T420s on 2016/11/8.
 */
public class MenuAdapter extends BaseAdapter {
    private List<String> data;
    private Context mContext;

    public MenuAdapter(Context context) {
        this.data = new ArrayList<>();
        mContext = context;
    }

    @Override
    public int getCount() {
        int size = 0;
        if (data != null) {
            size = data.size();
        }
        return size;
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressWarnings("unchecked")
    @Deprecated
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_popwindow_menu, parent, false);
            viewHolder.tvItem = (TextView) convertView.findViewById(R.id.tv_popmenu_menu);
            viewHolder.tvItem.setClickable(true);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.tvItem.setText(data.get(position));
        return convertView;
    }

    class ViewHolder {
        TextView tvItem;
    }

    public void setData(List<String> data) {
        this.data = data;
        notifyDataSetChanged();
    }

}
