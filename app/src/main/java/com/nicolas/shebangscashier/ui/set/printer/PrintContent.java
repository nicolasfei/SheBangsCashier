package com.nicolas.shebangscashier.ui.set.printer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.nicolas.shebangscashier.R;
import com.nicolas.shebangscashier.app.MyApp;
import com.nicolas.shebangscashier.cashier.MyKeeper;
import com.nicolas.shebangscashier.common.BarCodeInformation;
import com.nicolas.shebangscashier.common.GoodsInformation;
import com.nicolas.shebangscashier.common.ReturnGoodsInformation;
import com.nicolas.shebangscashier.common.StoreReceipt;
import com.nicolas.shebangscashier.ui.cash.data.SaleGoodsInformation;
import com.nicolas.shebangscashier.ui.cash.data.SettlementGoodsInformation;
import com.printer.command.CpclCommand;
import com.printer.command.EscCommand;
import com.printer.command.LabelCommand;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;

public class PrintContent {
    public static final short PRINT_POSITION_1 = 6;
    public static final short PRINT_POSITION_1_C = 7;
    public static final short PRINT_POSITION_2 = 10;
    // 将unit设置为这个单位值，其实际距离大约是一个字的1/3
    public static final short PRINT_UNIT = 7;

    private static final int tabL = 6;      //制表符所占字节长度


    public static Map<String, Object> getBackReceipt(Context context, List<ReturnGoodsInformation> informations) {
        Map<String, Object> map = new HashMap<>();
        int i = 0;
        /**
         * 打印标题
         */
        map.put("title", MyKeeper.getInstance().getBranch().xsName);
        map.put("text" + i++, "\t\t\t" + context.getString(R.string.receipt_subtitle));
        /**
         * 小票信息
         */
        // 打印文字
        map.put("text" + i++, context.getString(R.string.complaintTel) + MyKeeper.getInstance().getBranch().complaintTel + "\n");   //投诉电话
        map.put("text" + i++, context.getString(R.string.receipt_print_time) +
                new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA).format(new Date()) + "\n");//打印时间
        map.put("text" + i++, context.getString(R.string.receipt_print_division));         //分割线
        //打印并换行

        /**
         * 商品信息
         */
        int numCount=0;
        float salePriceTotal=0;
        String goodsT;
        String tmp = context.getString(R.string.receipt_goods_name);       //品名
        goodsT = tmp + (tmp.getBytes().length < tabL ? "\t\t\t" : tmp.getBytes().length < tabL * 2 ? "\t\t" : "\t");
        tmp = context.getString(R.string.receipt_goods_num);               //数量
        goodsT += tmp + (tmp.getBytes().length < tabL ? "\t\t\t" : tmp.getBytes().length < tabL * 2 ? "\t\t" : "\t");
        goodsT += context.getString(R.string.receipt_goods_price);         //单价
        map.put("text" + i++, goodsT);
        for (ReturnGoodsInformation msg : informations) {
            String goodsV;
            String tm = msg.goodsClassName;       //品名
            goodsV = tm + (tm.getBytes().length < tabL ? "\t\t\t" : tm.getBytes().length < tabL * 2 ? "\t\t" : "\t");
            tm = String.valueOf(msg.amount);      //数量
            goodsV += (tm + (tm.getBytes().length < 4 ? "\t\t\t" : tm.getBytes().length < 4 * 2 ? "\t\t" : "\t"));
            goodsV += "-"+msg.totalPrice;          //总价
            map.put("text" + i++, goodsV);

            numCount+=msg.amount;
            salePriceTotal+=msg.totalPrice;
        }

        map.put("text" + i++, context.getString(R.string.receipt_print_division));                    //分割线
        //打印并换行
        map.put("text" + i++, (context.getString(R.string.receipt_num_total) + numCount) + "\n");   //合计数量
        map.put("text" + i++, (context.getString(R.string.receipt_price_total) + "-"+MyApp.getInstance().getString(R.string.money) + salePriceTotal) + "\n");             //合计金额
        map.put("text" + i++, context.getString(R.string.receipt_print_division));                    //分割线
        //打印并换行

        /**
         * 打印二维码图片
         */
        Bitmap b = BitmapFactory.decodeResource(MyApp.getInstance().getResources(), R.mipmap.ewm);
        map.put("bitmap", b);
        //打印并走纸换行

