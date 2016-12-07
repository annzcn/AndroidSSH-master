package cn.clickwise.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.clickwise.R;
import cn.clickwise.config.Constants;
import cn.clickwise.utils.other.CalculateUtil;
import cn.clickwise.utils.other.FormatUtil;
import cn.clickwise.utils.other.ToastUtil;
import cn.clickwise.views.VHTableScrollView;

/**
 * Created by T420s on 2016/11/10.
 */
public class RouterLocalBaseAdapter extends BaseAdapter {
    private List<String> titles;
    private List<? extends Map<String, ?>> data;
    private List<VHTableScrollView> mHScrollViews;
    private Context mContext;
    private ListView mListView;

    private Map<Integer, Boolean> mapTag;
    private Map<Integer, Boolean> mapClickTag;

    public void setData(List<Map<String, String>> data, List<String> titles) {
        this.data = CalculateUtil.sortMap(data);
        this.titles = titles;
        mapTag = new HashMap<>();
        mapClickTag = new HashMap<>();
        init();
        notifyDataSetChanged();
    }

    private void init() {
        int size = data.size();
        for (int position = 0; position < size; position++) {
            mapTag.put(position, false);//表示该position的item还没有显示或刚开始显示
            mapClickTag.put(position, false);
        }
    }

    public RouterLocalBaseAdapter(Context mContext, List<VHTableScrollView> mHScrollViews, ListView mListView) {
        this.mContext = mContext;
        this.mHScrollViews = mHScrollViews;
        this.mListView = mListView;
        titles = new ArrayList<>();
        data = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        mapTag.put(position, true);//表示该position的item开始显示
        RouterViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row_text, parent, false);
            viewHolder = new RouterViewHolder(convertView);
            //TextView容器
            TextView[] views = new TextView[titles.size()];
            int size = titles.size();
            for (int i = 0; i < size; i++) {
                //子布局 纵行
                View rowLayout = View.inflate(mContext, R.layout.item_item_row_adapter, null);
                TextView tvRowShow = (TextView) rowLayout.findViewById(R.id.tv_row_show);
                //将LinearLayout加入到父类LinearLayout,有多少标题加多少个
                viewHolder.mLinerRouterlocalScroll.addView(rowLayout);
                views[i] = tvRowShow;
            }
            convertView.setTag(views);
            addHViews(viewHolder.mVhRouterlocalScroll);
        }
        TextView[] holders = (TextView[]) convertView.getTag();
        int length = holders.length;
        for (int i = 0; i < length; i++) {
            //根据title加载数据
            String contentStr = data.get(position).get(titles.get(i)).toString();
            holders[i].setText(contentStr);
            if (mapTag.get(position)) {//防止条目错乱
                //改变点击状态只需改变路由器的状态
                if (Constants.ROUTERLOCAL_RECEIVE_TASK.equals(contentStr) && Constants.ROUTERLOCAL_STATE_OFFLINE.equals(data.get(position).get(Constants.ROUTERLOCAL_STATE).toString())) {
                    //holders[i].setTextColor(Color.RED);
                    //holders[i].setId(R.id.list_adapter_task_id);
                    holders[i].setTextAppearance(mContext, R.style.item_textViewStyle);
                    holders[i].setEnabled(true);
                    holders[i].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //ToastUtil.make("onClick: item item" + data.get(position).get(Constants.ROUTERLOCAL_MAC).toString());
                            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                            builder.setTitle("提示")
                                    .setMessage("小伙伴，确定领取此任务？\r\n\nMac地址：" + data.get(position).get(Constants.ROUTERLOCAL_MAC).toString() + "\r\n商\t\t户：" + data.get(position).get(Constants.ROUTERLOCAL_MERCHANT).toString() + "\r\n代 理 商：" + data.get(position).get(Constants.ROUTERLOCAL_AGENT).toString() + "\r\n距\t\t离：" + data.get(position).get(Constants.ROUTERLOCAL_DISTANCE).toString())
                                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).show();
                        }
                    });
                    //holders[i].setOnClickListener(new ReceiveTaskClickListener(position));
                }
                if (Constants.ROUTERLOCAL_RECEIVE_TASK.equals(contentStr) && Constants.ROUTERLOCAL_STATE_ONLINE.equals(data.get(position).get(Constants.ROUTERLOCAL_STATE).toString())) {
                    holders[i].setText(Constants.ROUTERLOCAL_NO_RECEIVE_TASK);
                    holders[i].setEnabled(false);
                    holders[i].setTextAppearance(mContext, R.style.item_textViewStyle);
                    holders[i].setTextColor(Color.BLUE);
                }
                //如果内容是离线则标记为红色
                if (Constants.ROUTERLOCAL_STATE_OFFLINE.equals(contentStr)) {
                    holders[i + 1].setTextColor(Color.RED);
                    holders[i].setTextColor(Color.RED);
                }
                //在线设置为绿色
                if (Constants.ROUTERLOCAL_STATE_ONLINE.equals(contentStr)) {
                    holders[i + 1].setTextColor(Color.BLUE);
                    holders[i].setTextColor(Color.BLUE);
                }
                //如果是电话就auto
                if (FormatUtil.isPhone(contentStr)) {
                    holders[i].setAutoLinkMask(Linkify.PHONE_NUMBERS);
                    //必须有这个设置所有的才能auto phone，不然只有第一个
                    holders[i].setMovementMethod(LinkMovementMethod.getInstance());
                }
                //为了显示全部商家名称，使用“跑马灯”
                if (data.get(position).containsKey(Constants.ROUTERLOCAL_MERCHANT)) {
                    //ToastUtil.make("商户名");
                /*TextView tv=holders[i];
                LinearLayout parents = (LinearLayout) tv.getParent();
                ViewGroup.LayoutParams params=parents.getLayoutParams();
                params.width=20;
                params.height=parents.getWidth();
                parents.setLayoutParams(params);*/
                /*tv.setSingleLine();
                tv.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                tv.setMarqueeRepeatLimit(-1);
                tv.setFocusable(true);
                tv.setFocusableInTouchMode(true);*/
                }
            }
        }
        mapTag.put(position, true);//显示完毕之后置为true
        return convertView;
    }

    class ReceiveTaskClickListener implements View.OnClickListener {
        private int position;

        public ReceiveTaskClickListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            TextView textView = (TextView) v;
            if (mapClickTag.get(position)) {
                mapClickTag.put(position, false);
                ToastUtil.make("点击了--》" + textView.getText().toString());
            } else {
                mapClickTag.put(position, true);
                //ToastUtil.make("点击了--》"+textView.getText().toString());
            }
        }
    }

    public void addHViews(final VHTableScrollView hScrollView) {
        if (!mHScrollViews.isEmpty()) {
            int size = mHScrollViews.size();
            VHTableScrollView scrollView = mHScrollViews.get(size - 1);
            final int scrollX = scrollView.getScrollX();
            //第一次满屏后，向下滑动，有一条数据在开始时未加入
            // if (scrollX != 0) {
            mListView.post(new Runnable() {
                @Override
                public void run() {
                    //当listView刷新完成之后，把该条移动到最终位置
                    hScrollView.scrollTo(scrollX, 0);
                }
            });
            //}
        }
        mHScrollViews.add(hScrollView);
    }

    class RouterViewHolder {
        @BindView(R.id.liner_routerlocal_scroll)
        LinearLayout mLinerRouterlocalScroll;
        @BindView(R.id.vh_routerlocal_scroll)
        VHTableScrollView mVhRouterlocalScroll;

        RouterViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
