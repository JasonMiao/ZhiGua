/*
package printer.porting;

import android.bluetooth.BluetoothAdapter;
import android.content.res.Resources;
import android.graphics.Point;


import printer.JQPrinter;
import printer.Port;

*/
/**
 * 打印机封装类
 *
 * @author iNanHu
 *//*

public class PrinterManager {

    */
/****************************************
     * ********** 打印机参数设置（默认值） *****************
     ***************************************//*

    // Page页开始
    public int originX = 0; // Page页面参考原点相对标签纸当前位置左上角的 x轴偏移量
    public int originY = 0; // Page页面参考原点相对标签纸当前位置左上角的 y轴偏移量
    public int pageWidth = 576; // Page页面页宽， x+Width的取值范围为：[1,576]
    public int pageHeight = 1200; // Page页面页高，Height的取值范围为：[1,1200]
//    public Page.PAGE_ROTATE rotate = Page.PAGE_ROTATE.x0; // Page页面旋转角度，Rotate的取值范围为：{0,1}。当Rotate为 0时，页面不旋转。当 Rotate为1时，页面旋转 90°打印

    // Page页

    */
/***************
     * END
     *********************//*


    private JQPrinter printer;

    */
/**
     * 初始化打印机
     *
     * @param btAdapter
     * @param btDeviceString
     * @return
     *//*

    public String init(BluetoothAdapter btAdapter, String btDeviceString) {
        if (printer != null) {
            printer.close();
        }
        printer = new JQPrinter(btAdapter, btDeviceString);
        if (!printer.open(printer.JQPrinter.PRINTER_TYPE.ULT113x)) {
            return "01";
        }
        if (!printer.wakeUp()) {
            return "02";
        }
        return "00";
    }

    */
/**
     * 关闭打印机
     *
     * @return
     *//*

    public boolean close() {
        if (printer != null && printer.isOpen) {
            printer.close();
        }
        return true;
    }

    */
/**
     * 打印机是否打开成功
     *
     * @return
     *//*

    public boolean isPrinterOpened() {
        if (printer != null) {
            return printer.getPortState() == Port.PORT_STATE.PORT_OPEND ? true : false;
        } else {
            return false;
        }
    }

    */
/**
     * 打印机状态是否正常
     *
     * @return
     *//*

    public String isPrinterOk() {
        if (printer != null){
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

    */
/**
     * Page页开始（默认值）
     *
     * @return
     *//*

    public boolean pageStart() {
        return pageStart(originX, originY, pageWidth, pageHeight, rotate);
    }

    */
/**
     * Page页开始
     *
     * @param originX
     * @param originY
     * @param pageWidth
     * @param pageHeight
     * @param rotate
     * @return
     *//*

    public boolean pageStart(int originX, int originY, int pageWidth, int pageHeight, PAGE_ROTATE rotate) {
        return printer.jpl.page.start(originX, originY, pageWidth, pageHeight, rotate);
    }

    */
/**
     * 绘制打印页面结束
     *
     * @return
     *//*

    public boolean pageEnd() {
        return printer.jpl.page.end();
    }

    */
/**
     * 打印页面内容 之前做的页面处理，只是把打印对象画到内存中，必须要通过这个方法把内容打印出来
     *
     * @return
     *//*

    public boolean print() {
        return printer.jpl.page.print();
    }

    */
/**
     * 当前页面重复打印几次
     *
     * @return
     *//*

    public boolean print(int count) {
        return printer.jpl.page.print(count);
    }

    */
/**
     * 走纸 topPosition 标识走纸停止位置标示，取值范围: {0, 3}。 StopType = 0，切纸口与标签缝平齐处停止走纸；
     * StopType = 1，光标与标签头平齐处停止走纸； StopType = 2，切纸口与黑标下方平齐处停止走纸； StopType =
     * 3，光标与黑标下方平齐平齐处停止走纸；
     *
     * Offset 标识停止位置偏移。当打印机检测到标签头或标签为后，继续走纸 Offset 各点的长度
     *//*


    */
/**
     * 走纸到下一张标签开始
     *
     * @return
     *//*

    public boolean feedNextLabelBegin() {
        return printer.jpl.feedNextLabelBegin();
    }

    public boolean feedNextLabelEnd(int dots) {
        return printer.jpl.feedNextLabelEnd(dots);
    }

    public boolean feedBack(int dots) {
        return printer.jpl.feedBack(dots);
    }

    public boolean feedMarkOrGap(int dots) {
        return printer.jpl.feedMarkOrGap(dots);
    }

    public boolean feedMarkEnd(int dots) {
        return printer.jpl.feedMarkEnd(dots);
    }

    public boolean feedMarkBegin(int dots) {
        return printer.jpl.feedMarkBegin(dots);
    }

    */
/**
     * 根据指定坐标打印文本
     *
     * @param x    定义文本起始位置 x坐标，取值范围：[0, Page_Width-1]
     * @param y    定义文本起始位置 y坐标，取值范围：[0, Page_Height-1]
     * @param text 要打印的，以 0x00终止的文本字符串数据流
     * @return
     *//*

    public boolean printText(int x, int y, String text) {
        return printer.jpl.text.drawOut(x, y, text);
    }

    */