        /**
         * 打印店铺地址
         */
        map.put("text" + i++, context.getString(R.string.receipt_address) + MyKeeper.getInstance().getBranch().address);
        return map;
    }

    public static Map<String, Object> getSaleReceipt(Context context, List<GoodsInformation> informations) {
        Map<String, Object> map = new HashMap<>();
        int i = 0;
        /**
         * 打印标题
         */
        map.put("title", MyKeeper.getInstance().getBranch().xsName);
        map.put("text" + i++, "\t\t\t" + context.getString(R.string.receipt_subtitle));
        /**
         * 小票信息
         */
        // 打印文字
        map.put("text" + i++, context.getString(R.string.receipt_code) + informations.get(0).receiptId + "\n");   //小票编号
        map.put("text" + i++, context.getString(R.string.complaintTel) + MyKeeper.getInstance().getBranch().complaintTel + "\n");   //投诉电话
        map.put("text" + i++, context.getString(R.string.receipt_print_time) + new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA).format(new Date()) + "\n");//打印时间
        map.put("text" + i++, context.getString(R.string.receipt_print_division));         //分割线
        //打印并换行

        /**
         * 商品信息
         */
        int numCount=0;
        float salePriceTotal=0;
        String goodsT;
        String tmp = context.getString(R.string.receipt_goods_name);       //品名
        goodsT = tmp + (tmp.getBytes().length < tabL ? "\t\t\t" : tmp.getBytes().length < tabL * 2 ? "\t\t" : "\t");
        tmp = context.getString(R.string.receipt_goods_num);               //数量
        goodsT += tmp + (tmp.getBytes().length < tabL ? "\t\t\t" : tmp.getBytes().length < tabL * 2 ? "\t\t" : "\t");
        goodsT += context.getString(R.string.receipt_goods_price);         //单价
        map.put("text" + i++, goodsT);
        for (GoodsInformation msg : informations) {
            String goodsV;
            String tm = msg.goodsClassName;       //品名
            goodsV = tm + (tm.getBytes().length < tabL ? "\t\t\t" : tm.getBytes().length < tabL * 2 ? "\t\t" : "\t");
            tm = "x" + msg.saleNum;               //数量
            goodsV += (tm + (tm.getBytes().length < 4 ? "\t\t\t" : tm.getBytes().length < 4 * 2 ? "\t\t" : "\t"));
            goodsV += msg.salePrice;              //单价
            map.put("text" + i++, goodsV);

            numCount+=msg.saleNum;
            salePriceTotal+=msg.salePrice;
        }

        map.put("text" + i++, context.getString(R.string.receipt_print_division));                    //分割线
        //打印并换行
        map.put("text" + i++, (context.getString(R.string.receipt_num_total) + numCount) + "\n");   //合计数量
        map.put("text" + i++, (context.getString(R.string.receipt_price_total) + MyApp.getInstance().getString(R.string.money) + salePriceTotal) + "\n");             //合计金额
        map.put("text" + i++, context.getString(R.string.receipt_print_division));                    //分割线
        //打印并换行
        /**
         * 打印注意
         */
        map.put("text" + i++, context.getString(R.string.receipt_warning) + "\n");                                  //注意
        map.put("text" + i++, "  " + context.getString(R.string.receipt_back_term) + "\n");                         //退货期限
        map.put("text" + i++, "  " + MyKeeper.getInstance().getGoodsBackTerm(informations.get(0).saleTime) + "\n"); //退货期限
        map.put("text" + i++, "  " + context.getString(R.string.receipt_back_condition) + "\n");  //退货条件
        map.put("text" + i++, "    " + context.getString(R.string.receipt_back_condition1) + "\n");         //条件1
        map.put("text" + i++, "    " + context.getString(R.string.receipt_back_condition2) + "\n");         //条件2
        map.put("text" + i++, "    " + context.getString(R.string.receipt_back_condition3) + "\n");         //条件3
        map.put("text" + i++, "" + context.getString(R.string.receipt_back_condition4) + "\n");         //条件4
        //打印并换行

        /**
         * 打印二维码图片
         */
        Bitmap b = BitmapFactory.decodeResource(MyApp.getInstance().getResources(), R.mipmap.ewm);
        map.put("bitmap", b);
        //打印并走纸换行

        /**
         * 打印店铺地址
         */
        map.put("text" + i++, context.getString(R.string.receipt_address) + MyKeeper.getInstance().getBranch().address);
        return map;
    }


    public static Map<String, Object> getSaleReceipt(Context context, SettlementGoodsInformation information) {
        Map<String, Object> map = new HashMap<>();
        int i = 0;
        /**
         * 打印标题
         */
        map.put("title", MyKeeper.getInstance().getBranch().xsName);
        map.put("text" + i++, "\t\t\t" + context.getString(R.string.receipt_subtitle));
        /**
         * 小票信息
         */
        // 打印文字
        map.put("text" + i++, context.getString(R.string.receipt_code) + information.receiptCode + "\n");                              //小票编号
        map.put("text" + i++, context.getString(R.string.complaintTel) + MyKeeper.getInstance().getBranch().complaintTel + "\n");   //投诉电话
        map.put("text" + i++, context.getString(R.string.receipt_print_time) + new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA).format(new Date()) + "\n");//打印时间
        map.put("text" + i++, context.getString(R.string.receipt_print_division));         //分割线
        //打印并换行

        /**
         * 商品信息
         */
        String goodsT;
        String tmp = context.getString(R.string.receipt_goods_name);       //品名
        goodsT = tmp + (tmp.getBytes().length < tabL ? "\t\t\t" : tmp.getBytes().length < tabL * 2 ? "\t\t" : "\t");
        tmp = context.getString(R.string.receipt_goods_num);               //数量
        goodsT += tmp + (tmp.getBytes().length < tabL ? "\t\t\t" : tmp.getBytes().length < tabL * 2 ? "\t\t" : "\t");
        goodsT += context.getString(R.string.receipt_goods_price);         //单价
        map.put("text" + i++, goodsT);
        for (SaleGoodsInformation msg : information.settlementList) {
            String goodsV;
            String tm = msg.goodsClassName;       //品名
            goodsV = tm + (tm.getBytes().length < tabL ? "\t\t\t" : tm.getBytes().length < tabL * 2 ? "\t\t" : "\t");
            tm = "x" + msg.amount;               //数量
            goodsV += (tm + (tm.getBytes().length < 4 ? "\t\t\t" : tm.getBytes().length < 4 * 2 ? "\t\t" : "\t"));
            goodsV += msg.price;                  //单价
            map.put("text" + i++, goodsV);
        }

        map.put("text" + i++, context.getString(R.string.receipt_print_division));                    //分割线
        //打印并换行
        map.put("text" + i++, (context.getString(R.string.receipt_num_total) + information.settlementList.size()) + "\n");   //合计数量
        map.put("text" + i++, (context.getString(R.string.receipt_price_total) + MyApp.getInstance().getString(R.string.money) + information.saleTotal) + "\n");             //合计金额
        map.put("text" + i++, (context.getString(R.string.receipt_Collection) + MyApp.getInstance().getString(R.string.money) + information.actualPayment) + "\n");          //收款
        map.put("text" + i++, (context.getString(R.string.receipt_give_change) + MyApp.getInstance().getString(R.string.money) + information.change) + "\n");                //找零
        map.put("text" + i++, context.getString(R.string.receipt_print_division));                    //分割线
        //打印并换行
        /**
         * 会员信息
         */
        if (information.vip != null) {
            map.put("text" + i++, (context.getString(R.string.receipt_vip_tel) + information.vip.tel) + "\n");                              //会员手机号码
            map.put("text" + i++, (context.getString(R.string.receipt_sale_integral) + (int) information.saleTotal) + "\n");                //本次积分
            map.put("text" + i++, (context.getString(R.string.receipt_consume_integral) + information.useIntegral) + "\n");                 //消费积分
            map.put("text" + i++, (context.getString(R.string.receipt_offset_cash) + information.useIntegral / 100 * 5) + "\n");            //抵扣现金
            map.put("text" + i++, (context.getString(R.string.receipt_surplus_integral) + (information.vip.integral - information.useIntegral)) + "\n");    //剩余积分
        } else {
            map.put("text" + i++, context.getString(R.string.receipt_vip_tel) + "\n");              //会员手机号码
            map.put("text" + i++, context.getString(R.string.receipt_sale_integral) + "\n");        //本次积分
            map.put("text" + i++, context.getString(R.string.receipt_consume_integral) + "\n");     //消费积分
            map.put("text" + i++, context.getString(R.string.receipt_offset_cash) + "\n");          //抵扣现金
            map.put("text" + i++, context.getString(R.string.receipt_surplus_integral) + "\n");     //剩余积分
        }
        map.put("line", context.getString(R.string.receipt_print_division));                        //分割线
        //打印并换行
        /**
         * 打印注意
         */
        map.put("text" + i++, context.getString(R.string.receipt_warning) + "\n");                                  //注意
        map.put("text" + i++, "  " + context.getString(R.string.receipt_back_term) + "\n");                         //退货期限
        map.put("text" + i++, "  " + MyKeeper.getInstance().getGoodsBackTerm(new SimpleDateFormat("yyyy-MM-dd",Locale.CHINA).format(new Date()))+ "\n");                                //退货期限
        map.put("text" + i++, "  " + context.getString(R.string.receipt_back_condition) + "\n");            //退货条件
        map.put("text" + i++, "    " + context.getString(R.string.receipt_back_condition1) + "\n");         //条件1
        map.put("text" + i++, "    " + context.getString(R.string.receipt_back_condition2) + "\n");         //条件2
        map.put("text" + i++, "    " + context.getString(R.string.receipt_back_condition3) + "\n");         //条件3
        map.put("text" + i++, "" + context.getString(R.string.receipt_back_condition4) + "\n");         //条件4
        //打印并换行

        /**
         * 打印二维码图片
         */
        Bitmap b = BitmapFactory.decodeResource(MyApp.getInstance().getResources(), R.mipmap.ewm);
        map.put("bitmap", b);
        //打印并走纸换行

        /**
         * 打印店铺地址
         */
        map.put("text" + i++, context.getString(R.string.receipt_address) + MyKeeper.getInstance().getBranch().address);
        return map;
    }

    /**
     * 收银小票
     */
    public static Vector<Byte> getSaleReceipt(SettlementGoodsInformation information) {
        MyApp context = MyApp.getInstance();
        EscCommand esc = new EscCommand();
        //初始化打印机
        esc.addInitializePrinter();
        //打印走纸多少个单位
        esc.addPrintAndFeedLines((byte) 3);
        /**
         * 打印标题
         */
        // 设置打印居中
        esc.addSelectJustification(EscCommand.JUSTIFICATION.CENTER);
        // 设置为倍高倍宽
        esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF, EscCommand.ENABLE.ON, EscCommand.ENABLE.ON, EscCommand.ENABLE.OFF);
        // 打印文字
        esc.addText(context.getString(R.string.receipt_title));         //砰然心动连锁服饰
        // 打印文字
        esc.addText(context.getString(R.string.receipt_subtitle));         //收银小票
        //打印并换行
        esc.addPrintAndLineFeed();
        // 取消倍高倍宽
        esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);

        /**
         * 小票信息
         */
        // 设置打印左对齐
        esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);
        // 打印文字
        esc.addText(context.getString(R.string.receipt_code) + information.receiptCode + "\n");                                       //小票编号
