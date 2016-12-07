package cn.clickwise.utils.other;

import android.util.Xml;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import cn.clickwise.bean.RouterLocalInfoReturn;
import cn.clickwise.bean.UpdateInfoBean;
import cn.clickwise.bean.UserLoginBackgroundReturn;
import cn.clickwise.bean.UserLoginReturn;
import cn.clickwise.config.Constants;

/**
 * Created by T420s on 2016/11/3.
 */
public class JsonParse {
    public static UpdateInfoBean getUpdateInfo(String jsonStr) {
        UpdateInfoBean updateInfoBean = null;
        try {
            updateInfoBean = new UpdateInfoBean();
            JSONObject jsonObject = new JSONObject(jsonStr);
            updateInfoBean.setVersion(jsonObject.optString("version"));
            updateInfoBean.setUrl(jsonObject.optString("url"));
            updateInfoBean.setDescription(jsonObject.optString("description"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return updateInfoBean;
    }

    /**
     * 解析服务器返回来的xml文件
     *
     * @param is
     * @return
     */
    public static UpdateInfoBean getUpdateInfo(InputStream is) {
        XmlPullParser parser = Xml.newPullParser();
        UpdateInfoBean updateInfoBean = new UpdateInfoBean();
        try {
            parser.setInput(is, "utf-8");
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if ("version".equals(parser.getName())) {
                            updateInfoBean.setVersion(parser.nextText());
                        } else if ("url".equals(parser.getName())) {
                            updateInfoBean.setUrl(parser.nextText());
                        } else if ("description".equals(parser.getName())) {
                            updateInfoBean.setDescription(parser.nextText());
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                }
                eventType = parser.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return updateInfoBean;
    }

    public static String formatJsonStr(String jsonStr) {
        return jsonStr.substring(jsonStr.indexOf("{"), jsonStr.lastIndexOf("}") + 1);
    }

    public static UpdateInfoBean getUpdateInfo(String json, int tag) {
        return new Gson().fromJson(json.toString(), UpdateInfoBean.class);
    }

    public static RouterLocalInfoReturn getRouterLocalInfoBean(String jsonStr) throws JSONException {
        RouterLocalInfoReturn routerLocalInfoBean = new RouterLocalInfoReturn();
        JSONObject jsonObject = new JSONObject(formatJsonStr(jsonStr));
        routerLocalInfoBean.setJsonrpc(jsonObject.optString("jsonrpc"));
        routerLocalInfoBean.setState(jsonObject.optString("state"));
        if (jsonObject.optString("state").equals(Constants.REQUEST_ROUTERLOCAL_RESULT_SUCCESS)) {
            List<RouterLocalInfoReturn.Result> results = new ArrayList<>();
            JSONArray result = jsonObject.optJSONArray("result");
            int length = result.length();
            for (int i = 0; i < length; i++) {
                RouterLocalInfoReturn.Result resultBean = new RouterLocalInfoReturn.Result();
                JSONObject object = (JSONObject) result.get(i);
                //JSONObject object = (JSONObject) new JSONTokener((String) result.get(i)).nextValue();
                resultBean.setAcmac(object.optString("acmac"));
                resultBean.setLatitude(object.optString("latitude"));
                resultBean.setLongtitude(object.optString("longtitude"));
                resultBean.setShopname(object.optString("shopname"));
                resultBean.setName(object.optString("name"));
                resultBean.setOnline_stats(object.optString("online_stats"));
                resultBean.setAid(object.optString("aid"));
                resultBean.setPhone(object.optString("phone"));
                resultBean.setLinker(object.optString("linker"));
                resultBean.setRoutename(object.optString("routename"));
                resultBean.setHw_version(object.optString("hw_version"));
                resultBean.setSw_version(object.optString("sw_version"));
                resultBean.setLast_heartbeat_time(object.optString("last_heartbeat_time"));
                resultBean.setOnline_time(object.optString("online_time"));
                results.add(resultBean);
            }
            routerLocalInfoBean.setResult(results);
        }
        return routerLocalInfoBean;
    }

    public static UserLoginReturn getUserLoginBean(String jsonStr) throws JSONException {
        JSONObject jsonObject = new JSONObject(formatJsonStr(jsonStr));
        UserLoginReturn user = new UserLoginReturn();
        user.setJsonrpc(jsonObject.optString("jsonrpc"));
        user.setState(jsonObject.optString("state"));
        if (Constants.REQUEST_ROUTERLOCAL_RESULT_SUCCESS.equals(user.getState())) {
            JSONObject object = jsonObject.optJSONObject("result");
            UserLoginReturn.Result result = new UserLoginReturn.Result();
            result.setId(object.optString("id"));
            result.setLinker(object.optString("linker"));
            result.setName(object.optString("name"));
            result.setPhone(object.optString("phone"));
            result.setType(object.optInt("type"));
            user.setResult(result);
        }
        return user;
    }

    public static UserLoginBackgroundReturn getUserLoginBackground(String jsonStr) throws JSONException {
        UserLoginBackgroundReturn userLoginBackground = new UserLoginBackgroundReturn();
        JSONObject jsonObject = new JSONObject(formatJsonStr(jsonStr));
        userLoginBackground.setError(jsonObject.optInt("error"));
        userLoginBackground.setMsg(jsonObject.optString("msg"));
        if (userLoginBackground.getError() == 0) {//请求成功
            userLoginBackground.setUrl(jsonObject.optString("url"));
        }
        return userLoginBackground;
    }
}
