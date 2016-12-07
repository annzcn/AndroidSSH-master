package cn.clickwise.bean;

import java.util.List;

/**
 * Created by lvyang on 2016/11/30.
 */
public class PushUserLocationReturn {
    /**
     * jsonrpc : 2.0
     * state : success
     * result : []
     */

    private String jsonrpc;
    private String state;
    private List<?> result;

    public String getJsonrpc() {
        return jsonrpc;
    }

    public void setJsonrpc(String jsonrpc) {
        this.jsonrpc = jsonrpc;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public List<?> getResult() {
        return result;
    }

    public void setResult(List<?> result) {
        this.result = result;
    }
}