//         esc.addText(context.getString(R.string.receipt_complaints_hotline) + BranchKeeper.getInstance().information.dzTel + "\n");                  //投诉电话
//         esc.addText(context.getString(R.string.receipt_guider) + BranchKeeper.getInstance().onDutyGuide.guideName + "\n");              //导购员
        esc.addText(context.getString(R.string.receipt_print_time) + new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA).format(new Date()) + "\n");//打印时间
        esc.addText(context.getString(R.string.receipt_print_division));         //分割线
        //打印并换行
        esc.addPrintAndLineFeed();

        /**
         * 商品信息
         */
        esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);
        esc.addText(context.getString(R.string.receipt_goods_name));            //品名
        esc.addSetHorAndVerMotionUnits((byte) PRINT_UNIT, (byte) 0);
        esc.addSetAbsolutePrintPosition(PRINT_POSITION_1);
        esc.addText(context.getString(R.string.receipt_goods_num));             //数量
        esc.addSetAbsolutePrintPosition(PRINT_POSITION_2);
        esc.addText(context.getString(R.string.receipt_goods_price));           //单价
        esc.addPrintAndLineFeed();
        for (SaleGoodsInformation msg : information.settlementList) {
            esc.addText(msg.goodsClassName);            //品名
            esc.addSetHorAndVerMotionUnits((byte) PRINT_UNIT, (byte) 0);
            esc.addSetAbsolutePrintPosition(PRINT_POSITION_1);
            esc.addText(" x" + msg.amount);             //数量
            esc.addSetAbsolutePrintPosition(PRINT_POSITION_2);
            esc.addText(MyApp.getInstance().getString(R.string.price) + msg.price);    //单价
            //打印并换行
            esc.addPrintAndLineFeed();
        }

        esc.addText(context.getString(R.string.receipt_print_division));                    //分割线
        //打印并换行
        esc.addPrintAndLineFeed();
        esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);
        esc.addText((context.getString(R.string.receipt_num_total) + information.settlementList.size()) + "\n");   //合计数量
        esc.addText((context.getString(R.string.receipt_price_total) + MyApp.getInstance().getString(R.string.money) + information.saleTotal) + "\n");             //合计金额
        esc.addText((context.getString(R.string.receipt_Collection) + MyApp.getInstance().getString(R.string.money) + information.actualPayment) + "\n");          //收款
        esc.addText((context.getString(R.string.receipt_give_change) + MyApp.getInstance().getString(R.string.money) + information.change) + "\n");                //找零
        esc.addText(context.getString(R.string.receipt_print_division));                    //分割线
        //打印并换行
        esc.addPrintAndLineFeed();
        /**
         * 会员信息
         */
        if (information.vip != null) {
            esc.addText((context.getString(R.string.receipt_vip_tel) + information.vip.tel) + "\n");                              //会员手机号码
            esc.addText((context.getString(R.string.receipt_sale_integral) + (int) information.saleTotal) + "\n");                          //本次积分
            esc.addText((context.getString(R.string.receipt_consume_integral) + information.useIntegral) + "\n");                              //消费积分
            esc.addText((context.getString(R.string.receipt_offset_cash) + information.useIntegral / 100 * 5) + "\n");                    //抵扣现金
            esc.addText((context.getString(R.string.receipt_surplus_integral) + (information.vip.integral - information.useIntegral)) + "\n");    //剩余积分
        } else {
            esc.addText(context.getString(R.string.receipt_vip_tel) + "\n");    //会员手机号码
            esc.addText(context.getString(R.string.receipt_sale_integral) + "\n");     //本次积分
            esc.addText(context.getString(R.string.receipt_consume_integral) + "\n");     //消费积分
            esc.addText(context.getString(R.string.receipt_offset_cash) + "\n");  //抵扣现金
            esc.addText(context.getString(R.string.receipt_surplus_integral) + "\n");  //剩余积分
        }
        esc.addText(context.getString(R.string.receipt_print_division));                    //分割线
        //打印并换行
        esc.addPrintAndLineFeed();
        /**
         * 打印注意
         */
        esc.addText(context.getString(R.string.receipt_warning) + "\n");                                   //注意
        esc.addText("  " + context.getString(R.string.receipt_back_term) + information.getGoodsBackTerm() + "\n");//退货期限
        esc.addText("  " + context.getString(R.string.receipt_back_condition) + "\n");                            //退货条件
        esc.addText("    " + context.getString(R.string.receipt_back_condition1) + "\n");                           //条件1
        esc.addText("    " + context.getString(R.string.receipt_back_condition2) + "\n");                           //条件2
        esc.addText("    " + context.getString(R.string.receipt_back_condition3) + "\n");                           //条件3
        //打印并换行
        esc.addPrintAndLineFeed();

        /**
         * 打印二维码图片
         */
        esc.addSelectJustification(EscCommand.JUSTIFICATION.CENTER);
        Bitmap b = BitmapFactory.decodeResource(MyApp.getInstance().getResources(), R.mipmap.ewm);
        // 打印图片  光栅位图  384代表打印图片像素  0代表打印模式
        // 58mm打印机 可打印区域最大点数为 384 ，80mm 打印机 可打印区域最大点数为 576
        esc.addRastBitImage(b, 192, 0);
        esc.addPrintAndLineFeed();
        //打印并走纸换行
        esc.addPrintAndLineFeed();

        /**
         * 打印店铺地址
         */
        // 设置打印居中对齐
        esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);
        //打印fontB文字字体
        esc.addSelectCharacterFont(EscCommand.FONT.FONTB);
