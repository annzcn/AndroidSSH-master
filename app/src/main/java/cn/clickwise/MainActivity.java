package cn.clickwise;

import android.Manifest;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.clickwise.adapters.CommandAdapter;
import cn.clickwise.base.BaseActivity;
import cn.clickwise.config.Constants;
import cn.clickwise.dialogs.SshConnectFragmentDialog;
import cn.clickwise.utils.helper.SDHelper;
import cn.clickwise.utils.helper.ShareHelper;
import cn.clickwise.utils.other.NetWorkUtil;
import cn.clickwise.utils.other.TimeUtil;
import cn.clickwise.utils.other.ToastUtil;
import cn.clickwise.utils.sshutils.ConnectionStatusListener;
import cn.clickwise.utils.sshutils.ExecTaskCallbackHandler;
import cn.clickwise.utils.sshutils.SessionController;
import cn.clickwise.views.SshEditText;
import cn.sharesdk.framework.ShareSDK;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

/**
 * Main activity. Connect to SSH server and launch command shell.
 */
@RuntimePermissions
public class MainActivity extends BaseActivity implements OnClickListener {

    private static final String TAG = "MainActivity";
    @BindView(R.id.fbtn_main_trunwx)
    FloatingActionButton mFbtnMainTrunwx;
    @BindView(R.id.tbar_main_title)
    Toolbar mTbarMainTitle;
    @BindView(R.id.fbtn_main_share)
    FloatingActionButton mFbtnMainShare;
    @BindView(R.id.fbtn_main_truntp)
    FloatingActionButton mFbtnMainTruntp;
    @BindView(R.id.rbtn_main_connrouter)
    RadioButton mRbtnMainConnrouter;
    @BindView(R.id.rbtn_main_closerouter)
    RadioButton mRbtnMainCloserouter;
    @BindView(R.id.rbtn_main_savelog)
    RadioButton mRbtnMainSavelog;
    EditText mEtDialoglogLogname;
    TextView mTvDialoglogSavetime;
    private SshEditText mCommandEdit;
    private Handler mHandler;
    private Handler mTvHandler;
    private String mLastLine;
    private TextView mTvMainChoose;
    private PopupWindow mPopupWindow;
    private CommandAdapter mCommandAdapter;
    private ListView mLvMainChoose;
    private View mInflate;
    private List<String> mCommands;
    private SshConnectFragmentDialog mSshConnDialog;
    // IWXAPI 是第三方app和微信通信的openapi接口
    private IWXAPI api;
    private View mItemDialogLayLog;
    private AlertDialog.Builder mDialogLogBuilder;
    private Spinner mSpDialoglogSavetype;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        //加载布局
        setContentView(R.layout.activity_main);
        //动态读取权限
        MainActivityPermissionsDispatcher.getFileWithCheck(this);
        //初始化ShareSDK
        ShareSDK.initSDK(this);
        //初始化控件
        ButterKnife.bind(this);
        //具体操作方法
        initLayout();
        initView();
        initData();
        initCtrl();
        setAdapters();
        setListener();
    }
    @Override
    protected void initCtrl() {
        mCommandAdapter = new CommandAdapter(this);
    }

    @Override
    protected void setAdapters() {
        mCommandAdapter.setData(mCommands);
        mLvMainChoose.setAdapter(mCommandAdapter);
    }

    private void initData() {
        String[] commands = getResources().getStringArray(R.array.command);
        //命令数据
        mCommands = new ArrayList<>();
        mCommands.add("ifconfig");
        mCommands.add("ifconfig");
        mCommands.add("ifconfig");
        //初始进入显示未连接状态
        mTbarMainTitle.setLogo(R.mipmap.closestatus_48);
    }

    /**
     * Displays toast to user.
     *
     * @param text
     */

    private void makeToast(int text) {
        Toast.makeText(this, getResources().getString(text), Toast.LENGTH_SHORT).show();
    }

    /**
     * Start activity to do SFTP transfer. User will choose from list of files
     * to transfer.
     */
    private void startSftpActivity() {
        Intent intent = new Intent(this, FileListActivity.class);
        String[] info = {
                SessionController.getSessionController().getSessionUserInfo().getUser(),
                SessionController.getSessionController().getSessionUserInfo().getHost(),
                SessionController.getSessionController().getSessionUserInfo().getPassword()
        };

        intent.putExtra("UserInfo", info);

        startActivity(intent);
    }

    /**
     * @return
     */
    private String getLastLine() {
        int index = mCommandEdit.getText().toString().lastIndexOf("\n");
        if (index == -1) {
            return mCommandEdit.getText().toString().trim();
        }
        if (mLastLine == null) {
            Toast.makeText(this, "no text to process", Toast.LENGTH_LONG);
            return "";
        }
        String[] lines = mLastLine.split(Pattern.quote(mCommandEdit.getPrompt()));
        String lastLine = mLastLine.replace(mCommandEdit.getPrompt().trim(), "");
        Log.d(TAG, "command is " + lastLine + ", prompt is  " + mCommandEdit.getPrompt());
        return lastLine.trim();
    }

    private String getSecondLastLine() {

        String[] lines = mCommandEdit.getText().toString().split("\n");
        if (lines == null || lines.length < 2) return mCommandEdit.getText().toString().trim();

        else {
            int len = lines.length;
            String ln = lines[len - 2];
            return ln.trim();
        }
    }

    /**
     * Checks if the EditText is empty.
     *
     * @param editText
     * @return true if empty
     */
    private boolean isEditTextEmpty(EditText editText) {
        if (editText.getText() == null || editText.getText().toString().equalsIgnoreCase("")) {
            return true;
        }
        return false;
    }

    public void onClick(View v) {
        if (v == mRbtnMainConnrouter) {//连接路由
            if (NetWorkUtil.isWifiConn(this)) {
                if (!AppContext.isTestConnFlag) {
                    showDialog();
                } else {
                    Snackbar.make(mRbtnMainConnrouter, "已经连接路由", Snackbar.LENGTH_SHORT)/*.setAction("断开", new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (SessionController.isConnected()) {
                                try {
                                    SessionController.getSessionController().disconnect();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                //将日志信息输入对话框第一次显示设置为false，可以再次显示
                            }
                        }
                    })*/.show();
                }

            } else {
                //网络提示
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("网络提示")
                        .setIcon(R.mipmap.warn)
                        .setMessage("亲，请连接WiFi或保持WiFi可用")
                        .setPositiveButton("设置WiFi", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));//直接进入手机中的Wifi设置界面
                                dialog.dismiss();
                            }
                        }).setNegativeButton("退出", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
            }
        } /*else if (v == mSftpButton) {
            if (SessionController.isConnected()) {
                startSftpActivity();
            }
        } */ else if (v == this.mRbtnMainCloserouter) {//关闭路由
            try {
                if (SessionController.isConnected()) {
                    SessionController.getSessionController().disconnect();
                    //将日志信息输入对话框第一次显示设置为false，可以再次显示
                    AppContext.isDialogLogShow = false;
                } else {
                    //Toast.makeText(MainActivity.this, "路由器未连接", Toast.LENGTH_SHORT).show();
                    Snackbar.make(mRbtnMainCloserouter, "亲，路由器未连接", Snackbar.LENGTH_SHORT).setAction("连接", new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showDialog();
                        }
                    }).show();
                }
            } catch (Throwable t) { //catch everything!
                Log.e(TAG, "Disconnect exception " + t.getMessage());
            }
        } else if (v == mTvMainChoose) {//选择命令
            if (mPopupWindow == null) {
                /**
                 * 注意：：这儿需要适配
                 */
                mPopupWindow = new PopupWindow(mInflate, 250, WindowManager.LayoutParams.WRAP_CONTENT, true);
            }
            //监听命令
            mLvMainChoose.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String command = (String) parent.getItemAtPosition(position);
                    mCommandEdit.setText(command);
                    if (mSshConnDialog != null) {
                        wifiTest();
                    }
                    if (mPopupWindow != null) {
                        mPopupWindow.dismiss();
                    }
                }
            });
            //检查是否处在连接状态
            if (AppContext.isTestConnFlag) {
                mPopupWindow.showAsDropDown(mTvMainChoose);
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("连接提示")
                        .setIcon(R.mipmap.warn)
                        .setMessage("亲，路由器未连接")
                        .setPositiveButton("连接", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                showDialog();
                            }
                        }).setNegativeButton("退出", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
            }
        } else if (v == mFbtnMainTrunwx) {//跳转微信聊天
            if (api == null) {
                // 通过WXAPIFactory工厂，获取IWXAPI的实例
                api = WXAPIFactory.createWXAPI(this, Constants.APP_ID, false);
            }
            api.openWXApp();
        } else if (v == mFbtnMainShare) {//悬浮分享按钮
            ShareHelper.showShare(this, null, true);
        } else if (v == mFbtnMainTruntp) {//调用电话界面
            Intent intent = new Intent(Intent.ACTION_DIAL);
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }
        } else if (v == mRbtnMainSavelog) {//启动保存日志

        }
    }

    //显示连接状况
    void showDialog() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        //Fragment prev = getFragmentManager().findFragmentByTag("dialog");
        ft.addToBackStack(null);
        //监听是否连接成功
        mSshConnDialog.setListener(new ConnectionStatusListener() {
            @Override
            public void onDisconnected() {
                mTvHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        AppContext.isTestConnFlag = false;
                        Toast.makeText(MainActivity.this, "连接失败", Toast.LENGTH_SHORT).show();
                        mTbarMainTitle.setLogo(R.mipmap.closestatus_48);
                    }
                });
            }

            //连接成功
            @Override
            public void onConnected() {
                mTvHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        //连接标志
                        AppContext.isTestConnFlag = true;
                        Toast.makeText(MainActivity.this, "连接成功", Toast.LENGTH_SHORT).show();
                        mTbarMainTitle.setLogo(R.mipmap.connstatus_18_2);
                        if (!AppContext.isDialogLogShow) {
                            //连接成功后输入日志名
                            if (mDialogLogBuilder == null) {
                                mDialogLogBuilder = new AlertDialog.Builder(MainActivity.this);
                            }
                            final String date = TimeUtil.getDate();
                            mTvDialoglogSavetime.setText("操作时间：" + date);
                            mDialogLogBuilder.setIcon(R.mipmap.savelog_48).setTitle("请输入日志信息").setView(mItemDialogLayLog).setPositiveButton("保存", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (mEtDialoglogLogname != null && !mEtDialoglogLogname.getText().toString().equals("")) {
                                        //保存日志文件名
                                        SDHelper.fileName = mEtDialoglogLogname.getText().toString() + date;
                                        dialog.dismiss();
                                    }
                                }
                            }).setNegativeButton("退出", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).show();
                            AppContext.isDialogLogShow = true;
                        }
                    }
                });
            }
        });
        mSshConnDialog.show(ft, "dialog");
    }

    @Override
    protected void initView() {
        mTbarMainTitle.setLogo(R.mipmap.closestatus_48);
        mTbarMainTitle.inflateMenu(R.menu.toptitle);
        setSupportActionBar(mTbarMainTitle);
        //mSftpButton = (Button) findViewById(R.id.sftpbutton);
        mCommandEdit = (SshEditText) findViewById(R.id.command);
        mTvMainChoose = (TextView) findViewById(R.id.tv_main_choose);
        mEtDialoglogLogname = (EditText) mItemDialogLayLog.findViewById(R.id.et_dialoglog_logname);
        mSpDialoglogSavetype = (Spinner) mItemDialogLayLog.findViewById(R.id.sp_dialoglog_savetype);
        //命令行
        mLvMainChoose = (ListView) mInflate.findViewById(R.id.lv_main_choose);

        // Create and show the dialog.
        mSshConnDialog = SshConnectFragmentDialog.newInstance();
    }

    @Override
    protected void initLayout() {
        mItemDialogLayLog = View.inflate(this, R.layout.item_dialog_log, null);
        mInflate = View.inflate(this, R.layout.pop_listview, null);
    }

    @Override
    protected void setListener() {
        mRbtnMainConnrouter.setOnClickListener(this);
        mRbtnMainCloserouter.setOnClickListener(this);
        //mSftpButton.setOnClickListener(this);
        mTvMainChoose.setOnClickListener(this);
        mFbtnMainTrunwx.setOnClickListener(this);
        mFbtnMainShare.setOnClickListener(this);
        mFbtnMainTruntp.setOnClickListener(this);
        mRbtnMainSavelog.setOnClickListener(this);
        //text change listener, for getting the current input changes.
        //handlers
        mHandler = new Handler();
        mTvHandler = new Handler();
        mCommandEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String[] sr = editable.toString().split("\r\n");
                String s = sr[sr.length - 1];
                mLastLine = s;
            }
        });
        //输入命令点击开始测试
        mCommandEdit.setOnEditorActionListener(
                new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        //Log.d(TAG, "editor action " + event);
                        if (isEditTextEmpty(mCommandEdit)) {
                            return false;
                        }
                        // run command
                        else {
                            if (event == null || event.getAction() != KeyEvent.ACTION_DOWN) {
                                return false;
                            }
                            wifiTest();
                            return false;
                        }
                    }
                }
        );
        //获取日志保存类型
        mSpDialoglogSavetype.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SDHelper.fileType = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void wifiTest() {
        // get the last line of terminal
        String command = getLastLine();
        ToastUtil.make("command"+command);
        ExecTaskCallbackHandler t = new ExecTaskCallbackHandler() {
            @Override
            public void onFail() {
                makeToast(R.string.taskfail);
            }

            @Override
            public void onComplete(String completeString) {

            }
        };
        mCommandEdit.addLastInput(command);
        SessionController.getSessionController().executeCommand(mHandler, t, command);
    }

    @NeedsPermission({Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void getFile() {
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        MainActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }
}
