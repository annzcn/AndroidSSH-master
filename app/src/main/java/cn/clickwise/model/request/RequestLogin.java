package cn.clickwise.model.request;

import android.app.ProgressDialog;
import android.os.AsyncTask;

import org.json.JSONException;

import java.io.IOException;
import java.util.Map;

import cn.clickwise.bean.UserLoginReturn;
import cn.clickwise.config.Constants;
import cn.clickwise.interf.IUserLoginCallBack;
import cn.clickwise.utils.other.JsonParse;
import cn.clickwise.utils.other.JsonUtil;
import cn.clickwise.utils.other.OkHttpUtil;
import okhttp3.Response;

/**
 * Created by T420s on 2016/11/15.
 */
public class RequestLogin extends AsyncTask<String, Integer, UserLoginReturn> {
    private Map<String, String> jsonMap;
    private IUserLoginCallBack mUserLoginCallBack;
    private ProgressDialog mProgressDialog;

    public RequestLogin(ProgressDialog progressDialog, Map<String, String> jsonMap, IUserLoginCallBack userLoginCallBack) {
        this.mProgressDialog = progressDialog;
        this.jsonMap = jsonMap;
        mUserLoginCallBack = userLoginCallBack;
    }

    @Override
    protected void onPreExecute() {
        if (mProgressDialog != null) {
            mProgressDialog.setMax(Constants.PROGRESS_MAX);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setMessage("正在登陆中……");
            mProgressDialog.show();
            //progressDialog.setProgressStyle();
        }
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        if (mProgressDialog != null ) {
            mProgressDialog.setProgress(values[0]);
            if (Constants.PROGRESS_MAX == values[0])
            mProgressDialog.dismiss();
        }
    }

    @Override
    protected UserLoginReturn doInBackground(String... params) {
        UserLoginReturn userLoginBean = null;
        if (params[0] != null && params[0].length() > 0) {
            try {
                Response responsePost = OkHttpUtil.getResponsePost(OkHttpUtil.getJsonRequestBody(OkHttpUtil.JSON, JsonUtil.toJson(jsonMap)), OkHttpUtil.REQUEST_POST, params[0]);
                String jsonStr = responsePost.body().string();
                if (jsonStr != null && jsonStr.length() > 0) {
                    userLoginBean = JsonParse.getUserLoginBean(jsonStr);
                    publishProgress(Constants.PROGRESS_MAX);//登陆成功
                } else {
                    publishProgress(Constants.PROGRESS_MAX);
                }
            } catch (IOException e) {
                publishProgress(Constants.PROGRESS_MAX);
                e.printStackTrace();
            } catch (JSONException e) {
                publishProgress(Constants.PROGRESS_MAX);
                e.printStackTrace();
            }
        } else {
            publishProgress(Constants.PROGRESS_MAX);
        }
        return userLoginBean;
    }

    @Override
    protected void onPostExecute(UserLoginReturn userLoginBean) {
        if (userLoginBean != null) {
            mUserLoginCallBack.userLoginResult(userLoginBean);
        }
    }
}