//         esc.addText(context.getString(R.string.receipt_address) + BranchKeeper.getInstance().information.address);
        //打印并换行
        esc.addPrintAndLineFeed();
        //打印走纸n个单位
        esc.addPrintAndFeedLines((byte) 4);
        // 开钱箱
        //esc.addGeneratePlus(LabelCommand.FOOT.F2, (byte) 255, (byte) 255);
        //开启切刀
        //esc.addCutPaper();
        //添加缓冲区打印完成查询
        byte[] bytes = {0x1D, 0x72, 0x01};
        //添加用户指令
        esc.addUserCommand(bytes);
        Vector<Byte> datas = esc.getCommand();
        return datas;
    }

    private static final int RowHeight = 20;         //行高
    private static final int RowStartOffset = 140;   //行起始偏移
    private static final int LabelWidth = 375;       //标签宽
    private static final int LabelHalfWidth = LabelWidth / 2;       //标签宽
    private static final int LabelHeight = 300;      //标签高
    private static final int wordWidth = 25;         //字宽度
    private static final int halfWordWidth = wordWidth / 2;         //半字宽度


    /**
     * 对其测试打印
     */
    public static Vector<Byte> getTableTest() {
        EscCommand esc = new EscCommand();
        //初始化打印机
        esc.addInitializePrinter();
        //打印走纸多少个单位
        esc.addPrintAndFeedLines((byte) 3);

        esc.addText("品名");            //品名

        esc.addSetHorAndVerMotionUnits((byte) PRINT_UNIT, (byte) 0);
        esc.addSetAbsolutePrintPosition((short) 6);
        esc.addText("数量");            //数量

        esc.addSetAbsolutePrintPosition((short) 10);
        esc.addText("单价");           //单价

        esc.addPrintAndLineFeed();
        //添加用户指令
        byte[] bytes = {0x1D, 0x72, 0x01};
        esc.addUserCommand(bytes);
        Vector<Byte> datas = esc.getCommand();
        return datas;
    }

    /**
     * 票据打印测试页
     *
     * @return
     */
    public static Vector<Byte> getReceipt() {
        EscCommand esc = new EscCommand();
        //初始化打印机
        esc.addInitializePrinter();
        //打印走纸多少个单位
        esc.addPrintAndFeedLines((byte) 3);
        // 设置打印居中
        esc.addSelectJustification(EscCommand.JUSTIFICATION.CENTER);
        // 设置为倍高倍宽
        esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF, EscCommand.ENABLE.ON, EscCommand.ENABLE.ON, EscCommand.ENABLE.OFF);
        // 打印文字
        esc.addText("票据测试\n");
        //打印并换行
        esc.addPrintAndLineFeed();
        // 取消倍高倍宽
        esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);
        // 设置打印左对齐
        esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);
        // 打印文字
        esc.addText("打印文字测试:\n");
        // 打印文字
        esc.addText("欢迎使用打印机!\n");
        esc.addPrintAndLineFeed();
        esc.addText("打印对齐方式测试:\n");
        // 设置打印左对齐
        esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);
        esc.addText("居左");
        esc.addPrintAndLineFeed();
        // 设置打印居中对齐
        esc.addSelectJustification(EscCommand.JUSTIFICATION.CENTER);
        esc.addText("居中");
        esc.addPrintAndLineFeed();
        // 设置打印居右对齐
        esc.addSelectJustification(EscCommand.JUSTIFICATION.RIGHT);
        esc.addText("居右");
        esc.addPrintAndLineFeed();
        esc.addPrintAndLineFeed();
        // 设置打印左对齐
        esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);
        esc.addText("打印Bitmap图测试:\n");
