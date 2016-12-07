package cn.clickwise.interf;

import cn.clickwise.bean.PushUserLocationReturn;

/**
 * Created by lvyang on 2016/11/29.
 */
public interface IPushUserLocationCallBack {
    void userLocationResult(PushUserLocationReturn pushUserLocationReturn);
}
