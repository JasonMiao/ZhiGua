package printer.porting;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.TextUtils;

import com.inanhu.zhigua.activity.BtConfigActivity;
import com.inanhu.zhigua.base.Constant;
import com.inanhu.zhigua.base.GlobalValue;
import com.inanhu.zhigua.base.ZhiGuaApp;
import com.inanhu.zhigua.service.PrintService;
import com.inanhu.zhigua.util.CommonUtils;
import com.inanhu.zhigua.util.LogUtil;
import com.uzmap.pkg.uzkit.request.APICloudHttpClient;
import com.uzmap.pkg.uzkit.request.HttpParams;
import com.uzmap.pkg.uzkit.request.HttpPost;
import com.uzmap.pkg.uzkit.request.HttpResult;
import com.uzmap.pkg.uzkit.request.RequestCallback;

import org.dom4j.DocumentException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

import printer.JQPrinter;
import printer.Port;
import printer.esc.ESC;

/**
 * 打印机封装类（济强ESC）
 *
 * @author iNanHu
 */
public class JQEscPrinterManager {

    private static final String TAG = "JQEscPrinterManager";

    /****************************************
     * ********** 打印机参数设置（默认值） *****************
     ***************************************/


    /***************
     * END
     *********************/

    private JQPrinter printer;

    /**
     * 初始化打印机
     *
     * @param btAdapter
     * @param btDeviceString
     * @return
     */
    public String init(BluetoothAdapter btAdapter, String btDeviceString) {
        if (printer != null) {
            printer.close();
        }
        printer = new JQPrinter(btAdapter, btDeviceString);
        if (!printer.open(JQPrinter.PRINTER_TYPE.ULT113x)) {
            return "01";
        }
        if (!printer.wakeUp()) {
            return "02";
        }
        return "00";
    }

    /**
     * 关闭打印机
     *
     * @return
     */
    public boolean close() {
        if (printer != null && printer.isOpen) {
            printer.close();
        }
        return true;
    }

    /**
     * 打印机是否打开成功
     *
     * @return
     */
    public boolean isPrinterOpened() {
        if (printer != null) {
            return printer.getPortState() == Port.PORT_STATE.PORT_OPEND ? true : false;
        } else {
            return false;
        }
    }

    /**
     * 唤醒打印机
     * 注意:部分手持蓝牙连接第一次不稳定，会造成开头字符乱码，可以通常这个方法来避免此问题
     *
     * @return
     */
    public boolean wakeUp(){
        return printer.wakeUp();
    }

    /**
     * 打印机状态是否正常
     *
     * @return
     */
    public String isPrinterOk() {
        if (printer != null) {
            if (printer.getPortState() != Port.PORT_STATE.PORT_OPEND) {
                return "03";
            }
            if (!printer.getPrinterState(3000)) {
                return "04";
            }
            if (printer.printerInfo.isNoPaper) {
                return "05";
            }
            if (printer.printerInfo.isOverHeat) {
                return "06";
            }
            if (printer.printerInfo.isBatteryLow) {
                return "07";
            }
            if (printer.printerInfo.isPrinting) {
                return "08";
            }
            if (printer.printerInfo.isCoverOpen) {
                return "09";
            }
            return "00";
        } else {
            return "10";
        }
    }

    /**
     * 换行回车
     *
     * @return
     */
    public boolean feedEnter() {
        return printer.esc.feedEnter();
    }

    /**
     * 走纸几行
     *
     * @param lines 行数
     * @return
     */
    public boolean feedLines(int lines) {
        return printer.esc.feedLines(lines);
    }

    /**
     * 走纸几点
     *
     * @param dots 点数
     * @return
     */
    public boolean feedDots(int dots) {
        return printer.esc.feedDots(dots);
    }


    /**
     * 打印文字
     *
     * @param text 打印内容
     * @return
     */
    public boolean printText(String text) {
        return printer.esc.text.printOut(text);
    }