//        Bitmap b = BitmapFactory.decodeResource(SheBangsApp.getInstance().getResources(), R.drawable.gprinter);
//        // 打印图片  光栅位图  384代表打印图片像素  0代表打印模式
//        // 58mm打印机 可打印区域最大点数为 384 ，80mm 打印机 可打印区域最大点数为 576
//        esc.addRastBitImage(b, 384, 0);
//        esc.addPrintAndLineFeed();
        // 打印文字
        esc.addText("打印条码测试:\n");
        esc.addSelectPrintingPositionForHRICharacters(EscCommand.HRI_POSITION.BELOW);
        // 设置条码可识别字符位置在条码下方
        // 设置条码高度为60点
        esc.addSetBarcodeHeight((byte) 60);
        // 设置条码宽窄比为2
        esc.addSetBarcodeWidth((byte) 2);
        // 打印Code128码
        esc.addCODE128(esc.genCodeB("barcode128"));
        esc.addPrintAndLineFeed();
        /*
         * QRCode命令打印 此命令只在支持QRCode命令打印的机型才能使用。 在不支持二维码指令打印的机型上，则需要发送二维条码图片
         */
        esc.addText("打印二维码测试:\n");
        // 设置纠错等级
        esc.addSelectErrorCorrectionLevelForQRCode((byte) 0x31);
        // 设置qrcode模块大小
        esc.addSelectSizeOfModuleForQRCode((byte) 4);
        // 设置qrcode内容
        esc.addStoreQRCodeData("www.smarnet.cc");
        // 打印QRCode
        esc.addPrintQRCode();
        //打印并走纸换行
        esc.addPrintAndLineFeed();
        // 设置打印居中对齐
        esc.addSelectJustification(EscCommand.JUSTIFICATION.CENTER);
        //打印fontB文字字体
        esc.addSelectCharacterFont(EscCommand.FONT.FONTB);
        esc.addText("测试完成!\r\n");
        //打印并换行
        esc.addPrintAndLineFeed();
        //打印走纸n个单位
        esc.addPrintAndFeedLines((byte) 4);
        // 开钱箱
        esc.addGeneratePlus(LabelCommand.FOOT.F2, (byte) 255, (byte) 255);
        //开启切刀
        esc.addCutPaper();
        //添加缓冲区打印完成查询
        byte[] bytes = {0x1D, 0x72, 0x01};
        //添加用户指令
        esc.addUserCommand(bytes);
        Vector<Byte> datas = esc.getCommand();
        return datas;
    }

    /**
     * 标签打印测试页
     *
     * @return
     */
    public static Vector<Byte> getLabel(String label) {
        LabelCommand tsc = new LabelCommand();
        // 设置标签尺寸宽高，按照实际尺寸设置 单位mm
        tsc.addSize(50, 40);
        // 设置标签间隙，按照实际尺寸设置，如果为无间隙纸则设置为0 单位mm
        tsc.addGap(3);
        // 设置打印方向
        tsc.addDirection(LabelCommand.DIRECTION.FORWARD, LabelCommand.MIRROR.NORMAL);
        // 开启带Response的打印，用于连续打印
        tsc.addQueryPrinterStatus(LabelCommand.RESPONSE_MODE.ON);
        // 设置原点坐标
        tsc.addReference(0, 0);
        //设置浓度
        tsc.addDensity(LabelCommand.DENSITY.DNESITY4);
        // 撕纸模式开启
        tsc.addTear(EscCommand.ENABLE.ON);
        // 清除打印缓冲区
        tsc.addCls();
        // 绘制一维条码
        tsc.add1DBarcode(80, 50, LabelCommand.BARCODETYPE.CODE128, 100, LabelCommand.READABEL.EANBEL, LabelCommand.ROTATION.ROTATION_0, label);
        // 打印标签
        tsc.addPrint(1, 1);
        // 打印标签后 蜂鸣器响
        tsc.addSound(2, 100);
        //开启钱箱
//        tsc.addCashdrwer(LabelCommand.FOOT.F5, 255, 255);
        Vector<Byte> datas = tsc.getCommand();
        // 发送数据
        return datas;
    }

    /**
     * 标签打印长图
     *
     * @param bitmap
     * @return
     */
