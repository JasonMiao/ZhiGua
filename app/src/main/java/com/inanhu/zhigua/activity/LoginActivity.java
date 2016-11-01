package com.inanhu.zhigua.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.inanhu.zhigua.R;
import com.inanhu.zhigua.base.BaseActivity;
import com.inanhu.zhigua.base.Constant;
import com.inanhu.zhigua.base.GlobalValue;
import com.inanhu.zhigua.base.ZhiGuaApp;
import com.inanhu.zhigua.util.LogUtil;
import com.inanhu.zhigua.util.MD5Util;
import com.inanhu.zhigua.util.ToastUtil;
import com.uzmap.pkg.uzkit.request.APICloudHttpClient;
import com.uzmap.pkg.uzkit.request.HttpGet;
import com.uzmap.pkg.uzkit.request.HttpPost;
import com.uzmap.pkg.uzkit.request.HttpResult;
import com.uzmap.pkg.uzkit.request.RequestCallback;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.util.Map;
import java.util.Set;

import printer.porting.JQEscPrinterManager;

/**
 * Created by Jason on 2016/10/27.
 */

public class LoginActivity extends BaseActivity {

    EditText etUsername, etUserPwd;
    RadioButton rbRoleEmployee, rbRoleBoss;
    Button btnLogin;

    //    private String roleType; // 用户角色：0-员工 1-老板
    private String isApp = "1"; // 客户端请求

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initView();
    }

    private void initView() {
        etUsername = (EditText) findViewById(R.id.et_username);
        etUserPwd = (EditText) findViewById(R.id.et_user_pwd);
        rbRoleEmployee = (RadioButton) findViewById(R.id.rb_role_type_employee);
        rbRoleBoss = (RadioButton) findViewById(R.id.rb_role_type_boss);
        btnLogin = (Button) findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username = etUsername.getText().toString().trim();
                final String userpwd = etUserPwd.getText().toString().trim();
//                final String username = "fengzhihua:0001";
//                final String userpwd = "feng123456/";
                final String roleType = rbRoleEmployee.isChecked() ? "0" : "1";

                if (TextUtils.isEmpty(username) || TextUtils.isEmpty(userpwd)) {
                    ToastUtil.showToast("账号密码不能为空");
                    return;
                }
                String loginUrl = Constant.LOGIN_ACTION
                        + "?account=" + username
                        + "&password=" + userpwd
                        + "&roleType=" + roleType
                        + "&isApp=" + isApp;
                LogUtil.e(TAG, loginUrl);
                HttpGet get = new HttpGet(loginUrl);
                get.setCacheEntry(null);
                get.setTimeout(10);
                get.setCallback(new RequestCallback() {
                    @Override
                    public void onFinish(final HttpResult httpResult) {
                        String data = httpResult.data;
                        JSONObject resp = (JSONObject) JSONValue.parse(data);
                        String resultCode = (String) resp.get("resultCode");
                        LogUtil.e(TAG, "resultCode=" + resultCode);
                        if ("000000".equals(resultCode)) {
                            // 获取请求地址
                            String url = (String) resp.get("url");
                            LogUtil.e(TAG, "url=" + url);
                            // 保存用户信息
                            GlobalValue.getInstance().saveGlobal(Constant.Key.LOGIN_USERNAME, username);
                            GlobalValue.getInstance().saveGlobal(Constant.Key.LOGIN_USERPWD, MD5Util.encrypt(userpwd));
                            GlobalValue.getInstance().saveGlobal(Constant.Key.LOGIN_ROLE_TYPE, roleType);
                            // 获取cookie
                            Map<String, String> map = httpResult.headers;
                            Set<String> set = map.keySet();
                            for (String key : set) {
                                LogUtil.e(TAG, "key=" + key + " value=" + map.get(key));
                            }
                            String cookie = map.get("Set-Cookie");
                            LogUtil.e(TAG, "cookie:" + cookie);
                            if (!TextUtils.isEmpty(cookie)) {
                                // 同步cookie
                                syncCookie(Constant.START_URL, cookie);
                            }
                            // 跳转主页
                            startActivity(new Intent(LoginActivity.this, WebPageActivity.class).putExtra(Constant.Key.START_URL, Constant.START_URL + "/" + url));
                            finish();
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ToastUtil.showToast("用户名或密码错误");

                                }
                            });
                        }
                    }
                });
                APICloudHttpClient.instance().request(get);
            }
        });
    }

    /**
     * 将cookie同步到WebView
     *
     * @param url    WebView要加载的url
     * @param cookie 要同步的cookie
     * @return true 同步cookie成功，false同步cookie失败
     * @Author JPH
     */
    public static boolean syncCookie(String url, String cookie) {
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setCookie(url, cookie);//如果没有特殊需求，这里只需要将session id以"key=value"形式作为cookie即可
        String newCookie = cookieManager.getCookie(url);
        return TextUtils.isEmpty(newCookie) ? false : true;
    }

    private long firstTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        long secondTime = System.currentTimeMillis();
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (secondTime - firstTime < 2000) {
                finish();
            } else {
                ToastUtil.showToast("再按一次退出");
                firstTime = System.currentTimeMillis();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
