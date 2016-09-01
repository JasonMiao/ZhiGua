package com.jq.printer.porting;

import android.bluetooth.BluetoothAdapter;
import android.graphics.Bitmap;

import com.jq.printer.JQPrinter;
import com.jq.printer.JQPrinter.ALIGN;
import com.jq.printer.Port.PORT_STATE;
import com.jq.printer.esc.ESC;

import java.io.ByteArrayOutputStream;

/**
 * 打印机封装类（济强ESC）
 *
 * @author iNanHu
 */
public class JQEscPrinterManager {

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
            return printer.getPortState() == PORT_STATE.PORT_OPEND ? true : false;
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
            if (printer.getPortState() != PORT_STATE.PORT_OPEND) {
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

    public boolean printText(int x, int y, ESC.FONT_HEIGHT height,boolean bold, ESC.TEXT_ENLARGE enlarge, String text){
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
    public boolean printText(ALIGN align, boolean bold, String text) {
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
    public boolean printText(ALIGN align, ESC.FONT_HEIGHT height, boolean bold, ESC.TEXT_ENLARGE enlarge, String text) {
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

}
