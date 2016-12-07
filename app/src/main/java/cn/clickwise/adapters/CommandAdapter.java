package cn.clickwise.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.clickwise.R;

/**
 * Created by T420s on 2016/10/21.
 */
public class CommandAdapter extends BaseAdapter {
    private List<String> data;
    private Context mContext;

    public CommandAdapter(Context context) {
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
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.text_item, parent, false);
            viewHolder.tvItem = (TextView) convertView.findViewById(R.id.tv_item_cmd);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
       // if (position == 1) {//给Mac地址加粗
//            SpannableStringBuilder sb=new SpannableStringBuilder(data.get(position));
//            StyleSpan bss=new StyleSpan(Typeface.BOLD_ITALIC);
//            sb.setSpan(bss,0,data.get(position).length()-1, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
//            viewHolder.tvItem.setTextSize(50);
//            viewHolder.tvItem.setText(data.get(position));
//        }else{
            viewHolder.tvItem.setText(data.get(position));
//        }
        return convertView;
    }

    class ViewHolder {
        TextView tvItem;
    }

    public void setData(List<String> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    public void addData(List<String> data) {
        this.data.addAll(data);
    }
}
