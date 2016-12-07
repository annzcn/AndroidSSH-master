package cn.clickwise.utils.other;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.callback.RequestCallBack;

import java.io.File;

import cn.clickwise.utils.helper.SDHelper;

/**
 * Created by lvyang on 2016/12/5.
 */
public class MultiDownloadUtil {
    public static void multiDownloadApk(String url, RequestCallBack<File> requestCallBack) {
        HttpUtils httpUtils = new HttpUtils();
        httpUtils.download(url, SDHelper.filePath + ApkUpdateUtil.updateName, requestCallBack);
    }
}
