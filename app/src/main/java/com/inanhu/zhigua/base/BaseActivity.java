package com.inanhu.zhigua.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.inanhu.zhigua.R;
import com.inanhu.zhigua.service.PrintService;
import com.inanhu.zhigua.util.CommonUtils;
import com.inanhu.zhigua.util.LogUtil;
import com.inanhu.zhigua.util.NetUtil;
import com.inanhu.zhigua.widget.CustomProgress;
import com.uzmap.pkg.uzkit.request.APICloudHttpClient;
import com.uzmap.pkg.uzkit.request.HttpParams;
import com.uzmap.pkg.uzkit.request.HttpPost;
import com.uzmap.pkg.uzkit.request.HttpResult;
import com.uzmap.pkg.uzkit.request.RequestCallback;

import org.dom4j.DocumentException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import printer.porting.JQEscPrinterManager;


/**
 * Created by iNanHu on 2016/6/27.
 */
public class BaseActivity extends AppCompatActivity {
    protected String TAG;
    protected CustomProgress dialog;
    protected ZhiGuaApp application;
    protected JQEscPrinterManager printer;

    protected final String HTTP_TASK_KEY = "HttpTaskKey_" + hashCode();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        TAG = this.getClass().getSimpleName();
        application = ZhiGuaApp.getInstance();
        printer = ZhiGuaApp.getPrinter();

        super.onCreate(savedInstanceState);
    }


    /**
     * 标题栏是否显示返回键
     *
     * @param isNeedToShow
     */
    protected void showTopBarBack(boolean isNeedToShow) {
        TextView tvTopBarBack = (TextView) findViewById(R.id.id_topbar_back);
        if (isNeedToShow) {
            tvTopBarBack.setVisibility(View.VISIBLE);
            tvTopBarBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        } else {
            tvTopBarBack.setVisibility(View.INVISIBLE);
        }
    }


    /**
     * 是否显示标题栏
     *
     * @param isNeedToShow
     */
    protected void showTopBar(boolean isNeedToShow) {
        RelativeLayout rlTopBar = (RelativeLayout) findViewById(R.id.id_topbar);
        if (isNeedToShow) {
            rlTopBar.setVisibility(View.VISIBLE);
        } else {
            rlTopBar.setVisibility(View.GONE);
        }
    }

    /**
     * 设置标题栏标题
     *
     * @param title
     */
    protected void setTopBarTitle(String title) {
        ((TextView) findViewById(R.id.activity_title)).setText(title);
    }

    /**
     * 设置标题栏标题
     *
     * @param resId
     */
    protected void setTopBarTitle(int resId) {
        ((TextView) findViewById(R.id.activity_title)).setText(resId);
    }

    protected void setTopBarRight(String text) {
        ((TextView) findViewById(R.id.id_topbar_right)).setText(text);
    }

    /**
     * 判断网络是否连接
     *
     * @return
     */
    protected boolean isNetConnected() {
        return NetUtil.isConnected(this);
    }

    /**
     * 判断是否为WiFi连接
     *
     * @return
     */
    protected boolean isNetWifi() {
        return NetUtil.isWifi(this);
    }

    /**
     * 显示进度条
     *
     * @param message
     */
    protected void showProgressDialog(String message) {
        dialog = CustomProgress.show(this, message, true, null);
    }

    /**
     * 关闭进度条
     */
    protected void closeProgressDialog() {
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
    }

    /**
     * 打印机上线
     */
    public void printerOnline(String btName, String btAddress) {
        StringBuffer reqxml = new StringBuffer();
        reqxml.append("<?xml version=\"1.0\" encoding=\"utf-16\"?>\n");
        reqxml.append("<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">\n");
        reqxml.append("<soap:Body>\n");
        reqxml.append("<printerOnlineWS xmlns=\"http://ws.wechat.zgkj.com/\">\n");
        reqxml.append("<arg0 xmlns=\"\">\n");
        reqxml.append("<nonce>dcbf94fb-36e4-459f-a8b1-9228c103b0fa</nonce>\n");
        reqxml.append("<signature>1755ce87efdcc37ad6da0625df33cbf34d52659e</signature>\n");
        reqxml.append("<timestamp>1477473830545</timestamp>\n");
        reqxml.append("<accounts>fengzhihua</accounts>\n");
        reqxml.append("<mac>" + btAddress +"</mac>\n");
        reqxml.append("<password>3Kgnxk8CxNvHnbTarmHtdw==</password>\n");
        reqxml.append("<printerName>" + btName + "</printerName>\n");
        reqxml.append("<roleType>1</roleType>\n");
        reqxml.append("</arg0>\n");
        reqxml.append("</printerOnlineWS>\n");
        reqxml.append("</soap:Body>\n");
        reqxml.append("</soap:Envelope>\n");
        HttpParams params = new HttpParams();
        params.setBody(reqxml.toString());
        HttpPost post = new HttpPost(Constant.PRINTER_WS_URL, params);
        post.addHeader("content-type", "text/xml;charset=utf-8");
        post.setCallback(new RequestCallback() {
            @Override
            public void onFinish(HttpResult httpResult) {
                LogUtil.e(TAG, "result: " + httpResult.data);
                try {
                    Map<String, String> map = CommonUtils.parseXML(httpResult.data);
                    LogUtil.e(TAG, "mqttHost: " + map.get("mqttHost"));
                    Intent intent = new Intent(BaseActivity.this, PrintService.class);
                    intent.putExtra(Constant.Key.HOST, map.get("mqttHost"));
                    intent.putExtra(Constant.Key.SERVERID, map.get("mqttServerId"));
                    intent.putExtra(Constant.Key.CLIENTTOPIC, map.get("equipmentID"));
                    intent.putExtra(Constant.Key.USERNAME, map.get("mqttUserName"));
                    intent.putExtra(Constant.Key.PASSWORD, map.get("mqttPassWord"));
                    startService(intent);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (DocumentException e) {
                    e.printStackTrace();
                }
            }
        });
        APICloudHttpClient.instance().request(post);
    }

}
