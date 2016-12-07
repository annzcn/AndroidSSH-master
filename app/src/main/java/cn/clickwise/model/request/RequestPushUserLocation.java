package cn.clickwise.model.request;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;

import cn.clickwise.bean.PushUserLocation;
import cn.clickwise.bean.PushUserLocationReturn;
import cn.clickwise.interf.IPushUserLocationCallBack;
import cn.clickwise.utils.other.JsonUtil;
import cn.clickwise.utils.other.OkHttpUtil;
import okhttp3.Response;

/**
 * Created by lvyang on 2016/11/29.
 */
public class RequestPushUserLocation extends AsyncTask<String, Void, PushUserLocationReturn> {
    private IPushUserLocationCallBack mIUserLocationCallBack;
    private PushUserLocation mPushUserLocation;

    public RequestPushUserLocation(PushUserLocation mPushUserLocation, IPushUserLocationCallBack IUserLocationCallBack) {
        this.mIUserLocationCallBack = IUserLocationCallBack;
        this.mPushUserLocation = mPushUserLocation;
    }

    @Override
    protected PushUserLocationReturn doInBackground(String... params) {
        PushUserLocationReturn pushUserLocationReturn = null;
        if (null != params[0] && params.length > 0) {
            try {
                Response responsePost = OkHttpUtil.getResponsePost(OkHttpUtil.getJsonRequestBody(OkHttpUtil.JSON, JsonUtil.toJson(mPushUserLocation)), OkHttpUtil.REQUEST_POST, params[0]);
                if (responsePost != null) {
                    pushUserLocationReturn = JsonUtil.toBean(PushUserLocationReturn.class, responsePost.body().string());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return pushUserLocationReturn;
    }

    @Override
    protected void onPostExecute(PushUserLocationReturn pushUserLocationReturn) {
        if (pushUserLocationReturn != null) {
            mIUserLocationCallBack.userLocationResult(pushUserLocationReturn);
        }
    }
}
