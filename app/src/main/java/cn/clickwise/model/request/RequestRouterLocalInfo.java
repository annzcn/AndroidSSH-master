package cn.clickwise.model.request;

import android.os.AsyncTask;

import org.json.JSONException;

import java.io.IOException;
import java.util.Map;

import cn.clickwise.bean.RouterLocalInfoReturn;
import cn.clickwise.interf.IRouterLocalInfoCallBack;
import cn.clickwise.utils.other.JsonParse;
import cn.clickwise.utils.other.JsonUtil;
import cn.clickwise.utils.other.OkHttpUtil;
import okhttp3.Response;

/**
 * Created by T420s on 2016/11/9.
 */
public class RequestRouterLocalInfo extends AsyncTask<String, Void, RouterLocalInfoReturn> {
    private Map<String, Object> requestMap;
    private IRouterLocalInfoCallBack mRequestRouterLocalCallBack;

    public RequestRouterLocalInfo(Map<String, Object> requestMap, IRouterLocalInfoCallBack mRequestRouterLocalCallBack) {
        this.requestMap = requestMap;
        this.mRequestRouterLocalCallBack = mRequestRouterLocalCallBack;
    }

    @Override
    protected RouterLocalInfoReturn doInBackground(String... params) {
        RouterLocalInfoReturn routerLocalInfoBean = null;
        if (params[0] != null && !params[0].equals("")) {
            try {
                Response responsePost = OkHttpUtil.getResponsePost(OkHttpUtil.getJsonRequestBody(OkHttpUtil.JSON, JsonUtil.toJson(requestMap)), OkHttpUtil.REQUEST_POST, params[0]);
                //Log.d("jjj", "doInBackground: json-->"+JsonUtil.toJson(requestMap));
                String jsonResult = responsePost.body().string();
                if (responsePost.isSuccessful()) {
                    try {
                        routerLocalInfoBean = JsonParse.getRouterLocalInfoBean(jsonResult);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return routerLocalInfoBean;
    }

    @Override
    protected void onPostExecute(RouterLocalInfoReturn routerLocalInfoBean) {
        if (routerLocalInfoBean != null) {
            mRequestRouterLocalCallBack.routerLocalResult(routerLocalInfoBean);
        }
    }
}
