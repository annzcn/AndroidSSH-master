package cn.clickwise.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.clickwise.AppContext;
import cn.clickwise.R;
import cn.clickwise.base.BaseActivity;
import cn.clickwise.bean.UserLoginReturn;
import cn.clickwise.config.Constants;
import cn.clickwise.config.RequestUrl;
import cn.clickwise.interf.ICheckLogin;
import cn.clickwise.interf.ICheckNet;
import cn.clickwise.model.request.RequestManager;
import cn.clickwise.service.AutoLocationService;
import cn.clickwise.service.OfflineRouterService;
import cn.clickwise.utils.other.MD5Util;
import cn.clickwise.utils.other.NetWorkUtil;
import cn.clickwise.utils.other.ToastUtil;
import cn.clickwise.wxapi.RouteActivity;

public class LoginActivity extends BaseActivity implements CompoundButton.OnCheckedChangeListener {

    @BindView(R.id.tbar_login_title)
    Toolbar mTbarLoginTitle;
    @BindView(R.id.et_login_username)
    EditText mEtLoginUsername;
    @BindView(R.id.et_login_pwd)
    EditText mEtLoginPwd;
    @BindView(R.id.cbx_login_remberpwd)
    CheckBox mCbxLoginRemberpwd;
    @BindView(R.id.cbx_login_nologin)
    CheckBox mCbxLoginNologin;
    @BindView(R.id.btn_login_login)
    Button mBtnLoginLogin;
    private Map<String, String> mRequestMap;
    private ProgressDialog mLoginProgress;
    private Handler tranMainThreadHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        register();
        initView();
        setListener();
        initData();
    }

    @Override
    protected void register() {
        EventBus.getDefault().register(this);
    }

    @Override
    protected void unregister() {
        EventBus.getDefault().unregister(this);
    }

    private void initData() {
        mRequestMap = new HashMap<>();
    }

    @Override
    protected void initView() {
        mTbarLoginTitle.setTitle(getString(R.string.welcome_login));
        SharedPreferences loginInfo = getSharedPreferences(Constants.SAVE_LOGIN_INFO, 0);
        String username = loginInfo.getString(Constants.USER_NAME, null);
        String pwd = loginInfo.getString(Constants.USER_PWD, null);
        if (username != null && username.length() > 0) {
            mEtLoginUsername.setText(username);
        }
        if (pwd != null && pwd.length() > 0) {
            mEtLoginPwd.setText(pwd);
            mCbxLoginRemberpwd.setChecked(true);
        }
        if (AppContext.isTenNoLogin) {
            SharedPreferences tenNoLogin = getSharedPreferences(Constants.TEN_NO_LOGIN_TIME, 0);
            long chsTime = tenNoLogin.getLong(Constants.CHS_TIME, 0);
            if (chsTime != 0) {
                mCbxLoginNologin.setChecked(true);
            }
        }
    }

    @Override
    protected void setListener() {
        mBtnLoginLogin.setOnClickListener(this);
        mCbxLoginRemberpwd.setOnCheckedChangeListener(this);
        mCbxLoginNologin.setOnCheckedChangeListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login_login://登录
                if (isEmpty(mEtLoginUsername)) {
                    mEtLoginUsername.setError("帐号输入不能为空");
                    mEtLoginUsername.requestFocus();
                    return;
                }
                if (isEmpty(mEtLoginPwd)) {
                    mEtLoginPwd.setError("密码输入不能为空");
                    mEtLoginPwd.requestFocus();
                    return;
                }
                SharedPreferences loginInfo = getSharedPreferences(Constants.SAVE_LOGIN_INFO, 0);
                SharedPreferences.Editor edit = loginInfo.edit();
                edit.putString(Constants.USER_NAME, mEtLoginUsername.getText().toString().trim());
                edit.commit();
                NetWorkUtil.isNetWork(new ICheckNet() {
                    @Override
                    public void available() {
                        tranMainThreadHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                String username = mEtLoginUsername.getText().toString().trim();
                                String inputPwd = mEtLoginPwd.getText().toString().trim();
                                String encryptPwd = MD5Util.encryption(inputPwd);
                                mRequestMap.put("account", username);
                                mRequestMap.put("password", encryptPwd);
                                mLoginProgress = new ProgressDialog(LoginActivity.this);
                                RequestManager.getLoginResult(mLoginProgress, RequestUrl.URL_login, mRequestMap);
                                //保存登陆信息
                                SharedPreferences loginInfo = getSharedPreferences(Constants.SAVE_LOGIN_INFO, 0);
                                SharedPreferences.Editor edit = loginInfo.edit();
                                edit.putString(Constants.USER_NAME, mEtLoginUsername.getText().toString().trim());
                                edit.commit();
                            }
                        });
                    }

                    @Override
                    public void notAvailable() {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Snackbar.make(mBtnLoginLogin, "网络连接不可用", Snackbar.LENGTH_SHORT).setAction("设置网络", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));//直接进入手机中的Wifi设置界面
                                    }
                                }).show();
                            }
                        });
                    }
                });
                break;
        }
    }

    private Handler mHandler = new Handler();

    private boolean isEmpty(EditText etLoginUsername) {
        boolean isEmpty = true;
        String inputStr = etLoginUsername.getText().toString().trim();
        if (inputStr != null && inputStr.length() > 0) {
            isEmpty = false;
        }
        return isEmpty;
    }

    @Subscriber
    public void loginResultMainEvent(UserLoginReturn userLoginBean) {
        if (userLoginBean != null && userLoginBean.getState().equals(Constants.REQUEST_ROUTERLOCAL_RESULT_SUCCESS)) {
            saveUserInfo(userLoginBean);
            AppContext.isLogin = true;
            startActivity(new Intent(this, RouteActivity.class));
            //ToastUtil.make("登陆成功");
            if (userLoginBean.getResult().getType() == UserLoginReturn.AGENT_PUSH) {
                //地推人员实时定位上传
                startService(new Intent(this, AutoLocationService.class));
                //启动掉线路由定位服务
                startService(new Intent(this, OfflineRouterService.class));
            }
            mLoginProgress.dismiss();
            finish();
        } else {
            mLoginProgress.dismiss();
            ToastUtil.make("抱歉，登陆失败");
            if (mCbxLoginRemberpwd.isChecked()) {
                mCbxLoginRemberpwd.setChecked(false);
            }
        }
    }

    private void saveUserInfo(UserLoginReturn userLoginBean) {
        UserLoginReturn.Result result = userLoginBean.getResult();
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SAVE_LOGIN_INFO, 0);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putString(Constants.USER_ID, result.getId());
        edit.putString(Constants.USER_REAL_NAME, result.getName());
        edit.putString(Constants.USER_PHONE, result.getPhone());
        edit.putInt(Constants.USER_TYPR, result.getType());
        edit.commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregister();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int id = buttonView.getId();
        SharedPreferences loginInfo = getSharedPreferences(Constants.SAVE_LOGIN_INFO, 0);
        final SharedPreferences.Editor edit = loginInfo.edit();
        switch (id) {
            case R.id.cbx_login_remberpwd:
                if (isChecked) {
                    //保存登陆信息
                    if (!isEmpty(mEtLoginPwd) && !isEmpty(mEtLoginUsername)) {
                        edit.putString(Constants.USER_NAME, mEtLoginUsername.getText().toString().trim());
                        edit.putString(Constants.USER_PWD, mEtLoginPwd.getText().toString().trim());
                        edit.commit();
                    } else {
                        mEtLoginPwd.setError("用户名或密码输入不能为空");
                        mEtLoginUsername.requestFocus();
                        mEtLoginPwd.requestFocus();
                        mCbxLoginRemberpwd.setChecked(false);
                        return;
                    }
                } else {
                    edit.clear();
                    edit.commit();
                }
                break;
            case R.id.cbx_login_nologin://十天内免登陆
                SharedPreferences sharedPreferences = getSharedPreferences(Constants.TEN_NO_LOGIN_TIME, 0);
                final SharedPreferences.Editor noLoginEdit = sharedPreferences.edit();
                if (isChecked) {
                    if (isEmpty(mEtLoginUsername)) {
                        mEtLoginUsername.setError("用户名输入不能为空");
                        mEtLoginUsername.requestFocus();
                        mCbxLoginNologin.setChecked(false);
                        return;
                    }
                    if (isEmpty(mEtLoginPwd)) {
                        mEtLoginPwd.setError("密码输入不能为空");
                        mEtLoginPwd.requestFocus();
                        mCbxLoginNologin.setChecked(false);
                        return;
                    }
                    String username = mEtLoginUsername.getText().toString().trim();
                    String inputPwd = mEtLoginPwd.getText().toString().trim();
                    String encryptPwd = MD5Util.encryption(inputPwd);
                    mRequestMap.put("account", username);
                    mRequestMap.put("password", encryptPwd);
                    RequestManager.getCheckLoginResult(null, RequestUrl.URL_login, mRequestMap, new ICheckLogin() {
                        @Override
                        public void checkLoginSuccess() {
                            //选中免登陆就自动选中记住密码
                            mCbxLoginRemberpwd.setChecked(true);
                            edit.putString(Constants.USER_PWD, mEtLoginPwd.getText().toString().trim());
                            edit.commit();
                            //保存免登陆操作时间
                            noLoginEdit.putLong(Constants.CHS_TIME, System.currentTimeMillis());
                            noLoginEdit.commit();
                        }

                        @Override
                        public void checkLoginFail() {
                            mCbxLoginNologin.setChecked(false);
                            ToastUtil.make("账号或密码错误，免登陆失败");
                        }
                    });

                } else {
                    noLoginEdit.clear();
                    noLoginEdit.commit();
                }
                break;
        }
    }
}