//    public static Vector<Byte> printViewPhoto(Bitmap bitmap) {
//        LabelCommand labelCommand = new LabelCommand();
//        /**
//         * 参数说明
//         * 0：打印图片x轴
//         * 0：打印图片Y轴
//         * 576：打印图片宽度  纸张可打印宽度  72 *8
//         * bitmap:图片
//         */
//        labelCommand.addZLibNoTrembleBitmapheight(0, 0, 576, bitmap);
//        return labelCommand.getCommand();
//    }

    /**
     * 面单打印测试页
     *
     * @return
     */
    public static Vector<Byte> getCPCL() {
        CpclCommand cpcl = new CpclCommand();
        cpcl.addInitializePrinter(1130, 1);
        cpcl.addJustification(CpclCommand.ALIGNMENT.CENTER);
        cpcl.addSetmag(1, 1);
        cpcl.addText(CpclCommand.TEXT_FONT.FONT_4, 0, 30, "Sample");
        cpcl.addSetmag(0, 0);
        cpcl.addJustification(CpclCommand.ALIGNMENT.LEFT);
        cpcl.addText(CpclCommand.TEXT_FONT.FONT_4, 0, 65, "Print text");
        cpcl.addText(CpclCommand.TEXT_FONT.FONT_4, 0, 95, "Welcom to use SMARNET printer!");
        cpcl.addText(CpclCommand.TEXT_FONT.FONT_13, 0, 135, "佳博智匯標籤打印機");
        cpcl.addText(CpclCommand.TEXT_FONT.FONT_4, 0, 195, "智汇");
        cpcl.addJustification(CpclCommand.ALIGNMENT.CENTER);
        cpcl.addText(CpclCommand.TEXT_FONT.FONT_4, 0, 195, "网络");
        cpcl.addJustification(CpclCommand.ALIGNMENT.RIGHT);
        cpcl.addText(CpclCommand.TEXT_FONT.FONT_4, 0, 195, "设备");
        cpcl.addJustification(CpclCommand.ALIGNMENT.LEFT);
        cpcl.addText(CpclCommand.TEXT_FONT.FONT_4, 0, 230, "Print bitmap!");
//        Bitmap bitmap = BitmapFactory.decodeResource(SheBangsApp.getInstance().getResources(), R.drawable.gprinter);
//        cpcl.addEGraphics(0, 255, 385, bitmap);
        cpcl.addText(CpclCommand.TEXT_FONT.FONT_4, 0, 645, "Print code128!");
        cpcl.addBarcodeText(5, 2);
        cpcl.addBarcode(CpclCommand.COMMAND.BARCODE, CpclCommand.CPCLBARCODETYPE.CODE128, 50, 0, 680, "SMARNET");
        cpcl.addText(CpclCommand.TEXT_FONT.FONT_4, 0, 775, "Print QRcode");
        cpcl.addBQrcode(0, 810, "QRcode");
        cpcl.addJustification(CpclCommand.ALIGNMENT.CENTER);
        cpcl.addText(CpclCommand.TEXT_FONT.FONT_4, 0, 1010, "Completed");
        cpcl.addJustification(CpclCommand.ALIGNMENT.LEFT);
        cpcl.addPrint();
        Vector<Byte> datas = cpcl.getCommand();
        return datas;
    }

    /**
     * 获取图片
     *
     * @param mcontext
     * @return
     */
    public static Bitmap getBitmap(Context mcontext) {
//        View v = View.inflate(SheBangsApp.getInstance(), R.layout.pj, null);
//        TableLayout tableLayout = (TableLayout) v.findViewById(R.id.li);
//        TextView jine = (TextView) v.findViewById(R.id.jine);
//        TextView pep = (TextView) v.findViewById(R.id.pep);
//        tableLayout.addView(ctv(mcontext, "红茶\n加热\n加糖", 8, 3));
//        tableLayout.addView(ctv(mcontext, "绿茶", 109, 899));
//        tableLayout.addView(ctv(mcontext, "咖啡", 15, 4));
//        tableLayout.addView(ctv(mcontext, "红茶", 8, 3));
//        tableLayout.addView(ctv(mcontext, "绿茶", 10, 8));
//        tableLayout.addView(ctv(mcontext, "咖啡", 15, 4));
//        tableLayout.addView(ctv(mcontext, "红茶", 8, 3));
//        tableLayout.addView(ctv(mcontext, "绿茶", 10, 8));
//        tableLayout.addView(ctv(mcontext, "咖啡", 15, 4));
//        tableLayout.addView(ctv(mcontext, "红茶", 8, 3));
//        tableLayout.addView(ctv(mcontext, "绿茶", 10, 8));
//        tableLayout.addView(ctv(mcontext, "咖啡", 15, 4));
//        tableLayout.addView(ctv(mcontext, "红茶", 8, 3));
//        tableLayout.addView(ctv(mcontext, "绿茶", 10, 8));
//        tableLayout.addView(ctv(mcontext, "咖啡", 15, 4));
//        jine.setText("998");
//        pep.setText("张三");
//        final Bitmap bitmap = convertViewToBitmap(v);
//        return bitmap;
        return null;
    }

    /**
     * mxl转bitmap图片
     *
     * @param view
     * @return
     */
    public static Bitmap convertViewToBitmap(View view) {
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();
        return bitmap;
    }

    public static TableRow ctv(Context context, String name, int k, int n) {
        TableRow tb = new TableRow(context);
        tb.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));
        TextView tv1 = new TextView(context);
        tv1.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
        tv1.setText(name);
        tv1.setTextColor(Color.BLACK);
        tv1.setTextSize(30);
        tb.addView(tv1);
        TextView tv2 = new TextView(context);
        tv2.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
        tv2.setText(k + "");
        tv2.setTextColor(Color.BLACK);
        tv2.setTextSize(30);
        tb.addView(tv2);
        TextView tv3 = new TextView(context);
        tv3.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
        tv3.setText(n + "");
        tv3.setTextColor(Color.BLACK);
        tv3.setTextSize(30);
        tb.addView(tv3);
        return tb;
    }

    public static Vector<Byte> getBarcodeLabel(BarCodeInformation info) {
        return null;
    }

    /**
     * 打印标签
     *
     * @param receipt 条码信息
     * @return Vector
     */
    public static List<Vector<Byte>> getGoodsLabel(StoreReceipt receipt) {
        List<Vector<Byte>> vectors = new ArrayList<>();
        for (StoreReceipt.PropertyRecord record : receipt.records) {
            LabelCommand tsc = new LabelCommand();
            /* 设置标签尺寸，按照实际尺寸设置 */
            tsc.addSize(LabelWidth, LabelHeight);
            /* 设置标签间隙，按照实际尺寸设置，如果为无间隙纸则设置为0 */
            tsc.addGap(10);
            /* 设置打印方向 */
            tsc.addDirection(LabelCommand.DIRECTION.FORWARD, LabelCommand.MIRROR.NORMAL);
            /* 开启带Response的打印，用于连续打印 */
//        tsc.addQueryPrinterStatus(LabelCommand.RESPONSE_MODE.ON);
            /* 撕纸模式开启 */
            tsc.addTear(EscCommand.ENABLE.ON);
            /* 清除打印缓冲区 */
            tsc.addCls();

            int contentStartY = 0;
            //---------------------------第一行-------------------------//
            /* 设置原点坐标 */
            tsc.addReference(0, 0);
            //订单条码
            tsc.add1DBarcode(RowStartOffset, RowHeight / 3, LabelCommand.BARCODETYPE.CODE93, 60,
                    LabelCommand.READABEL.EANBEL, LabelCommand.ROTATION.ROTATION_0, receipt.id);
            contentStartY += 100;
            //---------------------------第二行-------------------------//
            /* 设置原点坐标 */
            tsc.addReference(0, contentStartY);
            //商店信息
            tsc.addText(RowStartOffset, RowHeight, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE,
                    LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,
                    "怦然心动:9057 ZG-01229004");
            contentStartY += RowHeight + 15;
            //---------------------------第三行-------------------------//
            /* 设置原点坐标 */
            tsc.addReference(0, contentStartY);
            //商店信息
            tsc.addText(RowStartOffset, RowHeight, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE,
                    LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,
                    record.color + "/" + record.size + "码");
            contentStartY += RowHeight + 15;
            //---------------------------第四行-------------------------//
            /* 设置原点坐标 */
            tsc.addReference(0, contentStartY);
            //商店信息
            tsc.addText(RowStartOffset, RowHeight, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE,
                    LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,
                    "统一零售价：" + receipt.salePrice + "元");
//            contentStartY += 80;

            /* 打印标签 */
            tsc.addPrint(1);

            /* 打印标签后 蜂鸣器响 */
            tsc.addSound(2, 100);
            vectors.add(tsc.getCommand());
        }
        return vectors;
    }

    /**
     * 打印矩阵二维码
     *
     * @return
     */
