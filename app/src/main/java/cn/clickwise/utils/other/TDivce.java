package cn.clickwise.utils.other;

import android.content.Context;
import android.view.WindowManager;

/**
 * Created by T420s on 2016/11/12.
 */
public class TDivce {
    public static WindowManager getWindowManager(Context context) {
        return (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    }

    /**
     * 得到设备的高度
     * @param context
     * @return
     */
    public static int getTDivceHeight(Context context) {
        return getWindowManager(context).getDefaultDisplay().getHeight();
    }

    /**
     * 得到设备的宽度
     * @param context
     * @return
     */
    public static int getTDviceWidth(Context context) {
        return getWindowManager(context).getDefaultDisplay().getWidth();
    }
}