    /**
     * 打印文字
     *
     * @param x    X坐标
     * @param y    Y坐标
     * @param text 打印内容
     * @return
     */
    public boolean printText(int x, int y, String text) {
        return printer.esc.text.drawOut(x, y, text);
    }

    public boolean printText(int x, int y, ESC.FONT_HEIGHT height, boolean bold, ESC.TEXT_ENLARGE enlarge, String text){
        return printer.esc.text.printOut(x, y, height, bold, enlarge, text);
    }

    /**
     * 打印文字
     *
     * @param align 对齐方式
     * @param bold  是否粗体
     * @param text  内容
     * @return
     */
    public boolean printText(JQPrinter.ALIGN align, boolean bold, String text) {
        return printer.esc.text.drawOut(align, bold, text);
    }

    /**
     * 打印文字
     *
     * @param align   对齐方式
     * @param height  字体倍高
     * @param bold    是否粗体
     * @param enlarge 字体大小
     * @param text    打印内容
     * @return
     */
    public boolean printText(JQPrinter.ALIGN align, ESC.FONT_HEIGHT height, boolean bold, ESC.TEXT_ENLARGE enlarge, String text) {
        return printer.esc.text.printOut(align, height, bold, enlarge, text);
    }

    /**
     * 打印图片
     *
     * @param x      X坐标
     * @param y      Y坐标
     * @param bitmap 图片
     * @return
     */
    public boolean printImage(int x, int y, Bitmap bitmap) {
        return printer.esc.image.drawOut(x, y, bitmap);
    }

    public boolean printImage(int x, Bitmap bitmap, int sleep_time, int base_image_height){
        return printer.esc.image.printOutFast(x, bitmap, sleep_time, base_image_height);
    }

    /**
     * 打印图片
     *
     * @param x                 X坐标
     * @param y                 Y坐标
     * @param image_width_dots  图片宽度
     * @param image_height_dots 图片高度
     * @param mode              缩放大小
     * @param bitmap            图片
     * @return
     */
    public boolean printImage(int x, int y, int image_width_dots, int image_height_dots, ESC.IMAGE_ENLARGE mode, Bitmap bitmap) {
        byte[] image_data = bitmap2Bytes(bitmap);
        return printer.esc.image.drawOut(x, y, image_width_dots, image_height_dots, mode, image_data);
    }

    /**
     * bitmap转byte数组
     *
     * @param bm
     * @return
     */
    public byte[] bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    /**
     * 绘制线段
     *
     * @param line_points
     * @return
     */
    public boolean printLine(ESC.LINE_POINT[] line_points) {
        return printer.esc.graphic.linedrawOut(line_points);
    }

