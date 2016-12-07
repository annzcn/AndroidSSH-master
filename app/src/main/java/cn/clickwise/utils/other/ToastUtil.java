package cn.clickwise.utils.other;

import android.os.Looper;
import android.widget.Toast;

import cn.clickwise.AppContext;

/**
 * Created by T420s on 2016/10/26.
 */
public class ToastUtil {
    public static void make(String outPutStr) {
        Toast.makeText(AppContext.getContext(), outPutStr, Toast.LENGTH_SHORT).show();
    }
    public static void make(String outPutStr,int tag) {
        Looper.prepare();
        Toast.makeText(AppContext.getContext(), outPutStr, Toast.LENGTH_SHORT).show();
        Looper.loop();
    }
}