/**
     * 打印文本（当文本宽度与文本起始坐标 x的和大于页面宽度时,文本被截断打印）
     *
     * @param x           定义文本起始位置 x坐标，取值范围：[0, Page_Width-1]
     * @param y           定义文本起始位置 y坐标，取值范围：[0, Page_Height-1]
     * @param text        要打印的，以 0x00终止的文本字符串数据流
     * @param fontHeight  文本字符字体高度，有效值范围为{16, 24, 32, 48, 64, 80, 96}
     * @param bold        是否加粗
     * @param reverse     是否反白（文本反白，黑底白字）
     * @param underLine   是否有下划线
     * @param deleteLine  是否有删除线
     * @param enlargeX    字体宽度放大倍数
     * @param enlargeY    字体高度放大倍数
     * @param rotateAngle 旋转标识
     * @return
     *//*

    public boolean printText(int x, int y, String text, int fontHeight, boolean bold, boolean reverse, boolean underLine, boolean deleteLine, TEXT_ENLARGE enlargeX,
                             TEXT_ENLARGE enlargeY, JPL.ROTATE rotateAngle) {
        return printer.jpl.text.drawOut(x, y, text, fontHeight, bold, reverse, underLine, deleteLine, enlargeX, enlargeY, rotateAngle);
    }

    */
/**
     * 打印文本（当文本宽度与文本起始坐标 x的和大于页面宽度时,文本被截断打印）
     *
     * @param align       水平对齐方式
     * @param y           定义文本起始位置 y坐标，取值范围：[0, Page_Height-1]
     * @param text        要打印的，以 0x00终止的文本字符串数据流
     * @param fontHeight  文本字符字体高度，有效值范围为{16, 24, 32, 48, 64, 80, 96}
     * @param bold        是否加粗
     * @param reverse     是否反白（文本反白，黑底白字）
     * @param underLine   是否有下划线
     * @param deleteLine  是否有删除线
     * @param enlargeX    字体宽度放大倍数
     * @param enlargeY    字体高度放大倍数
     * @param rotateAngle 旋转标识
     * @return
     *//*

    public boolean printText(ALIGN align, int y, String text, int fontHeight, boolean bold, boolean reverse, boolean underLine, boolean deleteLine, TEXT_ENLARGE enlargeX,
                             TEXT_ENLARGE enlargeY, ROTATE rotateAngle) {
        return printer.jpl.text.drawOut(align, y, text, fontHeight, bold, reverse, underLine, deleteLine, enlargeX, enlargeY, rotateAngle);
    }

    */
/**
     * 打印线段
     *
     * @param start 起始点
     * @param end   终止点
     * @return
     *//*

    public boolean printLine(Point start, Point end) {
        return printer.jpl.graphic.line(start, end);
    }

    */
/**
     * 打印指定颜色，宽度的线段
     *
     * @param start 起点
     * @param end   终点
     * @param width 直线段线宽，取值范围：[1，Page_Height-1]
     * @param color 直线段颜色，取值范围：{0, 1}。当 Color为 1时，线段为黑色。当 Color为 0时，线段为白色
     * @return
     *//*

    public boolean printLine(Point start, Point end, int width, COLOR color) {
        return printer.jpl.graphic.line(start, end, width, color);
    }

    */
/**
     * 打印指定宽度的黑色线段
     *
     * @param start 起点
     * @param end   终点
     * @param width 直线段线宽
     * @return
     *//*

    public boolean printLine(Point start, Point end, int width) {
        return printer.jpl.graphic.line(start, end, width);
    }

    */
/**
     * 打印矩形
     *
     * @param left   矩形框左上角 x坐标值，取值范围：[0, Page_Width-1]
     * @param top    矩形框左上角 y坐标值。取值范围：[0, Page_Height-1]
     * @param right  矩形框右下角 x坐标值。取值范围：[0, Page_Width-1]
     * @param bottom 矩形框左上角
     *               y坐标值。取值范围：[0, Page_Height-1]
     * @return
     *//*

    public boolean printRect(int left, int top, int right, int bottom) {
        return printer.jpl.graphic.rect(left, top, right, bottom);
    }

    */
/**
     * 打印指定线宽，框线颜色的矩形
     *
     * @param left   矩形框左上角 x坐标值，取值范围：[0, Page_Width-1]
     * @param top    矩形框左上角 y坐标值。取值范围：[0, Page_Height-1]
     * @param right  矩形框右下角 x坐标值。取值范围：[0, Page_Width-1]
     * @param bottom 矩形框左上角 y坐标值。取值范围：[0, Page_Height-1]
     * @param width  矩形框线宽
     * @param color  矩形框线颜色，曲直范围{0，1}。当 Color = 1时，绘制黑色矩形宽，Color = 0时，绘制白色矩形框
     * @return
     *//*

    public boolean printRect(int left, int top, int right, int bottom, int width, COLOR color) {
        return printer.jpl.graphic.rect(left, top, right, bottom, width, color);
    }

    */
