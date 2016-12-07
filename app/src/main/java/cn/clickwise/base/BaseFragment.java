package cn.clickwise.base;

import android.app.Fragment;
import android.view.View;

/**
 * Created by lvyang on 2016/11/29.
 */
public abstract class BaseFragment extends Fragment implements View.OnClickListener {
    protected abstract void initView();

    protected abstract void setListener();

    protected void register() {
    }

    protected void unregister() {
    }
}
