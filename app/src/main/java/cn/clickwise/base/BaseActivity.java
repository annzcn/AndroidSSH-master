package cn.clickwise.base;

import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Created by T420s on 2016/10/21.
 */
public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener {
    protected abstract void initView();

    protected void initLayout() {
    }

    protected void register() {
    }

    protected void unregister() {
    }

    protected abstract void setListener();

    protected void initCtrl() {
    }

    protected void setAdapters() {
    }

}