/**
     * @param left   矩形框左上角 x坐标值，取值范围：[0, Page_Width-1]
     * @param top    矩形框左上角 y坐标值。取值范围：[0, Page_Height-1]
     * @param right  矩形框右下角 x坐标值。取值范围：[0, Page_Width-1]
     * @param bottom 矩形框左上角 y坐标值。取值范围：[0, Page_Height-1]
     * @param color  矩形块颜色，取值范围：{0, 1}。当 Color为 1时，矩形块为黑色。当 Color为 0时，矩形块为白色
     * @return
     *//*

    public boolean printRectFill(int left, int top, int right, int bottom, COLOR color) {
        return printer.jpl.graphic.rectFill(left, top, right, bottom, color);
    }

    */
/**
     * 打印条形码（指定起点坐标）
     *
     * @param x          条码左上角 x坐标值，取值范围：[0, Page_Width-1]
     * @param y          条码左上角 y坐标值，取值范围：[0, Page_Height-1]
     * @param bar_height 定义条码高度
     * @param unit_width 定义条码码宽。取值范围：[1, 4]
     * @param rotate     条码旋转角度。取值范围：[0, 3]
     * @param text       以 0x00结尾的文本字符数据流
     * @return
     *//*

    public boolean printCode128(int x, int y, int bar_height, BAR_UNIT unit_width, BAR_ROTATE rotate, String text) {
        return printer.jpl.barcode.code128(x, y, bar_height, unit_width, rotate, text);
    }

    */
/**
     * 打印条形码（指定水平对齐方式）
     *
     * @param align      水平对齐方式
     * @param y          条码左上角 y坐标值，取值范围：[0, Page_Height-1]
     * @param bar_height 定义条码高度
     * @param unit_width 定义条码码宽。取值范围：[1, 4]
     * @param rotate     条码旋转角度。取值范围：[0, 3]
     * @param text       以 0x00结尾的文本字符数据流
     * @return
     *//*

    public boolean printCode128(ALIGN align, int y, int bar_height, BAR_UNIT unit_width, BAR_ROTATE rotate, String text) {
        return printer.jpl.barcode.code128(align, y, bar_height, unit_width, rotate, text);
    }

    */
/**
     * @param x          QRCode码左上角 x坐标值，取值范围：[0，Page_Width-1]
     * @param y          QRCode码左上角 y坐标值，取值范围：[0, Page_Height-1]
     * @param version    指定字符版本。取值范围：[0,20]。当 version为 0时，打印机根据字符串长度自动计算版本号
     * @param ecc        指定纠错等级。取值范围：[1, 4]
     * @param unit_width QRCode码码块，取值范围：[1, 4]。各值定义与一维条码指令输入参数 UniWidth相同
     * @param rotate     QRCode码旋转角度，取值范围： [0, 3]。各值定义与一维条码指令输入参数Rotate相同
     * @param text       以 0x00终止的 QRCode文本字符数据流
     * @return
     *//*

    public boolean printQRCode(int x, int y, int version, QRCODE_ECC ecc, BAR_UNIT unit_width, ROTATE rotate, String text) {
        return printer.jpl.barcode.QRCode(x, y, version, ecc, unit_width, rotate, text);
    }

    */
/**
     * @param x      位图左上角 x坐标值，取值范围：[0, Page_Width]
     * @param y      位图左上角 y坐标值，取值范围：[0, Page_Height]
     * @param width  位图的像素宽度
     * @param height 位图的像素高度
     * @param data   位图的点阵数据
     * @return
     *//*

    public boolean printBitmap(int x, int y, int width, int height, char[] data) {
        return printer.jpl.image.drawOut(x, y, width, height, data);
    }

    */
/**
     * @param x        位图左上角 x坐标值，取值范围：[0, Page_Width]
     * @param y        位图左上角 y坐标值，取值范围：[0, Page_Height]
     * @param width    位图的像素宽度
     * @param height   位图的像素高度
     * @param data     位图的点阵数据
     * @param Reverse  是否反白打印
     * @param Rotate   旋转标识
     * @param EnlargeX 位图宽度放大倍数
     * @param EnlargeY 位图高度放大倍数
     * @return
     *//*

    public boolean printBitmap(int x, int y, int width, int height, byte[] data, boolean Reverse, IMAGE_ROTATE Rotate, int EnlargeX, int EnlargeY) {
        return printer.jpl.image.drawOut(x, y, width, height, data, Reverse, Rotate, EnlargeX, EnlargeY);
    }

    */
/**
     * @param x      位图左上角 x坐标值，取值范围：[0, Page_Width]
     * @param y      位图左上角 y坐标值，取值范围：[0, Page_Height]
     * @param res    通过getResources()获取
     * @param id     资源文件ID
     * @param rotate 旋转标识
     * @return
     *//*

    public boolean printBitmap(int x, int y, Resources res, int id, IMAGE_ROTATE rotate) {
        return printer.jpl.image.drawOut(x, y, res, id, rotate);
    }
}
*/
