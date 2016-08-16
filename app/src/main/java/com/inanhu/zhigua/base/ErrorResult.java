package com.inanhu.zhigua.base;

import java.util.HashMap;
import java.util.Map;

/**
 * 错误信息列表
 * <p/>
 * Created by Jason on 2016/7/18.
 */
public class ErrorResult {

    public static String getError(String code) {
        if (errorInfo.containsKey(code)) {
            return code + " " + errorInfo.get(code);
        }
        return "未知错误";
    }

    private static Map<String, String> errorInfo = new HashMap<String, String>();

    static {

        errorInfo.put("00", "成功");
        errorInfo.put("01", "初始化打印机失败-Open失败");
        errorInfo.put("02", "初始化打印机失败-wakeUp失败");
        errorInfo.put("03", "蓝牙错误");
        errorInfo.put("04", "获取打印机状态失败");
        errorInfo.put("05", "打印机缺纸");
        errorInfo.put("06", "打印机打印头过热");
        errorInfo.put("07", "打印机电池电压过低");
        errorInfo.put("08", "打印机正在打印");
        errorInfo.put("09", "打印机纸仓盖未关闭");
        errorInfo.put("10", "打印机未初始化");
    }
}