    /**
     * 打印机上线
     *
     * @param context
     * @param btName
     * @param btAddress
     */
    public static void printerOnline(final Context context, final String btName, final String btAddress) {
        String userName = (String) GlobalValue.getInstance().getGlobal(Constant.Key.LOGIN_USERNAME);
        String userPwd = (String) GlobalValue.getInstance().getGlobal(Constant.Key.LOGIN_USERPWD);
        String roleType = (String) GlobalValue.getInstance().getGlobal(Constant.Key.LOGIN_ROLE_TYPE);

        StringBuffer reqxml = new StringBuffer();
        reqxml.append("<?xml version=\"1.0\" encoding=\"utf-16\"?>\n");
        reqxml.append("<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">\n");
        reqxml.append("<soap:Body>\n");
        reqxml.append("<printerOnlineWS xmlns=\"http://ws.wechat.zgkj.com/\">\n");
        reqxml.append("<arg0 xmlns=\"\">\n");
        reqxml.append("<nonce>dcbf94fb-36e4-459f-a8b1-9228c103b0fa</nonce>\n");
        reqxml.append("<signature>1755ce87efdcc37ad6da0625df33cbf34d52659e</signature>\n");
        reqxml.append("<timestamp>1477473830545</timestamp>\n");
        reqxml.append("<accounts>" + userName + "</accounts>\n");
        reqxml.append("<mac>" + btAddress +"</mac>\n");
        reqxml.append("<password>" + userPwd + "</password>\n");
        reqxml.append("<printerName>" + btName + "</printerName>\n");
        reqxml.append("<roleType>" + roleType + "</roleType>\n");
        reqxml.append("</arg0>\n");
        reqxml.append("</printerOnlineWS>\n");
        reqxml.append("</soap:Body>\n");
        reqxml.append("</soap:Envelope>\n");
        HttpParams params = new HttpParams();
        params.setBody(reqxml);
        HttpPost post = new HttpPost(Constant.PRINTER_WS_URL, params);
        post.addHeader("content-type", "text/xml;charset=utf-8");
        post.setCallback(new RequestCallback() {
            @Override
            public void onFinish(HttpResult httpResult) {
                LogUtil.e(TAG, "result: " + httpResult.data);
                try {
                    Map<String, String> map = CommonUtils.parseXML(httpResult.data);
                    LogUtil.e(TAG, "resultCode: " + map.get("resultCode"));
                    LogUtil.e(TAG, "mqttHost: " + map.get("mqttHost"));
                    String resultCode = map.get("resultCode");
                    if (!TextUtils.isEmpty(resultCode) && "000000".equals(resultCode)) { // 状态上送成功
                        // 保存打印机（蓝牙）属性到全局
                        GlobalValue.getInstance().saveGlobal(BtConfigActivity.BLUETOOTH_DEVICE_NAME, btName);
                        GlobalValue.getInstance().saveGlobal(BtConfigActivity.BLUETOOTH_DEVICE_ADDRESS, btAddress);
                        // 启动打印服务
                        Intent intent = new Intent(context, PrintService.class);
                        intent.putExtra(Constant.Key.HOST, map.get("mqttHost"));
                        intent.putExtra(Constant.Key.SERVERID, map.get("mqttServerId"));
                        intent.putExtra(Constant.Key.CLIENTTOPIC, map.get("equipmentID"));
                        intent.putExtra(Constant.Key.USERNAME, map.get("mqttUserName"));
                        intent.putExtra(Constant.Key.PASSWORD, map.get("mqttPassWord"));
                        context.startService(intent);
                        // 参数一：唯一的通知标识；参数二：通知消息。
                    } else {
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (DocumentException e) {
                    e.printStackTrace();
                }
            }
        });
        APICloudHttpClient.instance().request(post);
    }

    /**
     * 打印机下线
     */
    public static void printerOffline() {
        String userName = (String) GlobalValue.getInstance().getGlobal(Constant.Key.LOGIN_USERNAME);
        String userPwd = (String) GlobalValue.getInstance().getGlobal(Constant.Key.LOGIN_USERPWD);
        String roleType = (String) GlobalValue.getInstance().getGlobal(Constant.Key.LOGIN_ROLE_TYPE);

        // 获取当前连接的打印机（蓝牙）属性
        String btName = (String) GlobalValue.getInstance().getGlobal(BtConfigActivity.BLUETOOTH_DEVICE_NAME);
        String btAddress = (String) GlobalValue.getInstance().getGlobal(BtConfigActivity.BLUETOOTH_DEVICE_ADDRESS);
        StringBuffer reqxml = new StringBuffer();
        reqxml.append("<?xml version=\"1.0\" encoding=\"utf-16\"?>\n");
        reqxml.append("<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">\n");
        reqxml.append("<soap:Body>\n");
        reqxml.append("<printerOfflineWS xmlns=\"http://ws.wechat.zgkj.com/\">\n");
        reqxml.append("<arg0 xmlns=\"\">\n");
        reqxml.append("<nonce>dcbf94fb-36e4-459f-a8b1-9228c103b0fa</nonce>\n");
        reqxml.append("<signature>1755ce87efdcc37ad6da0625df33cbf34d52659e</signature>\n");
        reqxml.append("<timestamp>1477473830545</timestamp>\n");
        reqxml.append("<accounts>" + userName + "</accounts>\n");
        reqxml.append("<mac>" + btAddress +"</mac>\n");
        reqxml.append("<password>" + userPwd + "</password>\n");
        reqxml.append("<printerName>" + btName + "</printerName>\n");
        reqxml.append("<roleType>" + roleType + "</roleType>\n");
        reqxml.append("</arg0>\n");
        reqxml.append("</printerOfflineWS>\n");
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
                    LogUtil.e(TAG, "resultCode: " + map.get("resultCode"));
                    String resultCode = map.get("resultCode");
                    if (!TextUtils.isEmpty(resultCode) && "000000".equals(resultCode)) { // 状态上送成功
//                        GlobalValue.getInstance().deleteGlobal(BtConfigActivity.BLUETOOTH_DEVICE_NAME);
//                        GlobalValue.getInstance().deleteGlobal(BtConfigActivity.BLUETOOTH_DEVICE_ADDRESS);
                    } else {

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (DocumentException e) {
                    e.printStackTrace();
                }
            }
        });
        APICloudHttpClient.instance().request(post);
    }

    /**
     * 打印机状态上送
     *
     * @param isBatteryLow
     * @param isCoverOpen
     * @param isNoPaper
     * @param isOverHeat
     * @param isPrinting
     */
    public static void printerStatus(String isBatteryLow, String isCoverOpen, String isNoPaper, String isOverHeat, String isPrinting) {
        // 获取当前连接的打印机（蓝牙）属性
        String btName = (String) GlobalValue.getInstance().getGlobal(BtConfigActivity.BLUETOOTH_DEVICE_NAME);
        String btAddress = (String) GlobalValue.getInstance().getGlobal(BtConfigActivity.BLUETOOTH_DEVICE_ADDRESS);
        StringBuffer reqxml = new StringBuffer();
        reqxml.append("<?xml version=\"1.0\" encoding=\"utf-16\"?>\n");
        reqxml.append("<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">\n");
        reqxml.append("<soap:Body>\n");
        reqxml.append("<reportPrinterStatusWS xmlns=\"http://ws.wechat.zgkj.com/\">\n");
        reqxml.append("<arg0 xmlns=\"\">\n");
        reqxml.append("<nonce>dcbf94fb-36e4-459f-a8b1-9228c103b0fa</nonce>\n");
        reqxml.append("<signature>1755ce87efdcc37ad6da0625df33cbf34d52659e</signature>\n");
        reqxml.append("<timestamp>1477473830545</timestamp>\n");
        reqxml.append("<batteryLow>" + isBatteryLow + "</batteryLow>\n");
        reqxml.append("<coverOpen>" + isCoverOpen + "</coverOpen>\n");
        reqxml.append("<mac>" + btAddress +"</mac>\n");
        reqxml.append("<noPaper>" + isNoPaper + "</noPaper>\n");
        reqxml.append("<overHeat>" + isOverHeat + "</overHeat>\n");
        reqxml.append("<printerName>" + btName + "</printerName>\n");
        reqxml.append("<printing>" + isPrinting + "</printing>\n");
        reqxml.append("</arg0>\n");
        reqxml.append("</reportPrinterStatusWS>\n");
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
                    LogUtil.e(TAG, "resultCode: " + map.get("resultCode"));
                    String resultCode = map.get("resultCode");
                    if (!TextUtils.isEmpty(resultCode) && "000000".equals(resultCode)) { // 状态上送成功
//                        GlobalValue.getInstance().deleteGlobal(BtConfigActivity.BLUETOOTH_DEVICE_NAME);
//                        GlobalValue.getInstance().deleteGlobal(BtConfigActivity.BLUETOOTH_DEVICE_ADDRESS);
                        LogUtil.e(TAG, "打印机状态上送成功");
                    } else {
                        LogUtil.e(TAG, "打印机状态上送失败");
                    }
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
