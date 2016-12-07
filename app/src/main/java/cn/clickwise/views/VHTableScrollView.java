package cn.clickwise.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;

import cn.clickwise.ui.RouterDetailActivity;

/**
 * 自定义横纵联动列表
 */
public class VHTableScrollView extends HorizontalScrollView {

	RouterDetailActivity activity;
	
	public VHTableScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		activity = (RouterDetailActivity) context;
	}

	
	public VHTableScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		activity = (RouterDetailActivity) context;
	}

	public VHTableScrollView(Context context) {
		super(context);
		activity = (RouterDetailActivity) context;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		//进行触摸赋值
		activity.mTouchView = this;
		return super.onTouchEvent(ev);
	}
	
	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		//当 当前的CHSCrollView被触摸时，滑动其它
		if(activity.mTouchView == this) {
			activity.onScrollChanged(l, t, oldl, oldt);
		}else{
			super.onScrollChanged(l, t, oldl, oldt);
		}
	}
}
