package cn.clickwise.base;

import java.util.List;

/**
 * Created by T420s on 2016/10/21.
 */
public abstract class BaseAdapter extends android.widget.BaseAdapter{
    public abstract void setData(List<? extends Object> data);
    public abstract void addData(List<? extends Object> data);
}
