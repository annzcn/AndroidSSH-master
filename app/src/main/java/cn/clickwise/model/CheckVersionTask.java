package cn.clickwise.model;

import android.os.AsyncTask;

import cn.clickwise.bean.UpdateInfoBean;
import cn.clickwise.interf.ICheckVersionCallBack;
import cn.clickwise.utils.other.OkHttpUtil;
import cn.clickwise.utils.other.JsonParse;

/**
 * Created by T420s on 2016/11/2.
 */
public class CheckVersionTask extends AsyncTask<String, Void, UpdateInfoBean> {
    private ICheckVersionCallBack mCheckVersionCallBack;

    public CheckVersionTask(ICheckVersionCallBack checkVersionCallBack) {
        mCheckVersionCallBack = checkVersionCallBack;
    }

    @Override
    protected UpdateInfoBean doInBackground(String... params) {
        UpdateInfoBean updateInfoBean = null;
        String json = OkHttpUtil.getStringByUrl(params[0]);
        if (json != null && !json.equals("")) {
            updateInfoBean = JsonParse.getUpdateInfo(json);
        }
        return updateInfoBean;
    }

    @Override
    protected void onPostExecute(UpdateInfoBean result) {
        mCheckVersionCallBack.OnCheckVersionResult(result);
    }
}
