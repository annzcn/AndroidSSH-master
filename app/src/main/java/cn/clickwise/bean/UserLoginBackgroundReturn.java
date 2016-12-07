package cn.clickwise.bean;

/**
 * Created by lvyang on 2016/11/23.
 */
public class UserLoginBackgroundReturn {
    private int error;
    private String msg;
    private String url;

    public int getError() {
        return error;
    }

    public void setError(int error) {
        this.error = error;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
