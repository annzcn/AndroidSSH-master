package cn.clickwise.utils.other;

import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

/**
 * Created by T420s on 2016/11/14.
 */
public class WebViewUtil {
    /**
     * 设置WebView的属性
     *
     * @param mWebView
     * @param url
     */
    public static void setWebView(final WebView mWebView, final ProgressBar progressBar, String url) {
        //动态设置WebView的宽度
//        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mWebView.getLayoutParams();
//        layoutParams.width=100;
//        mWebView.setLayoutParams(layoutParams);
        //加载页面
        mWebView.loadUrl(url);
        //http://www.speedtest.cn/
        //  //"file:///android_asset/testspeed.html"
        // http://wangsuceshi.51240.com/
        // http://www.wangsuceshi.net/
        //http://www.d777.com/wangsuceshi/
        //是否支持JavaScript
        mWebView.getSettings().setJavaScriptEnabled(true);
        //设置默认字体大下
        //mWebView.getSettings().setDefaultFontSize(20);
        //使加载的网页适应手机屏幕
        mWebView.getSettings().setLoadWithOverviewMode(true);
        mWebView.getSettings().setUseWideViewPort(true);
        // 设置可以支持变焦
        mWebView.getSettings().setSupportZoom(true);
        //扩大比例的缩放
        mWebView.getSettings().setUseWideViewPort(true);
        //是否支持缩放
        mWebView.getSettings().getBuiltInZoomControls();
        //自适应屏幕
        mWebView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        // 对WebView监听实现Web页面回退不至于Activity finsih()了
        mWebView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (mWebView.canGoBack() && keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
                    mWebView.goBack();
                    //mWebView.reload();
                    //mWebView.goForward();
                    return true;
                }
                return false;
            }
        });
        //使网页直接显示到应用中
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                //return为true时view内打开，为false调用第三方浏览器打开
                return true;
            }
        });
        //显示加载进度
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                progressBar.setProgress(newProgress);
                if (progressBar.getProgress() == progressBar.getMax()) {
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
        //优先使用缓存
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);

    }
}
