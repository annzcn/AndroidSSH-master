package cn.clickwise.wxapi;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.clickwise.AppContext;
import cn.clickwise.CommandThread;
import cn.clickwise.R;
import cn.clickwise.adapters.WifiInfoAdapter;
import cn.clickwise.base.BaseActivity;
import cn.clickwise.bean.RouterLocalInfoReturn;
import cn.clickwise.bean.UserLoginReturn;
import cn.clickwise.config.Constants;
import cn.clickwise.config.RequestUrl;
import cn.clickwise.interf.ICheckNet;
import cn.clickwise.interf.ICheckRouterTypeListener;
import cn.clickwise.service.AutoLocationService;
import cn.clickwise.service.OfflineRouterService;
import cn.clickwise.ui.LoginActivity;
import cn.clickwise.ui.MapActivity;
import cn.clickwise.ui.PushMapActivity;
import cn.clickwise.ui.RouterDetailActivity;
import cn.clickwise.ui.RouterLoginActivity;
import cn.clickwise.ui.SignActivity;
import cn.clickwise.ui.WebActivity;
import cn.clickwise.utils.helper.RouteTestHelper;
import cn.clickwise.utils.helper.SDHelper;
import cn.clickwise.utils.helper.ShareHelper;
import cn.clickwise.utils.other.ApkUpdateUtil;
import cn.clickwise.utils.other.MapUtil;
import cn.clickwise.utils.other.NetWorkUtil;
import cn.clickwise.utils.other.TDivce;
import cn.clickwise.utils.other.TimeUtil;
import cn.clickwise.utils.other.ToastUtil;
import cn.clickwise.utils.other.WifiUtil;
import cn.clickwise.utils.sshutils.ConnectionStatusListener;
import cn.clickwise.utils.sshutils.ExecTaskCallbackHandler;
import cn.clickwise.utils.sshutils.SessionController;
import cn.clickwise.utils.sshutils.SessionUserInfo;
import cn.clickwise.views.ProgressView;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.wechat.friends.Wechat;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class RouteActivity extends BaseActivity implements ConnectionStatusListener, ExecTaskCallbackHandler, Toolbar.OnMenuItemClickListener {
    public static final String TAG = "RouteActivity";
    @BindView(R.id.tbar_main_title)
    Toolbar mTbarMainTitle;
    @BindView(R.id.rbtn_route_share)
    RadioButton mRbtnRouteShare;
    @BindView(R.id.img_route_toorbar)
    ImageView mImgRouteToorbar;
    @BindView(R.id.rbtn_route_diagnose)
    RadioButton mRbtnRouteDiagnose;
    @BindView(R.id.rbtn_route_wechat)
    RadioButton mRbtnRouteWechat;
    @BindView(R.id.rbtn_route_phone)
    RadioButton mRbtnRoutePhone;
    @BindView(R.id.coll_route_layout)
    CollapsingToolbarLayout mCollRouteLayout;
    //@BindView(R.id.rec_route_wifinfo)
    //RecyclerView mRecRouteWifinfo;
    @BindView(R.id.pg_route_netspeed)
    ProgressView mPgRouteNetspeed;
    @BindView(R.id.abar_route_layout)
    AppBarLayout mAbarRouteLayout;
    @BindView(R.id.rbtn_route_map)
    RadioButton mRbtnRouteMap;
    @BindView(R.id.rbtn_route_speedtest)
    RadioButton mRbtnRouteSpeedtest;
    @BindView(R.id.tr_route_t1)
    TableRow mTrRouteT1;
    @BindView(R.id.tr_route_t2)
    TableRow mTrRouteT2;
    @BindView(R.id.table_route_layout)
    TableLayout mTableRouteLayout;
    @BindView(R.id.fbtn_route_sign)
    FloatingActionButton mFbtnRouteSign;
    @BindView(R.id.tv_route_offline)
    TextView mTvRouteOffline;

    private SessionUserInfo mSUI;
    private Handler mExecuteHandler = new Handler();
    private Handler mPromptHandler = new Handler();
    private Handler traMainThreadHandler = new Handler();
    // IWXAPI 是第三方app和微信通信的openapi接口
    private IWXAPI api;
    private List<String> mWifiInfo;
    private ProgressDialog mProgressDialog;
    private Receiver mReceiver;
    private WifiInfoAdapter mWifiInfoAdapter;
    //退出标识
    private static boolean isExit = false;
    //取消通知标识
    private static boolean isCancelNotification = true;
    private int mUserType;
    private ProgressDialog mCheckProgress;
    private ArrayList<RouterLocalInfoReturn.Result> mOfflineRouterResult;
    private Map<String, Object> mLocationReturnMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_route);
        ToastUtil.make("登陆并签到成功");
        ButterKnife.bind(this);
        ShareSDK.initSDK(this);//初始化ShareSDK
        //6.0动态申请权限
        RouteActivityPermissionsDispatcher.needPermissionWithCheck(this);
        //注册
        registerReceiver();
        register();
        //初始化
        initView();
        initCtrl();
        checkNetWork();
        initData();
        setListener();
    }

    @Override
    protected void onStart() {
        super.onStart();
        //检查版本是否需要更新
        ApkUpdateUtil.checkVersion(this, traMainThreadHandler);
    }

    @Override
    protected void register() {
        EventBus.getDefault().register(this);
    }

    private void registerReceiver() {
        IntentFilter intentFilter = new IntentFilter(Constants.PROGRESSRESEIVER);
        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        mReceiver = new Receiver();
        registerReceiver(mReceiver, intentFilter);
    }

    /**
     * 开启子线程模拟进度
     */
    private static double simulateProgress = 0;

    private void simulateProgress() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                simulateProgress = 0;
                while (simulateProgress <= Constants.DIAGNOSE_PROGRESS_MAX) {
                    try {
                        Thread.sleep(1000);
                        sendBroadcast(Constants.intentKey, ++simulateProgress);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.login_exit://登录用户退出
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("退出提示")
                        .setMessage("亲爱的小伙伴，请选择操作？")
                        .setPositiveButton("退出用户", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                AppContext.isTenNoLogin = false;
                                AppContext.isLogin = false;
                                clearUserInfo();
                                ToastUtil.make("当前用户已退出");
                                //退出掉线路由通知
                                stopService(new Intent(getContext(), OfflineRouterService.class));
                                //退出实时定位
                                stopService(new Intent(getContext(), AutoLocationService.class));
                                startActivity(new Intent(RouteActivity.this, LoginActivity.class));
                                finish();
                            }
                        }).setNegativeButton("退出应用", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        AppContext.isLogin = false;
                        finish();
                        System.exit(0);
                    }
                }).setNeutralButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
                break;
            case R.id.cancel_notification://取消通知
                if (isCancelNotification) {//取消通知
                    ToastUtil.make("关闭了路由掉线通知");
                    isCancelNotification = !isCancelNotification;
                    stopService(new Intent(this, OfflineRouterService.class));
                } else {
                    ToastUtil.make("打开了路由掉线通知");
                    isCancelNotification = !isCancelNotification;
                    startService(new Intent(this, OfflineRouterService.class));
                }
                break;
        }
        return true;
    }

    private void clearUserInfo() {
        SharedPreferences userInfo = getSharedPreferences(Constants.SAVE_LOGIN_INFO, 0);
        SharedPreferences noLoginTen = getSharedPreferences(Constants.TEN_NO_LOGIN_TIME, 0);
        SharedPreferences.Editor edit = userInfo.edit();
        SharedPreferences.Editor noLoginTenEdit = noLoginTen.edit();
        edit.clear();
        noLoginTenEdit.clear();
        noLoginTenEdit.commit();
        edit.commit();
    }

    Handler exitHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            isExit = false;
        }
    };

    //退出
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void exit() {
        if (!isExit) {
            isExit = true;
            ToastUtil.make("再按一次退出");
            //推迟2s发送消息
            exitHandler.sendEmptyMessageDelayed(0, 2000);
        } else {
            finish();
            System.exit(0);
        }
    }

    //广播监听
    class Receiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //通过广播监听模拟/真实进度
            if (Constants.PROGRESSRESEIVER.equals(intent.getAction()) && mProgressDialog != null) {
                double progress = intent.getDoubleExtra(Constants.intentKey, Constants.DIAGNOSE_PROGRESS_MAX);
                mProgressDialog.setProgress((int) (progress / Constants.DIAGNOSE_PROGRESS_MAX * Constants.PROGRESS_MAX));
                //诊断失败,这里会接收所有的失败信息
                if (simulateProgress == Constants.DIAHNOSE_FAIL) {//若路由器登录失败即诊断失败
                    AppContext.isDiagnoseSuccess = false;//诊断失败
                    AppContext.isTestProgressing = false;
                    AppContext.isLoginFailFirstExecute = false;
                    mProgressDialog.dismiss();
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("诊断提示").setMessage("诊断失败").setNegativeButton("退出", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();
                }
                //诊断完成【注意：在写入日志没有完毕的情况下分享日志会失败，如果进程提早结束，进度条就会不动】
                if (AppContext.isFirstShowShareDialog && simulateProgress == Constants.DIAGNOSE_PROGRESS_MAX) {
                    CommandThread commandThread = new CommandThread();
                    commandThread.start();
                    try {
                        commandThread.join();
                        //诊断完毕断开SSH连接
                        SessionController.getSessionController().disconnect();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mProgressDialog.dismiss();
                    AppContext.isTestProgressing = false;
                    AppContext.isDiagnoseSuccess = true;
                    AppContext.isFirstShowShareDialog = false;
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("分享提示")
                            .setMessage("诊断完毕,是否分享？")
                            .setPositiveButton("是", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    AppContext.isClickShare = true;
                                    shareToOther();
                                }
                            }).setNegativeButton("否", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();
                }
            }
            //监听网络是否可用
            if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())) {
                Parcelable parcelableExtra = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if (parcelableExtra != null) {
                    NetworkInfo networkInfo = (NetworkInfo) parcelableExtra;
                    if (networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_WIFI && networkInfo.getState() == NetworkInfo.State.CONNECTED) {//Wifi连接
                        accordWifiUpdateUI(true, R.mipmap.toorbarpic, WifiUtil.getWifiName(RouteActivity.this));
                        //若Wifi不能用改用数据流量分享
                        if (AppContext.isClickShare) {//点击了分享后才通过可以分享
                            shareToOther();
                        }
                    } else if (networkInfo != null && networkInfo.isAvailable() && networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {//无Wifi连接
                        accordWifiUpdateUI(false, R.mipmap.toorbarpic_no, getResources().getString(R.string.app_name));
                    }
                }
            }
        }
    }

    private void checkNetWork() {
        if (!NetWorkUtil.isWifiConn(this)) {//判断Wifi是否连接
            accordWifiUpdateUI(false, R.mipmap.toorbarpic_no, getString(R.string.app_name));
            //网络提示
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("网络提示")
                    .setIcon(R.mipmap.warn)
                    .setMessage("亲，请连接WiFi,开始一键诊断")
                    .setPositiveButton("设置WiFi", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));//直接进入手机中的Wifi设置界面
                            dialog.dismiss();
                        }
                    }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).show();
        } else {
            accordWifiUpdateUI(true, R.mipmap.toorbarpic, WifiUtil.getWifiName(this));
        }
    }

    /**
     * 根据Wifi的变动更新
     *
     * @param pic
     * @param title
     */
    private void accordWifiUpdateUI(boolean isWifi, int pic, String title) {
        mImgRouteToorbar.setImageResource(pic);
        mCollRouteLayout.setTitle(title);
        if (isWifi) {//Wifi连接
            //更新信号质量UI
            setSpeedProgress();
            mWifiInfo = RouteTestHelper.getWifiInfo_second(this);
        } else {//无Wifi连接
        }
    }

    @Override
    protected void initView() {
        SharedPreferences userInfo = getSharedPreferences(Constants.SAVE_LOGIN_INFO, 0);
        mUserType = userInfo.getInt(Constants.USER_TYPR, -Constants.FAIL_INT);
        if (mUserType == UserLoginReturn.AGENT_PUSH) {//地推人员显示人员分布地图
            Drawable personDrawableTop = getResources().getDrawable(R.drawable.person_map_48);
            mRbtnRouteWechat.setCompoundDrawablesWithIntrinsicBounds(null, personDrawableTop, null, null);
            mRbtnRouteWechat.setText(getString(R.string.push_distribution));
        }
        //设置菜单
        mTbarMainTitle.inflateMenu(R.menu.menu_route);
        //动态设置标头的高度
        ViewGroup.LayoutParams params = mAbarRouteLayout.getLayoutParams();
        int tDivceHeight = TDivce.getTDivceHeight(this);
        params.height = (int) (tDivceHeight * 2.f / 5.f);
        mAbarRouteLayout.setLayoutParams(params);
        //动态设置菜单的大小
        ViewGroup.LayoutParams menuParams = mTableRouteLayout.getLayoutParams();
        menuParams.height = (int) (tDivceHeight * 1.75f / 5.f);
        mTableRouteLayout.setLayoutParams(menuParams);
        mTrRouteT1.setMinimumHeight(menuParams.height / 2);
        mTrRouteT2.setMinimumHeight(menuParams.height / 2);
        mRbtnRouteDiagnose.setMinHeight(menuParams.height / 2);
        mRbtnRouteShare.setMinHeight(menuParams.height / 2);
        mRbtnRouteMap.setMinHeight(menuParams.height / 2);
        mRbtnRoutePhone.setMinHeight(menuParams.height / 2);
        mRbtnRouteSpeedtest.setMinHeight(menuParams.height / 2);
        mRbtnRouteWechat.setMinHeight(menuParams.height / 2);

        //动态设置提示信息控件的大小
        ViewGroup.LayoutParams mapParams = mTvRouteOffline.getLayoutParams();
        mapParams.height = (int) (tDivceHeight * 1.f / 5.f);
        mTvRouteOffline.setLayoutParams(mapParams);
        //动态设置ProgressView的大小
        //ProgressView的高度是AppBarLayout的4/5
        float pgRadius = (int) (tDivceHeight * 2.f / 5.f * 4.f / 5.f / 2);
        //通过设置ProgressView的半径大小来确定其高度
        mPgRouteNetspeed.setRadius(pgRadius);
        //开始扫描
        mPgRouteNetspeed.startScanf();
    }

    private void initData() {
        if (NetWorkUtil.isWifiConn(this)) {
            mWifiInfo = RouteTestHelper.getWifiInfo_second(this);
            //检测网络状况
            mPgRouteNetspeed.setMaxProgress(Constants.WIFILINK_MAXSPEED);
            setSpeedProgress();
        }
    }

    /**
     * 不断更新网络信号状态
     */
    private Handler spHandler = new Handler();

    private void setSpeedProgress() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(500);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    spHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mPgRouteNetspeed.setMaxProgress(4);
                            mPgRouteNetspeed.setCurrentProgress(WifiUtil.getSignalLevel(getContext()));
                            mPgRouteNetspeed.setText(WifiUtil.getWifiLevel(getContext()));
                            setProgressViewByNet();
                        }
                    });
                }
            }
        }).start();
    }

    /**
     * 根据网络状况改变抬头的颜色
     */
    public void setProgressViewByNet() {
        int signalLevel = WifiUtil.getSignalLevel(this);
        switch (signalLevel) {
            case 0:
                setBackgroundColor(getResources().getColor(R.color.fitthlevel));
                break;
            case 1:
                setBackgroundColor(getResources().getColor(R.color.fourthlevel));
                break;
            case 2:
                setBackgroundColor(getResources().getColor(R.color.thirdlevel));
                break;
            case 3:
                setBackgroundColor(getResources().getColor(R.color.secondlevel));
                break;
            case 4:
                setBackgroundColor(getResources().getColor(R.color.firstlevel));
                break;
        }
    }

    private void setBackgroundColor(int colorId) {
        mPgRouteNetspeed.setInnerColor(colorId);//设置这个还不行
        mPgRouteNetspeed.mInnerPaint.setColor(colorId);
        mAbarRouteLayout.setBackgroundColor(colorId);
    }

    @Override
    protected void setListener() {
        mRbtnRouteDiagnose.setOnClickListener(this);
        mRbtnRouteShare.setOnClickListener(this);
        mRbtnRoutePhone.setOnClickListener(this);
        mRbtnRouteWechat.setOnClickListener(this);
        mFbtnRouteSign.setOnClickListener(this);
        mRbtnRouteMap.setOnClickListener(this);
        mRbtnRouteSpeedtest.setOnClickListener(this);
        mTbarMainTitle.setOnMenuItemClickListener(this);
        mTvRouteOffline.setOnClickListener(this);
    }

    private void saveWifiInfoAsyncTask(final List<String> result) {
        //保存Wifi信息
        new Thread(new Runnable() {
            @Override
            public void run() {
                RouteTestHelper.saveLogInfo(result);
            }
        }).start();
    }

    @Override
    protected void initCtrl() {
        mWifiInfoAdapter = new WifiInfoAdapter();
    }

    private void confirmDiagnose() {
        View confirmLayout = View.inflate(this, R.layout.item_dialog_confirm, null);
        RecyclerView confirmWifiInfo = (RecyclerView) confirmLayout.findViewById(R.id.rec_dialogconfirm_wifiinfo);
        confirmWifiInfo.setLayoutManager(new LinearLayoutManager(this));
        confirmWifiInfo.setAdapter(mWifiInfoAdapter);
        mWifiInfoAdapter.setData(RouteTestHelper.getWifiInfo_second(this));
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog alertDialog = builder.create();
        alertDialog.setView(confirmLayout, 50, 0, 50, 10);
        alertDialog.setTitle("诊断提示");
        alertDialog.setMessage("确认诊断此WiFi（注意Mac地址）？");
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "下一步", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //确认后开始诊断
                startDiagnose();
            }
        });
        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.show();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.rbtn_route_share://一键分享
                AppContext.isClickShare = true;
                //第七步：分享到微信
                shareToOther();
                //第八步：测试完毕
                //整个流程都保存到日志
                break;
            case R.id.rbtn_route_diagnose://一键诊断【所有流程在这里走完】
                if (!AppContext.isTestProgressing) {//不在诊断当中才能进入诊断
                    //网络状况检查
                    if (NetWorkUtil.isWifiConn(this)) {//按照顺序来不需要判断
                        //首先检查所连路由器是否为lute
                        mCheckProgress = new ProgressDialog(getContext());
                        mCheckProgress.setMax(Constants.PROGRESS_MAX);
                        mCheckProgress.setMessage("正在验证WiFi，请稍后……");
                        mCheckProgress.show();
                        loginRoute(true);
                    } else {//无Wifi联通
                        //诊断之前再次检查网络+提示
                        checkNetWork();
                    }
                } else {//正在测试之中
                    Snackbar.make(mRbtnRouteDiagnose, "正在诊断之中，请稍后再试", Snackbar.LENGTH_SHORT).show();
                }

                break;
            case R.id.rbtn_route_wechat:
                NetWorkUtil.isNetWork(new ICheckNet() {
                    @Override
                    public void available() {
                        traMainThreadHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (mUserType == UserLoginReturn.AGENT_PUSH) {//地推人员登陆进入人员分布地图
                                    startActivity(new Intent(getContext(), PushMapActivity.class));
                                } else {
                                    if (api == null) {
                                        // 通过WXAPIFactory工厂，获取IWXAPI的实例
                                        api = WXAPIFactory.createWXAPI(getContext(), Constants.APP_ID, false);
                                    }
                                    api.openWXApp();
                                }
                            }
                        });
                    }

                    @Override
                    public void notAvailable() {
                        traMainThreadHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                showNoNetWorkSnackbar();
                            }
                        });
                    }
                });
                break;
            case R.id.rbtn_route_phone://路由登录
                /*Intent intent = new Intent(Intent.ACTION_DIAL);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }*/
                NetWorkUtil.isNetWork(new ICheckNet() {
                    @Override
                    public void available() {
                        traMainThreadHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (AppContext.isLogin) {
                                    Intent intent = new Intent(RouteActivity.this, RouterLoginActivity.class);
                                    intent.putExtra(Constants.ACTIVITY_ROUTERTOLOGIN_URL, RequestUrl.URL_routerLoginOperat);
                                    startActivity(intent);
                                }
                            }
                        });
                    }

                    @Override
                    public void notAvailable() {
                        traMainThreadHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                showNoNetWorkSnackbar();
                            }
                        });
                    }
                });
                break;
            case R.id.rbtn_route_speedtest://测试网速
                NetWorkUtil.isNetWork(new ICheckNet() {
                    @Override
                    public void available() {
                        traMainThreadHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                int apiVersion = Integer.parseInt(Build.VERSION.SDK);
                                Intent webIntent = new Intent(getContext(), WebActivity.class);
                                if (apiVersion >= 21) {//5.0以上
                                    webIntent.putExtra(Constants.ACTIVITY_COMMUNICATION_URL, RequestUrl.testSpeedUrl_high);
                                } else {
                                    webIntent.putExtra(Constants.ACTIVITY_COMMUNICATION_URL, RequestUrl.testSpeedUrl);
                                }
                                startActivity(webIntent);
                            }
                        });
                    }

                    @Override
                    public void notAvailable() {
                        traMainThreadHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                showNoNetWorkSnackbar();
                            }
                        });
                    }
                });
                break;
            case R.id.rbtn_route_map://路由地图
                NetWorkUtil.isNetWork(new ICheckNet() {
                    @Override
                    public void available() {
                        traMainThreadHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                startActivity(new Intent(getContext(), MapActivity.class));
                            }
                        });
                    }

                    @Override
                    public void notAvailable() {
                        traMainThreadHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                showNoNetWorkSnackbar();
                            }
                        });
                    }
                });
                break;
            case R.id.fbtn_route_sign://签到
                NetWorkUtil.isNetWork(new ICheckNet() {
                    @Override
                    public void available() {
                        traMainThreadHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                startActivity(new Intent(getContext(), SignActivity.class));
                            }
                        });
                    }

                    @Override
                    public void notAvailable() {
                        traMainThreadHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                showNoNetWorkSnackbar();
                            }
                        });
                    }
                });
            case R.id.tv_route_offline://掉线路由详情
                if (mLocationReturnMap != null && mOfflineRouterResult != null && mOfflineRouterResult.size() > 0) {
                    EventBus.getDefault().postSticky(mLocationReturnMap);
                    EventBus.getDefault().postSticky(mOfflineRouterResult, MapUtil.TAG_OFFLINE_SUM);
                    Intent newIntent = new Intent(this, RouterDetailActivity.class);
                    startActivity(newIntent);
                }
                break;
        }
    }

    @Subscriber(tag = Constants.SERVICE_TO_ROUTE_LOCATION)
    public void onReceiveMainEvent(Map<String, Object> requestMap) {
        mLocationReturnMap = requestMap;
    }

    private void shareToOther() {
        AppContext.isClickShare = false;
        if (!AppContext.isTestProgressing && AppContext.isDiagnoseSuccess) {//不在诊断中或者说诊断完毕后可以分享
            NetWorkUtil.isNetWork(new ICheckNet() {
                @Override
                public void available() {
                    traMainThreadHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (NetWorkUtil.isWifiNetWork(getContext())) {//优先在Wifi状态下分享，若Wifi不可用则关闭Wifi打开GPGS分享
                                if (SDHelper.isExist()) {
                                    ShareHelper.showShare(getContext(), Wechat.NAME, true);//直接分享到微信
                                } else {
                                    Snackbar.make(mRbtnRouteShare, "诊断文件不存在，请重新诊断", Snackbar.LENGTH_SHORT).show();
                                }
                            } else {//关闭Wifi，打开GPRS分享
                                WifiUtil.operateWifi(false, getContext());//关闭Wifi
                                NetWorkUtil.operateGprs(true, getContext());//打开GPRS
                                //此时分享转到广播监听中，因为网络开启需要一定的时间
                                if (SDHelper.isExist()) {//日志文件在的情况下使用流量分享
                                    Snackbar.make(mRbtnRouteShare, "WiFi连接不可用，请使用GPRS分享", Snackbar.LENGTH_SHORT).setAction("分享", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            ShareHelper.showShare(getContext(), Wechat.NAME, true);
                                        }
                                    }).show();
                                }
                            }
                        }
                    });
                }

                @Override
                public void notAvailable() {
                    traMainThreadHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            showNoNetWorkSnackbar();
                        }
                    });
                }
            });
        } else {
            Snackbar.make(mRbtnRouteShare, "未进行诊断或诊断未完成", Snackbar.LENGTH_SHORT).show();
        }
    }

    private void showNoNetWorkSnackbar() {
        Snackbar.make(mRbtnRouteWechat, "网络连接不可用", Snackbar.LENGTH_SHORT).setAction("设置网络", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));//直接进入手机中的Wifi设置界面
            }
        }).show();
    }

    private Context getContext() {
        return RouteActivity.this;
    }

    //开始诊断
    private void startDiagnose() {
        simulateProgress();
        //每次进来都要初始化这些变量
        AppContext.isFirstShowShareDialog = true;//刚开始诊断就是第一次
        AppContext.isLoginSuccessFirstExecute = true;
        AppContext.isLoginFailFirstExecute = true;
        //测试之中
        AppContext.isTestProgressing = true;
        //收起虚拟键盘
        InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (im != null) {
            im.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
        }
        //进度Dialog
        mProgressDialog = new ProgressDialog(getContext());
        mProgressDialog.setTitle("诊断进度");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        //设置最大进度
        mProgressDialog.setMax(Constants.PROGRESS_MAX);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage("正在拼命诊断中，请耐心等待……");
        mProgressDialog.show();
        /**
         * 终止诊断这个功能逻辑有问题
         */
        //保存日志文件名（日志名+工作人员名字+工作人员电话+日期【中间用空格隔开】）
        SharedPreferences userInfo = getSharedPreferences(Constants.SAVE_LOGIN_INFO, 0);
        String realName = userInfo.getString(Constants.USER_REAL_NAME, null);
        String phone = userInfo.getString(Constants.USER_PHONE, null);
        SDHelper.fileName = realName + " " + phone + " " + TimeUtil.getDate();
        //第二步：读取Wifi信息
        saveWifiInfoAsyncTask(RouteTestHelper.getWifiInfo_second(getContext()));
        //第三步：ping地址并保存（是指没有登录路由器之前在Android设备上进行ping）
        RouteTestHelper.executeCommand(RouteTestHelper.getPingCommandBeforeLogin_third(getContext()), getContext());
        //非此步骤RouteTestHelper.executeCommand(RouteTestHelper.getPingCommandBeforeLogin_third(),mExecuteHandler,RouteActivity.this);
        //第四步：登录SSH
        loginRoute(false);
        //接下来的步骤转移后connected()中进行
        //下一步：连接成功的情况下输入日志信息【转到connected()】
    }

    private void sendBroadcast(String keyStr, double progress) {
        Intent intent = new Intent(Constants.PROGRESSRESEIVER);
        intent.putExtra(keyStr, progress);
        sendBroadcast(intent);
    }

    /**
     * 连接路由器
     */
    public void loginRoute(boolean isCheckRouterType) {
        List<String> loginSSHInfo_fourth = RouteTestHelper.getLoginSSHInfo_fourth(this);
        int port = Integer.valueOf(loginSSHInfo_fourth.get(2));
        mSUI = new SessionUserInfo(loginSSHInfo_fourth.get(0), loginSSHInfo_fourth.get(1),
                loginSSHInfo_fourth.get(3), port);
        SessionController.getSessionController().setUserInfo(mSUI);
        //SSH连接
        SessionController.getSessionController().connect(isCheckRouterType);
        if (isCheckRouterType) {//判断所连路由器是否为lute
            SessionController.getSessionController().setCheckRouterTypeListener(new ICheckRouterTypeListener() {
                @Override
                public void isLuteRouter() {//是 注意：这是异步回调
                    mPromptHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (mCheckProgress != null) {
                                mCheckProgress.dismiss();
                            }
                            //关闭SSH连接
                            try {
                                SessionController.getSessionController().disconnect();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            //第一步：连接Wifi并确再并输入日志信息
                            confirmDiagnose();
                        }
                    });
                }

                @Override
                public void noLuteRouter() {//否
                    mPromptHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            //关闭验证界面，进入网络查看界面
                            if (mCheckProgress != null) {
                                mCheckProgress.dismiss();
                            }
                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                            builder.setTitle("错误提示")
                                    .setMessage("抱歉，请检查所连路由是否为lute？")
                                    .setPositiveButton("关闭", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    }).setNegativeButton("查看网络", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));//直接进入手机中的Wifi设置界面
                                    dialog.dismiss();
                                }
                            }).show();
                        }
                    });
                }
            });
        } else {
            SessionController.getSessionController().setConnectionStatusListener(this);
        }
    }

    @Override
    public void onDisconnected() {
        //if (AppContext.isLoginFailFirstExecute) {
        mPromptHandler.post(new Runnable() {
            @Override
            public void run() {
                //发送失败信息
                sendBroadcast(Constants.intentKey, Constants.DIAHNOSE_FAIL);
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("连接提示").setMessage("SSH连接失败").setNegativeButton("退出", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
            }
        });
        AppContext.isLoginFailFirstExecute = false;
        // }
    }

    @Override
    public void onConnected() {
        //可以监听进度
        //if (AppContext.isLoginSuccessFirstExecute) {
        //连接成功后执行自动Linux命令
        mPromptHandler.post(new Runnable() {
            @Override
            public void run() {
                //第四步：登录SSH
                //第六步：ping地址
                RouteTestHelper.executeCommand(RouteTestHelper.getPingCommandAfterLogin_sixth(), mExecuteHandler, RouteActivity.this);
                //第五步：测试Linux命令
                RouteTestHelper.executeCommand(RouteTestHelper.getTestCommand_fifth(), mExecuteHandler, RouteActivity.this);
                AppContext.isTestProgressing = false;//诊断完毕
            }
        });
        AppContext.isLoginSuccessFirstExecute = false;
        // }
    }

    //命令执行失败返回
    @Override
    public void onFail() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("执行提示").setMessage("命令未执行完毕").show();
    }

    //命令执行成功返回
    @Override
    public void onComplete(String completeString) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("执行提示").setMessage("命令执行完毕").show();
    }

    @Override
    protected void unregister() {
        unregisterReceiver(mReceiver);
        EventBus.getDefault().unregister(this);
    }

    //掉线路由返回结果
    @Subscriber
    public void offlineRouterResultMainEvent(List<RouterLocalInfoReturn.Result> result) {
        if (result != null && result.size() > 0) {
            mOfflineRouterResult = (ArrayList<RouterLocalInfoReturn.Result>) result;
            int offlineRouterSum = mOfflineRouterResult.size();
            if (mLocationReturnMap != null) {
                String describe = (String) mLocationReturnMap.get(Constants.MAP_DESCRIBE);
                mTvRouteOffline.setVisibility(View.VISIBLE);
                /*mTvRouteOffline.setFocusable(true);
                mTvRouteOffline.requestFocus();
                mTvRouteOffline.setSelected(true);*/
                mTvRouteOffline.setText("当前位置：" + describe + "   提示:你附近有" + offlineRouterSum + "台掉线路由器，点击查看详情");
            } else {
                mTvRouteOffline.setVisibility(View.GONE);
            }
        } else {
            ToastUtil.make("数据请求失败");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregister();
    }

    @NeedsPermission({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void needPermission() {
    }

    /**
     * 6.0权限
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        RouteActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @OnShowRationale({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void onShowRatonale(final PermissionRequest request) {
    }

    @OnPermissionDenied({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void denied() {
    }

    @OnNeverAskAgain({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void neverAskAgain() {
    }

}