//    public static Vector<Byte> getNewCommandToPrintQrcode() {
//        LabelCommand tsc = new LabelCommand();
//        // 设置标签尺寸，按照实际尺寸设置
//        tsc.addSize(80, 80);
//        // 设置标签间隙，按照实际尺寸设置，如果为无间隙纸则设置为0
//        tsc.addGap(0);
//        // 设置打印方向
//        tsc.addDirection(LabelCommand.DIRECTION.FORWARD, LabelCommand.MIRROR.NORMAL);
//        // 设置原点坐标
//        tsc.addReference(0, 0);
//        // 撕纸模式开启
//        tsc.addTear(EscCommand.ENABLE.ON);
//        // 清除打印缓冲区
//        tsc.addCls();
//        //添加矩阵打印二维码  旋转
//        /**
//         * 参数 说明  x横坐标打印起始点   y 纵坐标打印起始点   width  打印宽度 height 打印高度  ROTATION：旋转   content：内容
//         */
//        tsc.addDMATRIX(10, 10, 400, 400, LabelCommand.ROTATION.ROTATION_90, "DMATRIX EXAMPLE 1");
//        /**
//         * 参数 说明  x横坐标打印起始点   y 纵坐标打印起始点   width  打印宽度 height 打印高度  content：内容
//         */
//        tsc.addDMATRIX(110, 10, 200, 200, "DMATRIX EXAMPLE 1");
//        /**
//         * 参数 说明  x横坐标打印起始点   y 纵坐标打印起始点   width  打印宽度 height 打印高度  Xzoom：放大倍数   content：内容
//         */
//        tsc.addDMATRIX(210, 10, 400, 400, 6, "DMATRIX EXAMPLE 2");
//        /**
//         * 参数 说明  x横坐标打印起始点   y 纵坐标打印起始点   width  打印宽度 height 打印高度  c：ASCLL码  Xzomm：放大倍数 content：内容
//         */
//        tsc.addDMATRIX(10, 200, 100, 100, 126, 6, "~1010465011125193621Gsz9YC24xBbQD~12406404~191ffd0~192Ypg+oU9uLHdR9J5ms0UlqzSPEW7wYQbknUrwOehbz+s+a+Nfxk8JlwVhgItknQEZyfG4Al26Rs/Ncj60ubNCWg==");
//        tsc.addPrint(1, 1);
//        // 打印标签后 蜂鸣器响
//        tsc.addSound(2, 100);
//        Vector<Byte> datas = tsc.getCommand();
//        // 发送数据
//        return datas;
//    }
}
