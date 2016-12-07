package cn.clickwise.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import java.io.File;

import cn.clickwise.R;
import cn.clickwise.config.Constants;
import cn.clickwise.utils.helper.SDHelper;
import cn.clickwise.utils.other.ApkUpdateUtil;
import cn.clickwise.utils.other.MultiDownloadUtil;
import cn.clickwise.utils.other.ToastUtil;

public class UpdateApkService extends Service {
    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mBuilder;
    private Notification mNotification;
    private RemoteViews mRemoteViews;

    public UpdateApkService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String updateApkUrl = intent.getStringExtra(Constants.UPDATE_APK_URL);
        final String updateApkVersion = intent.getStringExtra(Constants.UPDATE_APK_VERSION);
        if (!"".equals(updateApkUrl) && !"".equals(updateApkVersion)) {
            MultiDownloadUtil.multiDownloadApk(updateApkUrl, new RequestCallBack<File>() {
                @Override
                public void onStart() {
                    super.onStart();
                    mBuilder = new NotificationCompat.Builder(UpdateApkService.this);
                    showNotification(updateApkVersion);
                }

                @Override
                public void onSuccess(ResponseInfo<File> responseInfo) {
                    ToastUtil.make("新版本下载成功");
                    ApkUpdateUtil.installApk(SDHelper.getFile(SDHelper.filePath, Constants.CLISEWISE, SDHelper.fileType_apk), UpdateApkService.this);
                    stopSelf();
                }

                @Override
                public void onLoading(long total, long current, boolean isUploading) {
                    super.onLoading(total, current, isUploading);
                    int progress = (int) ((current * 1.f) * Constants.PROGRESS_MAX / (total * 1.f));
                    //mBuilder.setProgress(Constants.PROGRESS_MAX, progress, false);
                    mRemoteViews.setTextViewText(R.id.tv_notifi_title, "版本更新下载进度");
                    mRemoteViews.setProgressBar(R.id.pb_notifi_progress, Constants.PROGRESS_MAX, progress, false);
                    mBuilder.setContent(mRemoteViews);
                    setNotification(mBuilder.build());
                }

                @Override
                public void onFailure(HttpException e, String s) {
//                    mProgressDialog.dismiss();
                    ToastUtil.make("版本更新失败，重新下载更新版本");
                    startService(new Intent(UpdateApkService.this, UpdateApkService.class));
                }
            });
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void setNotification(Notification mNotification) {
        mNotification.flags = Notification.FLAG_ONGOING_EVENT | Notification.FLAG_FOREGROUND_SERVICE | Notification.FLAG_SHOW_LIGHTS | Notification.FLAG_ONLY_ALERT_ONCE | Notification.FLAG_AUTO_CANCEL | Notification.FLAG_NO_CLEAR;
        mNotificationManager.notify(0, mNotification);
    }

    private void showNotification(String updateApkVersion) {
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Bitmap largeIconBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.apk_progresss);
        mRemoteViews = new RemoteViews(getPackageName(), R.layout.item_notifi);
        mRemoteViews.setTextViewText(R.id.tv_notifi_title, "版本更新下载进度");
        //mRemoteViews.setImageViewResource(R.id.img_notifi_largeicon, R.mipmap.offline_router_72);
        /*setContentTitle("下载进度")*/
        Intent intent = new Intent();
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContent(mRemoteViews)
                .setLargeIcon(largeIconBitmap)
                .setContentIntent(pendingIntent)
//                .setContentInfo("更新版本：" + updateApkVersion)
                .setTicker("小伙伴，要更新版本喽")
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(false)
                .setOngoing(false)
                /*.setProgress(Constants.PROGRESS_MAX, 0, false)*/
                .setOnlyAlertOnce(true)
                .setSmallIcon(R.mipmap.remind_32)//注意：不设置Icon Notification不显示
                .setPriority(Notification.PRIORITY_HIGH)//设置优先级
                .setDefaults(Notification.DEFAULT_SOUND);
        mNotification = mBuilder.build();
        setNotification(mNotification);
    }

}
