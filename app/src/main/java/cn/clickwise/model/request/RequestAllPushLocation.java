package cn.clickwise.model.request;

import android.os.AsyncTask;

import java.io.IOException;
import java.util.Map;

import cn.clickwise.bean.AllPushLocationReturn;
import cn.clickwise.interf.IAllPushLocationCallBack;
import cn.clickwise.utils.other.JsonUtil;
import cn.clickwise.utils.other.OkHttpUtil;
import okhttp3.Response;

/**
 * Created by lvyang on 2016/11/29.
 */
public class RequestAllPushLocation extends AsyncTask<String, Integer, AllPushLocationReturn> {
    private IAllPushLocationCallBack mIAllPushLocationCallBack;
    private Map<String, Object> jsonMap;

    public RequestAllPushLocation(Map<String, Object> jsonMap, IAllPushLocationCallBack IAllPushLocationCallBack) {
        mIAllPushLocationCallBack = IAllPushLocationCallBack;
        this.jsonMap = jsonMap;
    }

    @Override
    protected AllPushLocationReturn doInBackground(String... params) {
        AllPushLocationReturn allPushLocationReturn = null;
        if (params != null && params.length > 0) {
            try {
                Response responsePost = OkHttpUtil.getResponsePost(OkHttpUtil.getJsonRequestBody(OkHttpUtil.JSON, JsonUtil.toJson(jsonMap)), OkHttpUtil.REQUEST_POST, params[0]);
                if (responsePost != null) {
                    allPushLocationReturn = JsonUtil.toBean(AllPushLocationReturn.class, responsePost.body().string());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return allPushLocationReturn;
    }

    @Override
    protected void onPostExecute(AllPushLocationReturn allPushLocationReturn) {
        if (allPushLocationReturn != null) {
            mIAllPushLocationCallBack.allPushLocationResult(allPushLocationReturn);
        }
    }
}
