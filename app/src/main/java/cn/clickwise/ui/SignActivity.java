package cn.clickwise.ui;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RadioButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.clickwise.R;
import cn.clickwise.base.BaseActivity;
import cn.clickwise.ui.fragment.SignFragment;
import cn.clickwise.ui.fragment.TraceFragment;
import cn.clickwise.utils.other.TDivce;

public class SignActivity extends BaseActivity {

    @BindView(R.id.tbar_sign_act_title)
    Toolbar mTbarSignActTitle;
    @BindView(R.id.frame_sign_act_group)
    FrameLayout mFrameSignActGroup;
    @BindView(R.id.rbtn_sign_act_sign)
    RadioButton mRbtnSignActSign;
    @BindView(R.id.rbtn_sign_act_trace)
    RadioButton mRbtnSignActTrace;
    private SignFragment mSignFragment;
    private TraceFragment mTraceFragment;
    private Drawable mChooseSignDrawable;
    private Drawable mChooseTraceDrawable;
    private Drawable mSignDrawable;
    private Drawable mTraceDrawable;
    private int mNoChooseColor;
    private int mChooseColor;
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign);
        ButterKnife.bind(this);
        init();
        initView();
        setListener();
    }

    private void init() {
        mNoChooseColor = getResources().getColor(R.color.gray_sign_menu);
        mChooseColor = getResources().getColor(R.color.gray_sign_menu_choose);
        mChooseSignDrawable = getResources().getDrawable(R.drawable.sign_sign_32_choose);
        mChooseTraceDrawable = getResources().getDrawable(R.drawable.sign_trace_32_choose);
        mSignDrawable = getResources().getDrawable(R.drawable.sign_sign_32);
        mTraceDrawable = getResources().getDrawable(R.drawable.sign_trace_32);
    }

    @Override
    protected void initView() {
        mTbarSignActTitle.setTitle(R.string.sign_today);
        mTbarSignActTitle.setNavigationIcon(R.drawable.returns);
        mTbarSignActTitle.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //动态设置导航条的高度
        ViewGroup.LayoutParams layoutParams = mFrameSignActGroup.getLayoutParams();
        layoutParams.height = (int) (TDivce.getTDivceHeight(this) / 1.f / 10.f);
        mFrameSignActGroup.setLayoutParams(layoutParams);
        signMenuView(true);
        //默认加载SignFragment
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (mSignFragment == null) {
            mSignFragment = new SignFragment();
        }
        fragmentTransaction.replace(R.id.frame_sign_act_group, mSignFragment);
        fragmentTransaction.commit();
    }

    @Override
    protected void setListener() {
        mRbtnSignActSign.setOnClickListener(this);
        mRbtnSignActTrace.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        switch (v.getId()) {
            case R.id.rbtn_sign_act_sign://今日签到
                signMenuView(true);
                if (mSignFragment == null) {
                    mSignFragment = new SignFragment();
                }
                fragmentTransaction.replace(R.id.frame_sign_act_group, mSignFragment);
                break;
            case R.id.rbtn_sign_act_trace://历史轨迹
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("提示").setMessage("抱歉，目前此功能未上线")
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                final AlertDialog dialog = builder.show();
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                    }
                }, 1000);
                /*signMenuView(false);
                if (mTraceFragment == null) {
                    mTraceFragment = new TraceFragment();
                }
                fragmentTransaction.replace(R.id.frame_sign_act_group, mTraceFragment);*/
                break;
        }
        fragmentTransaction.commit();
    }

    private void signMenuView(boolean isLoadSignFragment) {
        if (isLoadSignFragment) {
            mRbtnSignActSign.setTextColor(mChooseColor);
            mRbtnSignActTrace.setTextColor(mNoChooseColor);
            mTbarSignActTitle.setTitle(getString(R.string.sign_today));
            mRbtnSignActSign.setCompoundDrawablesWithIntrinsicBounds(null, mChooseSignDrawable, null, null);
            mRbtnSignActTrace.setCompoundDrawablesWithIntrinsicBounds(null, mTraceDrawable, null, null);
        } else {
            mRbtnSignActSign.setTextColor(mNoChooseColor);
            mRbtnSignActTrace.setTextColor(mChooseColor);
            mTbarSignActTitle.setTitle(getString(R.string.his_trace));
            mRbtnSignActSign.setCompoundDrawablesWithIntrinsicBounds(null, mSignDrawable, null, null);
            mRbtnSignActTrace.setCompoundDrawablesWithIntrinsicBounds(null, mChooseTraceDrawable, null, null);
        }
    }
}
