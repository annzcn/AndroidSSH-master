package cn.clickwise.utils.other;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by T420s on 2016/10/30.
 */
public class FormatUtil {
    /**
     * 判断电话号码是否符合格式.
     *
     * @param inputText the input text
     * @return true, if is phone
     */
    public static boolean isPhone(String inputText) {
        Pattern p = Pattern.compile("^((14[0-9])|(13[0-9])|(15[0-9])|(18[0-9])|(17[0-9]))\\d{8}$");
        Matcher m = p.matcher(inputText);
        return m.matches();
    }
}
