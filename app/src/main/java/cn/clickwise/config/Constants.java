package cn.clickwise.config;

/**
 * Created by T420s on 2016/10/24.
 */
public class Constants {
    public static final String APP_ID = "wx367837f654a070dc";
    public static final int DIAHNOSE_FAIL = 100000000;
    public static String intentKey = "intentKey";
    public static final String UPDATE_APK_URL = "updateApkUrl";
    public static final String UPDATE_APK_VERSION = "updateApkVersion";
    public static final String CLISEWISE = "点智互动";
    //广播ActionStr
    public static final String PROGRESSRESEIVER = "progress";
    public static final int PROGRESS_MAX = 100;
    public static final int DIAGNOSE_PROGRESS_MAX = 100;
    public static final String ACTIVITY_COMMUNICATION_URL = "url";
    public static final String ACTIVITY_ROUTERTOLOGIN_URL = "loginUrl";

    //最大Wifi接入速度
    public static final int WIFILINK_MAXSPEED = 72;

    public static final int MENU_AGENT = 0;
    //public static final int MENU_REGION = 1;
    public static final int MENU_ROUTER_STATE = 1;
    public static final int MENU_RANGE = 2;
    public static final String REQUEST_ROUTERLOCAL_RESULT_SUCCESS = "success";

    public static final String MAP_LONGITUDE = "longitude";
    public static final String MAP_LATITUDE = "latitude";
    public static final String MAP_DESCRIBE = "describe";

    public static class ShowMsgActivity {
        public static final String STitle = "showmsg_title";
        public static final String SMessage = "showmsg_message";
        public static final String BAThumbData = "showmsg_thumb_data";
    }

    //路由器分布详情
    public static final String ROUTERLOCAL_AGENT = "代理商";
    public static final String ROUTERLOCAL_REGION = "地区";
    public static final String ROUTERLOCAL_MERCHANT = "商户名";
    public static final String ROUTERLOCAL_MAC = "路由Mac";
    public static final String ROUTERLOCAL_STATE = "状态";
    public static final String ROUTERLOCAL_DISTANCE = "距离";
    public static final String ROUTERLOCAL_GRADE = "积分";
    public static final String ROUTERLOCAL_LINKER = "商户联系人";
    public static final String ROUTERLOCAL_PHONE = "商户电话";
    public static final String ROUTERLOCAL_DURATION_TIME = "持续时间";
    public static final String ROUTERLOCAL_TASK = "任务领取";
    public static final String ROUTERLOCAL_STATE_ONLINE = "在线";
    public static final String ROUTERLOCAL_STATE_OFFLINE = "离线";

    public static final String ROUTERLOCAL_RECEIVE_TASK = "点击领取";
    public static final String ROUTERLOCAL_NO_RECEIVE_TASK = "不可领取";
    //百度开发Key
    public static final String BAIDU_KEY = "D5ZqSPYY06jlaDlozYGB4TmK3mM9gPMH";
    //启动时间
    public static final int START_LAUCH_TIME = 2500;

    public static final String LOG_LINE = "---------------------------------------";

    public static final String SPAN = "\r\n";

    public static final String TEN_NO_LOGIN_TIME = "TenNoLogin";

    public static final String CHS_TIME = "chsTime";

    public static final String NO_CHS_TIME = "noChsTime";

    public static final long TEN_TIME = 1000 * 60 * 60 * 24 * 10;

    public static final String SAVE_LOGIN_INFO = "loginInfo";

    public static final String USER_NAME = "username";

    public static final String USER_PWD = "pwd";

    public static final String USER_ID = "user_id";

    public static final String USER_IS_SIGN = "user_is_sign";

    public static final String USER_REAL_NAME = "real_name";

    public static final String USER_PHONE = "phone";

    public static final String USER_TYPR = "user_type";

    public static final String RECEIVER_CLICK = "clickReceiver";

    public static final String CRYPTO_PASSWORD = "123";

    public static final int FAIL_INT = -1;

    public static final int LIST_ADAPTER_TASK_ID = 666;

    public static final String EMPTY = "";

    public static final int USER_SIGNED = 1;

    public static final int USER_SIGN_NO = 0;

    //地推人员3分钟定位上传一次
    public static final int PUSH_USER_LOCATION_SPAN_TIME = 1000 * 60 * 3;

    //3多分钟请求一次地推人员定位数据
    public static final int REQUEST_PUSH_USER_LOCATION_SPAN_TIME = 1000 * 60 * 3 + 500;

    public static final int BAIDU_ZOOM_LEVEL_NORMAL = 14;

    public static final int BAIDU_ZOOM_LEVEL_SHOW = 19;

    public static final String SERVICE_TO_ROUTE_LOCATION = "serviceToRouteLocation";
}
