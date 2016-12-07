package cn.clickwise.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebView;
import android.widget.ProgressBar;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.clickwise.R;
import cn.clickwise.bean.UserLoginBackgroundReturn;
import cn.clickwise.config.Constants;
import cn.clickwise.config.RequestUrl;
import cn.clickwise.model.request.RequestManager;
import cn.clickwise.utils.other.ToastUtil;
import cn.clickwise.utils.other.WebViewUtil;

public class RouterLoginActivity extends AppCompatActivity {

    @BindView(R.id.tbar_login_title)
    Toolbar mTbarLoginTitle;
    @BindView(R.id.web_login_login)
    WebView mWebLoginLogin;
    @BindView(R.id.pgb_login_progress)
    ProgressBar mPgbLoginProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_router_login);
        EventBus.getDefault().register(this);
        ButterKnife.bind(this);
        initView();
        loginBackground();
    }

    private void loginBackground() {
        SharedPreferences userInfo = getSharedPreferences(Constants.SAVE_LOGIN_INFO, 0);
        String username = userInfo.getString(Constants.USER_NAME, null);
        String pwd = userInfo.getString(Constants.USER_PWD, null);
        Map<String, String> map = new HashMap<>();
        map.put("user", username);
        map.put("password", pwd);
        RequestManager.getLoginBackground(RequestUrl.URL_login_background, map);
    }

    private void initView() {
        mTbarLoginTitle.setTitle(getString(R.string.router_login));
        //回退
        mTbarLoginTitle.setNavigationIcon(R.drawable.returns);
        mTbarLoginTitle.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Subscriber
    public void loadWebMainEvent(UserLoginBackgroundReturn userLoginBackgroundBean) {
        if (userLoginBackgroundBean.getUrl() != null && userLoginBackgroundBean.getError() == 0) {
            WebViewUtil.setWebView(mWebLoginLogin, mPgbLoginProgress, "http://www.cloudfi.cn"+userLoginBackgroundBean.getUrl());
        } else {
            ToastUtil.make(userLoginBackgroundBean.getMsg());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
