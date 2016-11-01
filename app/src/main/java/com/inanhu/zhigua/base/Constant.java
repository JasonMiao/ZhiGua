package com.inanhu.zhigua.base;

/**
 * Created by iNanHu on 2016/7/17.
 */
public class Constant {

    public static final String DEFAULT_NAME_FILTER = "JLP";  // 默认蓝牙名过滤器

//    public static final String SERVER = "http://www.zhinengshagua.com"; // 服务器地址
    public static final String SERVER = "http://www.zhiguait.com"; // 服务器地址

    public static final String START_URL = SERVER + "/zgskwechat"; // 加载的首页
//    public static final String START_URL = SERVER + "/zgskwechat/WechatLoginAction_employeeLogin"; // 加载的首页
    public static final String PRINTER_LINK_URL = SERVER + "/zgskwechat/link_printer";
    public static final String USER_LOGOUT = SERVER + "/zgskwechat/app_exit";
    public static final String PRINTER_WS_URL = SERVER + ":8086/zgskwxWS"; // 打印机webservice接口
    public static final String LOGIN_ACTION = SERVER + "/zgskwechat/WechatLoginAction_login";

    public class Key {
        public static final String BTADAPTER = "btAdapter";
        public static final String PRINTER = "printer";
        public static final String START_URL = "startUrl";
        public static final String HOST = "host";
        public static final String SERVERID = "serverId";
        public static final String CLIENTTOPIC = "clientTopic";
        public static final String USERNAME = "userName";
        public static final String PASSWORD = "passWord";

        public static final String LOGIN_USERNAME = "login_username";
        public static final String LOGIN_USERPWD = "login_userpwd";
        public static final String LOGIN_ROLE_TYPE = "login_role_type";
    }
}
