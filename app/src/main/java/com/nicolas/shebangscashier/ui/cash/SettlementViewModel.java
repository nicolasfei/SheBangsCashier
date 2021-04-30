package com.nicolas.shebangscashier.ui.cash;

import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.nicolas.shebangscashier.R;
import com.nicolas.shebangscashier.app.MyApp;
import com.nicolas.shebangscashier.cashier.MyKeeper;
import com.nicolas.shebangscashier.common.OperateError;
import com.nicolas.shebangscashier.common.OperateInUserView;
import com.nicolas.shebangscashier.common.OperateResult;
import com.nicolas.shebangscashier.communication.CommandResponse;
import com.nicolas.shebangscashier.communication.CommandTypeEnum;
import com.nicolas.shebangscashier.communication.CommandVo;
import com.nicolas.shebangscashier.communication.Invoker;
import com.nicolas.shebangscashier.communication.sale.SaleInterface;
import com.nicolas.shebangscashier.communication.vip.VipInterface;
import com.nicolas.shebangscashier.data.vip.Vip;
import com.nicolas.shebangscashier.ui.cash.data.SaleGoodsInformation;
import com.nicolas.shebangscashier.ui.cash.data.SettlementGoodsMsg;
import com.nicolas.shebangscashier.ui.cash.data.SettlementGroupMsg;
import com.nicolas.toollibrary.HttpHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SettlementViewModel extends ViewModel {

    private ArrayList<SaleGoodsInformation> goodsInformationList;            //销售数据
    private ArrayList<SettlementGroupMsg> settlementGroup;          //结算数据
    private Vip vip;                                                //vip信息
    private String payType;                                         //支付方式
    private int useIntegral;                                        //消费积分
    private int useIntegralDeduction;                               //消费积分可抵扣现金
    private float actualPayment;                                    //实付金额
    private float change;                                           //找零金额
    private float saleTotal;                                        //销售总金额
    private String receiptCode;                                     //销售条码
    private MutableLiveData<OperateResult> settlementChangeResult;    //结算数据转换
    private MutableLiveData<OperateResult> vipOperateResult;          //会员查询结果
    private MutableLiveData<OperateResult> settlementResult;          //结算结果
    private MutableLiveData<OperateResult> exchangeIntegralResult;    //积分兑换结果
    private MutableLiveData<OperateResult> actualPaymentResult;       //实付结果

    private boolean isSetActualPayment = false;

    private String currentSaleVipTel;           //当前售卖对象的手机号码

    public SettlementViewModel() {
        vipOperateResult = new MutableLiveData<>();
        settlementResult = new MutableLiveData<>();
        settlementChangeResult = new MutableLiveData<>();
        exchangeIntegralResult = new MutableLiveData<>();
        actualPaymentResult = new MutableLiveData<>();
        payType = MyKeeper.getInstance().getPayment()[2];       //默认现金支付
    }

    public LiveData<OperateResult> getSettlementResult() {
        return settlementResult;
    }

    public LiveData<OperateResult> getVipOperateResult() {
        return vipOperateResult;
    }

    public LiveData<OperateResult> getSettlementChangeResult() {
        return settlementChangeResult;
    }

    public LiveData<OperateResult> getActualPaymentResult() {
        return actualPaymentResult;
    }

    public LiveData<OperateResult> getExchangeIntegralResult() {
        return exchangeIntegralResult;
    }

    public String getSaleVoucher() {
        return goodsInformationList.get(0).id;
    }

    /**
     * 获取找零金额
     *
     * @return 找零金额
     */
    public float getChange() {
        return change;
    }

    /**
     * 获取实付金额
     *
     * @return 实付金额
     */
    public float getActualPayment() {
        return actualPayment;
    }

    /**
     * 是否已经设置了实付金额
     *
     * @return yes/no
     */
    public boolean isSetActualPayment() {
        return isSetActualPayment;
    }

    /**
     * 设置实付金额
     *
     * @param actualPayment 实付金额
     */
    public void setActualPayment(float actualPayment) {
        if ((actualPayment + (float) (this.useIntegral / 100 * 5)) < this.saleTotal) {
            actualPaymentResult.setValue(new OperateResult(new OperateError(-1, (MyApp.getInstance().getString(R.string.settlement_actualPayment_error)), null)));
        } else {
            this.actualPayment = actualPayment;
            this.change = this.actualPayment + this.useIntegralDeduction - this.saleTotal;
            isSetActualPayment = true;
            actualPaymentResult.setValue(new OperateResult(new OperateInUserView(null)));
        }
    }

    /**
     * 设置消费积分---在服务器端查询
     *
     * @param useIntegral 消费积分
     */
    public void setUseIntegral(int useIntegral) {
        if (this.vip.integral < useIntegral) {
            exchangeIntegralResult.setValue(new OperateResult(new OperateError(-1, MyApp.getInstance().getString(R.string.settlement_exchange_error), null)));
        } else {
            this.useIntegral = useIntegral;
            this.useIntegralDeduction = this.useIntegral / 100 * 5;
            exchangeIntegralResult.setValue(new OperateResult(new OperateInUserView(null)));
        }
    }

    /**
     * 获取消费积分
     *
     * @return 消费积分
     */
    public int getUseIntegral() {
        return useIntegral;
    }

    /**
     * 获取消费积分可抵扣现金
     *
     * @return 消费积分可抵扣现金
     */
    public int getUseIntegralDeduction() {
        return useIntegralDeduction;
    }

    /**
     * 设置支付方式
     *
     * @param payType 支付方式
     */
    public void setPayType(String payType) {
        this.payType = payType;
    }

    /**
     * 获取vip信息
     *
     * @return vip信息
     */
    public Vip getVip() {
        return vip;
    }

    /**
     * 获取转换后的结算数据
     *
     * @return 转换后的结算数据
     */
    public ArrayList<SettlementGroupMsg> getSettlementGroup() {
        return settlementGroup;
    }

    /**
     * 将销售商品信息转换为结算信息
     *
     * @param goodsInformationList 销售商品信息
     */
    public void transformSaleGoodsInformation(ArrayList<SaleGoodsInformation> goodsInformationList) {
        //保持销售数据
        this.goodsInformationList = goodsInformationList;
        //转换成结算数据
        ArrayList<SettlementGoodsMsg> goodsMsgs = new ArrayList<>();
        SettlementGoodsMsg head = new SettlementGoodsMsg("品名", "数量", "单价");
        int numCount = 0;
        float priceCount = 0;
        for (SaleGoodsInformation goods : goodsInformationList) {
            SettlementGoodsMsg settleGoods = new SettlementGoodsMsg(goods.goodsClassName + "(" + goods.id + ")",
                    String.valueOf(goods.amount), new DecimalFormat("###.00").format(goods.price));
            numCount += goods.amount;
            priceCount += goods.amount * goods.price;
            goodsMsgs.add(settleGoods);
        }
        SettlementGoodsMsg total = new SettlementGoodsMsg("合计", String.valueOf(numCount), new DecimalFormat("###.00").format(priceCount));
        saleTotal = priceCount;
        settlementGroup = new ArrayList<>();
        SettlementGroupMsg groupMsg = new SettlementGroupMsg(head, total, goodsMsgs);
        settlementGroup.add(groupMsg);
        settlementChangeResult.setValue(new OperateResult(new OperateInUserView(null)));
    }

    /**
     * 获取应付金额
     *
     * @return 应付金额
     */
    public float getSaleTotal() {
        return saleTotal;
    }

    /**
     * 通过手机号码，查询会员信息
     *
     * @param tel 电话号码
     */
    public void queryVipInformation(String tel) {
        this.currentSaleVipTel = tel;
        CommandVo vo = new CommandVo();
        vo.typeEnum = CommandTypeEnum.COMMAND_VIP;
        vo.url = VipInterface.VipInformationQuery;
        vo.contentType = HttpHandler.ContentType_APP;
        vo.requestMode = HttpHandler.RequestMode_POST;
        Map<String, String> parameters = new HashMap<>();
        parameters.put("tel", tel);
        vo.parameters = parameters;
        Invoker.getInstance().setOnEchoResultCallback(this.callback);
        Invoker.getInstance().exec(vo);
    }

    /**
     * 收银结算提交
     */
    public void submitSale() {
        CommandVo vo = new CommandVo();
        vo.typeEnum = CommandTypeEnum.COMMAND_SALE;
        vo.url = SaleInterface.SaleGoodsSale;
        vo.contentType = HttpHandler.ContentType_JSON;
        vo.requestMode = HttpHandler.RequestMode_POST;
        Map<String, String> parameters = new HashMap<>();
        try {
            JSONObject object = new JSONObject();
            object.put("employeesId", MyKeeper.getInstance().getStaff().id);
            object.put("pay", this.payType);
            if (this.vip != null) {
                object.put("tel", this.vip.tel);
                object.put("integral", useIntegral);
            } else {
                object.put("tel", "");
                object.put("integral", 0);
            }
            JSONArray jsonArray = new JSONArray();
            for (SaleGoodsInformation goods : goodsInformationList) {
                JSONObject item = new JSONObject();
                item.putOpt("id", goods.id);
                item.putOpt("goodsClass", goods.goodsClassId);
                item.putOpt("amount", goods.amount);
                item.putOpt("price", goods.price);
                jsonArray.put(item);
            }
            object.put("list", jsonArray);
            parameters.put("myJson", object.toString());
            vo.parameters = parameters;
            Invoker.getInstance().setOnEchoResultCallback(this.callback);
            Invoker.getInstance().exec(vo);
        } catch (JSONException e) {
            e.printStackTrace();
            settlementResult.setValue(new OperateResult(new OperateError(-1, MyApp.getInstance().getString(R.string.build_json_error), null)));
        }
    }

    /**
     * 查询结果返回接口
     */
    private Invoker.OnExecResultCallback callback = new Invoker.OnExecResultCallback() {
        @Override
        public void execResult(CommandResponse result) {
            switch (result.url) {
                case VipInterface.VipInformationQuery:        //获取会员信息
                    if (result.success) {
                        vip = new Vip(result.data);
                        if (TextUtils.isEmpty(vip.tel)) {
                            vip.tel = currentSaleVipTel;
                            Message msg = new Message();
                            msg.obj = "此为非会员用户，本次销售完成后自动升级为会员";
                            vipOperateResult.setValue(new OperateResult(new OperateInUserView(msg)));
                        } else {
                            vipOperateResult.setValue(new OperateResult(new OperateInUserView(null)));
                        }
                    } else {
                        vipOperateResult.setValue(new OperateResult(new OperateError(result.code, result.msg, null)));
                    }
                    break;
                case SaleInterface.SaleGoodsSale:            //提交销售数据
                    if (result.success) {
                        settlementResult.setValue(new OperateResult(new OperateInUserView(null)));
                    } else {
                        settlementResult.setValue(new OperateResult(new OperateError(result.code, result.msg, null)));
                    }
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 获取销售提交数据Bundle
     *
     * @return Bundle
     */
    public Bundle getSubmitSaleData() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("vip", vip);
        bundle.putString("payType", payType);
        bundle.putInt("useIntegral", useIntegral);
        bundle.putFloat("actualPayment", actualPayment);
        bundle.putFloat("change", change);
        bundle.putFloat("saleTotal", saleTotal);
        bundle.putString("receiptCode", receiptCode);
        bundle.putParcelableArrayList("sale", goodsInformationList);
        return bundle;
    }

    /**
     * 获取支付方式
     *
     * @return 支付方式
     */
    public String getPayType() {
        return payType;
    }
}
