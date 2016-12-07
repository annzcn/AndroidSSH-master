package cn.clickwise.utils.other;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import cn.clickwise.AppContext;
import cn.clickwise.R;
import cn.clickwise.bean.UpdateInfoBean;
import cn.clickwise.config.Constants;
import cn.clickwise.config.RequestUrl;
import cn.clickwise.interf.ICheckNet;
import cn.clickwise.interf.ICheckVersionCallBack;
import cn.clickwise.model.CheckVersionTask;
import cn.clickwise.model.DownloadApkTask;
import cn.clickwise.service.UpdateApkService;
import cn.clickwise.utils.helper.RouteTestHelper;
import cn.clickwise.utils.helper.SDHelper;

/**
 * Created by T420s on 2016/10/31.
 */
public class ApkUpdateUtil {

    public static String updateName = "点智互动.apk";
    private static ProgressDialog mProgressDialog;

    /**
     * 获取当前程序的版本号
     *
     * @return
     */
    public static String getVersionName() {
        String versionName = null;
        PackageManager packageManager = AppContext.getContext().getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(AppContext.getContext().getPackageName(), 0);
            versionName = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }

    public static File getFileFromServer(String path, ProgressDialog pd) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            BufferedInputStream bis = null;
            FileOutputStream fos = null;
            try {
                URL url = new URL(path);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(5000);
                pd.setMax(conn.getContentLength());
                InputStream inputStream = conn.getInputStream();
                File file = new File(SDHelper.ApkPath, updateName);
                fos = new FileOutputStream(file);
                bis = new BufferedInputStream(inputStream);
                byte[] buffer = new byte[1024];
                int len;
                int total = 0;
                while ((len = bis.read(buffer)) != -1) {
                    fos.write(buffer, 0, len);
                    total += len;
                    pd.setProgress(total);
                }
                return file;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (fos != null) {
                        fos.close();
                    }
                    if (bis != null) {
                        bis.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * 检查版本是否需要更新
     */
    public static void checkVersion(final Context context, final Handler handler) {
        NetWorkUtil.isNetWork(new ICheckNet() {
            @Override
            public void available() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        new CheckVersionTask(new ICheckVersionCallBack() {
                            @Override
                            public void OnCheckVersionResult(UpdateInfoBean result) {
                                //版本号不相同就需要更新
                                if (result != null && !((ApkUpdateUtil.getVersionName()).equals(result.getVersion()))) {
                                    updatePoint(result, context);
                                }
                            }
                        }).execute(RequestUrl.apkUpdateUrl);
                    }
                });
            }

            @Override
            public void notAvailable() {

            }
        });
    }

    /**
     * 下载apk
     *
     * @param updateInfoBean
     */
    private static void downloadApk(UpdateInfoBean updateInfoBean, final Context context) {
        //new DownloadApkTask(context).execute(updateInfoBean.getUrl());
        /*Intent intent = new Intent(context, UpdateApkService.class);
        intent.putExtra(Constants.UPDATE_APK_URL, updateInfoBean.getUrl());
        intent.putExtra(Constants.UPDATE_APK_VERSION, updateInfoBean.getVersion());
        context.startService(intent);*/
        MultiDownloadUtil.multiDownloadApk(updateInfoBean.getUrl(), new RequestCallBack<File>() {
            @Override
            public void onStart() {
                super.onStart();
                mProgressDialog = new ProgressDialog(context);
                mProgressDialog.setMax(Constants.PROGRESS_MAX);
                mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                mProgressDialog.setCancelable(false);
                mProgressDialog.setTitle("下载更新");
                mProgressDialog.show();
            }

            @Override
            public void onSuccess(ResponseInfo<File> responseInfo) {
                ToastUtil.make("新版本下载成功");
                ApkUpdateUtil.installApk(SDHelper.getFile(SDHelper.filePath, Constants.CLISEWISE, SDHelper.fileType_apk), context);
                mProgressDialog.dismiss();
            }

            @Override
            public void onLoading(long total, long current, boolean isUploading) {
                super.onLoading(total, current, isUploading);
                int progress = (int) ((current * 1.f) * Constants.PROGRESS_MAX / (total * 1.f));
                mProgressDialog.setProgress(progress);
            }

            @Override
            public void onFailure(HttpException e, String s) {
                mProgressDialog.dismiss();
                ToastUtil.make("版本更新失败，请重新下载");
            }
        });
    }

    /**
     * 更新提示
     *
     * @param updateInfoBean
     */
    private static void updatePoint(final UpdateInfoBean updateInfoBean, final Context context) {
        if (NetWorkUtil.isWifiNetWork(context)) {//WiFi环境下直接更新
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("更新提示").setMessage("嗨，小伙伴，要更新版本喽\r\n" + "更新版本：" + updateInfoBean.getVersion() + "\r\n更新内容：" + updateInfoBean.getDescription()).setPositiveButton("更新", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    downloadApk(updateInfoBean, context);
                }
            }).setCancelable(false).show();
        } else if (NetWorkUtil.isMobileNetWork(context)) {//数据流量环境下需提醒
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("更新提示:数据流量状态").setMessage("更新版本：" + updateInfoBean.getVersion() + "\r\n" + "更新内容：" + updateInfoBean.getDescription()).setPositiveButton("更新", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    downloadApk(updateInfoBean, context);
                }
            }).setNegativeButton("下次再说", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).show();
        }
    }

    /**
     * 安装apk
     *
     * @param file
     * @param context
     */
    public static void installApk(File file, Context context) {
        Intent intent = new Intent();
        //执行动作
        intent.setAction(Intent.ACTION_VIEW).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ;
        //执行的数据类型
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");//编者按：此处Android应为android，否则造成安装不了
        context.startActivity(intent);
    }
}
