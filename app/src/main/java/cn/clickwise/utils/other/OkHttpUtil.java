package cn.clickwise.utils.other;

import android.util.Log;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;

/**
 * Created by T420s on 2016/11/2.
 */
public class OkHttpUtil {
    public static final String JSON_TYPE = "json";
    public static final String FORM_TYPE = "form";
    private static OkHttpClient mOkHttpClient;
    public static final String REQUEST_POST = "POST";
    public static final String REQUEST_GET = "GET";

    static {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(10, TimeUnit.SECONDS);
        mOkHttpClient = builder.build();
    }

    public static Response getResponse(Request request) throws IOException {
        return mOkHttpClient.newCall(request).execute();
    }

    public static Request getRequest(String url) {
        Request.Builder builder = new Request.Builder();
        return builder.url(url).build();
    }

    public static String getStringByUrl(String url) {
        String result = null;
        try {
            result = getResponse(getRequest(url)).body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static final MediaType JSON = MediaType.parse("application/json;charset=utf-8");

    public static RequestBody getJsonRequestBody(MediaType mediaType, String jsonStr) {
        return RequestBody.create(mediaType, jsonStr);
    }

    public static RequestBody getFormRequestBody(Map<String, String> map) {
        FormBody.Builder builder = new FormBody.Builder();
        //MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        if (map != null && map.size() > 0) {
            for (String key : map.keySet()) {
                builder.add(key, map.get(key));
            }
        }
        return builder.build();
    }

    /**
     * 同步post请求
     *
     * @param url
     * @return
     * @throws IOException
     */
    public static Response getResponsePost(RequestBody requestBody, String requestType, String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .method(requestType, requestBody)
                .build();
        return mOkHttpClient.newCall(request).execute();
    }
}
