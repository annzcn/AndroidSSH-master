package cn.clickwise.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.clickwise.R;
import cn.clickwise.config.Constants;
import cn.clickwise.utils.other.WebViewUtil;

public class WebActivity extends AppCompatActivity {

    @BindView(R.id.web_web_testspeed)
    WebView mWebWebTestspeed;
    @BindView(R.id.tbar_web_title)
    Toolbar mTbarWebTitle;
    @BindView(R.id.pgb_web_progress)
    ProgressBar mPgbWebProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        ButterKnife.bind(this);
        initView();
        loadWeb();
    }

    private void initView() {
        //setSupportActionBar(mTbarWebTitle);
        mTbarWebTitle.setTitle(getString(R.string.netspeed_test));
        //回退
        mTbarWebTitle.setNavigationIcon(R.drawable.returns);
        mTbarWebTitle.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void loadWeb() {
        if (getStringFromOtherActivity() != null) {
            WebViewUtil.setWebView(mWebWebTestspeed, mPgbWebProgress, getStringFromOtherActivity());
        }
    }

    private String getStringFromOtherActivity() {
        String url = null;
        Intent intent = getIntent();
        if (intent != null) {
            url = intent.getStringExtra(Constants.ACTIVITY_COMMUNICATION_URL);
        }
        return url;
    }
}
