package com.nicolas.shebangscashier.ui.cash;

import android.os.Bundle;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.nicolas.printerlibraryforufovo.PrinterDevice;
import com.nicolas.printerlibraryforufovo.PrinterManager;
import com.nicolas.shebangscashier.R;
import com.nicolas.shebangscashier.app.MyApp;
import com.nicolas.shebangscashier.common.OperateError;
import com.nicolas.shebangscashier.common.OperateInUserView;
import com.nicolas.shebangscashier.common.OperateResult;
import com.nicolas.shebangscashier.data.vip.Vip;
import com.nicolas.shebangscashier.ui.cash.data.SaleGoodsInformation;
import com.nicolas.shebangscashier.ui.cash.data.SettlementGoodsInformation;
import com.nicolas.shebangscashier.ui.set.printer.PrintContent;

import java.io.IOException;
import java.util.ArrayList;

public class SettlementDetailedViewModel extends ViewModel {

    public ArrayList<SaleGoodsInformation> goodsInformationList;       //销售数据
    public Vip vip;                                                //vip信息
    public String payType;                                         //支付方式
    public int useIntegral;                                        //消费积分
    public int saleIntegral;                                       //本次积分
    public int surplusIntegral;                                    //剩余积分
    public float deduction;                                        //消费积分可抵扣现金
    public float collection;                                       //实付金额
    public float change;                                           //找零金额
    public float receivable;                                       //应收金额
    public float totalCast;                                        //合计金额

    private boolean isSubmitSaleSuccess = false;                    //是否提交了销售数据
    private String receiptCode;
    private MutableLiveData<OperateResult> printSaleBillResult;       //打印小票结果

    public SettlementDetailedViewModel() {
        printSaleBillResult = new MutableLiveData<>();
    }

    public LiveData<OperateResult> getPrintSaleBillResult() {
        return printSaleBillResult;
    }

    /**
     * 设置销售数据
     */
    public void setSaleData(Bundle saleData) {
        vip = saleData.getParcelable("vip");
        useIntegral = saleData.getInt("useIntegral");           //消费积分
        saleIntegral = (int) saleData.getFloat("saleTotal");    //本次积分
        if (vip != null) {
            surplusIntegral = vip.integral - useIntegral;
        }
        receivable = saleData.getFloat("saleTotal");            //应收金额
        collection = saleData.getFloat("actualPayment");        //收款金额
        totalCast = saleData.getFloat("saleTotal");             //合计金额
        change = saleData.getFloat("change");                   //找零
        deduction = useIntegral / 100;                               //抵扣金额
        goodsInformationList = saleData.getParcelableArrayList("sale");
        vip = saleData.getParcelable("vip");
        payType = saleData.getString("payType");
        receiptCode = saleData.getString("receiptCode");        //小票编号
    }

    /**
     * 打印小票
     */
    public void printerSaleBill() {
        try {
            PrinterManager.getInstance().printBill(PrinterDevice.CONN_METHOD.BLUETOOTH, PrintContent.getSaleReceipt(new SettlementGoodsInformation(goodsInformationList, vip, payType, useIntegral, collection, change, totalCast, receiptCode)));
            printSaleBillResult.setValue(new OperateResult(new OperateInUserView(null)));
        } catch (IOException e) {
            e.printStackTrace();
            printSaleBillResult.setValue(new OperateResult(new OperateError(0, MyApp.getInstance().getString(R.string.printer_no_link), null)));
        }
    }
}
