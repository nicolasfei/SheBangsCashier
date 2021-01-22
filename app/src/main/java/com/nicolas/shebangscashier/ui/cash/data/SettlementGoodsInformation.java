package com.nicolas.shebangscashier.ui.cash.data;

import com.nicolas.shebangscashier.data.vip.Vip;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class SettlementGoodsInformation {
    public ArrayList<SaleGoodsInformation> settlementList;         //结算数据
    public Vip vip;                                                //vip信息
    public String payType;                                         //支付方式
    public int useIntegral;                                        //消费积分
    public float actualPayment;                                    //实付金额
    public float change;                                           //找零金额
    public float saleTotal;                                        //销售总金额
    public String receiptCode;

    public SettlementGoodsInformation(ArrayList<SaleGoodsInformation> settlementList, Vip vip, String payType, int useIntegral,
                                      float actualPayment, float change, float saleTotal, String receiptCode) {
        this.settlementList = settlementList;
        this.vip = vip;
        this.payType = payType;
        this.useIntegral = useIntegral;
        this.actualPayment = actualPayment;
        this.change = change;
        this.saleTotal = saleTotal;
        this.receiptCode = receiptCode;
    }

    /**
     * 获取商品退货期限--7天时间
     *
     * @return 退货期限
     */
    public String getGoodsBackTerm() {
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) + 7);
        return calendar.get(Calendar.YEAR) + "年" + (calendar.get(Calendar.MONTH) + 1) + "月" + calendar.get(Calendar.DAY_OF_MONTH) + "日";
    }
}
