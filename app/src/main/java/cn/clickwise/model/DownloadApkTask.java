package cn.clickwise.model;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import cn.clickwise.config.Constants;
import cn.clickwise.utils.helper.SDHelper;
import cn.clickwise.utils.other.ApkUpdateUtil;

/**
 * Created by T420s on 2016/11/2.
 */
public class DownloadApkTask extends AsyncTask<String, Double, File> {
    private Context mContext;
    private ProgressDialog mProgressDialog;

    public DownloadApkTask(Context context) {
        this.mContext = context;
    }

    @Override
    protected void onPreExecute() {
        mProgressDialog=new ProgressDialog(mContext);
        mProgressDialog.setMax(Constants.PROGRESS_MAX);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setTitle("下载更新");
        mProgressDialog.show();
    }

    @Override
    protected void onProgressUpdate(Double... values) {
        double progress= values[0];
        mProgressDialog.setProgress((int) progress);
        if (mProgressDialog.getMax() == values[0]) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    protected File doInBackground(String... params) {
        File file = null;
        HttpURLConnection conn = null;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            BufferedInputStream bis = null;
            FileOutputStream fos = null;
            try {
                URL url = new URL(params[0]);
                conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(5000);
                conn.connect();
                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    double total = conn.getContentLength();
                    InputStream inputStream = conn.getInputStream();
                    file = new File(SDHelper.filePath, ApkUpdateUtil.updateName);
                    if (!file.exists()) {
                        if (!file.getParentFile().exists()) {
                            file.getParentFile().mkdirs();
                        }
                        file.createNewFile();
                    }
                    fos = new FileOutputStream(file);
                    bis = new BufferedInputStream(inputStream);
                    byte[] buffer = new byte[1024*5];
                    int len;
                    double downloadLen = 0;
                    while ((len = bis.read(buffer)) != -1) {
                        fos.write(buffer, 0, len);
                        downloadLen += len;
                        Double progress = (downloadLen* Constants.PROGRESS_MAX)/ total ;
                        publishProgress(progress);
                    }
                }
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
                    if (conn != null) {
                        conn.disconnect();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return file;
    }

    @Override
    protected void onPostExecute(File result) {
        if (result != null && result.exists()) {
            //下载完毕直接安装
            ApkUpdateUtil.installApk(result, mContext);
        }
    }
}
