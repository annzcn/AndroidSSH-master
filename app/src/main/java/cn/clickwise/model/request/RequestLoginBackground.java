package cn.clickwise.model.request;

import android.os.AsyncTask;

import org.json.JSONException;

import java.io.IOException;
import java.util.Map;

import cn.clickwise.bean.UserLoginBackgroundReturn;
import cn.clickwise.interf.IUserLoginBackgroundCallBack;
import cn.clickwise.utils.other.JsonParse;
import cn.clickwise.utils.other.OkHttpUtil;
import okhttp3.Response;

/**
 * Created by lvyang on 2016/11/23.
 */
public class RequestLoginBackground extends AsyncTask<String, Integer, UserLoginBackgroundReturn> {
    private Map<String, String> mStringMap;
    private IUserLoginBackgroundCallBack mUserLoginBackgroundCallBack;

    public RequestLoginBackground(Map<String, String> stringMap, IUserLoginBackgroundCallBack userLoginBackgroundCallBack) {
        mStringMap = stringMap;
        mUserLoginBackgroundCallBack = userLoginBackgroundCallBack;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected UserLoginBackgroundReturn doInBackground(String... params) {
        UserLoginBackgroundReturn userLoginBackgroundBean = null;
        try {
            if (params[0] != null && params[0].length() > 0) {
                Response responsePost = OkHttpUtil.getResponsePost(OkHttpUtil.getFormRequestBody(mStringMap), OkHttpUtil.REQUEST_POST, params[0]);
                String strJson = responsePost.body().string();
                if (strJson != null && strJson.length() > 0) {
                    userLoginBackgroundBean = JsonParse.getUserLoginBackground(strJson);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return userLoginBackgroundBean;
    }

    @Override
    protected void onPostExecute(UserLoginBackgroundReturn userLoginBackgroundBean) {
        if (userLoginBackgroundBean != null) {
            mUserLoginBackgroundCallBack.userLoginBackgroundResult(userLoginBackgroundBean);
        }
    }
}
