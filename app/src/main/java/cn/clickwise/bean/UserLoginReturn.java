package cn.clickwise.bean;

/**
 * Created by T420s on 2016/11/14.
 */
public class UserLoginReturn {
    public static final int AGENT_COMMON=0;
    public static final int AGENT_MOBILE=1;
    public static final int AGENT_PUSH=2;
    private String jsonrpc;
    private String state;
    private Result result;

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

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public static class Result {
        private String id;
        private String linker;
        private String name;
        private String phone;
        private int type;//0普通代理 1移动Wifi 2地推代理

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getLinker() {
            return linker;
        }

        public void setLinker(String linker) {
            this.linker = linker;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }
    }
}
