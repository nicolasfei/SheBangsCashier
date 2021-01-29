package com.nicolas.shebangscashier.ui.cash;

import android.text.TextUtils;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.nicolas.shebangscashier.R;
import com.nicolas.shebangscashier.app.MyApp;
import com.nicolas.shebangscashier.common.InputFormState;
import com.nicolas.shebangscashier.common.OperateError;
import com.nicolas.shebangscashier.common.OperateInUserView;
import com.nicolas.shebangscashier.common.OperateResult;
import com.nicolas.shebangscashier.common.ReturnGoodsInformation;
import com.nicolas.shebangscashier.communication.CommandResponse;
import com.nicolas.shebangscashier.communication.CommandTypeEnum;
import com.nicolas.shebangscashier.communication.CommandVo;
import com.nicolas.shebangscashier.communication.Invoker;
import com.nicolas.shebangscashier.communication.sale.SaleInterface;
import com.nicolas.toollibrary.HttpHandler;
import com.nicolas.toollibrary.Tool;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReturnGoodsViewModel extends ViewModel {

    private ArrayList<ReturnGoodsInformation> returnGoods;                //待退货的商品list
    private MutableLiveData<InputFormState> inputFormState;             //输入格式是否正确
    private MutableLiveData<OperateResult> queryGoodsInformationResult;     //查询商品信息
    private MutableLiveData<OperateResult> returnGoodsResult;               //退货结果

    private String vipPhone;                //会员手机
    private int vipIntegral;                //会员积分
    private int deductIntegral;             //扣除积分
    private int integralBalance;            //积分差额=会员积分-总的扣除积分
    private float refundDeduction;          //退款减扣=积分差额所抵扣的钱

    private int numCount;       //数量合计
    private float refundCount;  //退款合计

    public ReturnGoodsViewModel() {
        this.returnGoods = new ArrayList<>();

        this.inputFormState = new MutableLiveData<>();
        this.queryGoodsInformationResult = new MutableLiveData<>();
        this.returnGoodsResult = new MutableLiveData<>();
    }

    public LiveData<OperateResult> getQueryGoodsInformationResult() {
        return queryGoodsInformationResult;
    }

    public LiveData<OperateResult> getReturnGoodsResult() {
        return returnGoodsResult;
    }

    /**
     * 获取返货信息
     *
     * @return 返货信息
     */
    public List<ReturnGoodsInformation> getInformations() {
        return returnGoods;
    }

    /**
     * 条码输出框发生变化，检查是否格式有错误
     *
     * @param code 条码
     */
    public void codeDataChanged(String code) {
        if (Tool.isOrderCodeValid(code)) {
            inputFormState.setValue(new InputFormState(true));
        } else {
            inputFormState.setValue(new InputFormState(MyApp.getInstance().getString(R.string.format_error)));
        }
    }

    /**
     * 通过条码查询商品信息
     *
     * @param goodsCode 商品条码
     */
    public void queryGoodsInformation(String goodsCode) {
        //检查是否重复商品条码
        for (ReturnGoodsInformation goods : returnGoods) {
            if (goods.id.equals(goodsCode)) {
                queryGoodsInformationResult.setValue(new OperateResult(new OperateError(-1, MyApp.getInstance().getString(R.string.repeat_code), null)));
                return;
            }
        }
        //发送查询
        CommandVo vo = new CommandVo();
        vo.typeEnum = CommandTypeEnum.COMMAND_SALE;
        vo.url = SaleInterface.BarCodeBackById;
        vo.contentType = HttpHandler.ContentType_APP;
        vo.requestMode = HttpHandler.RequestMode_POST;
        Map<String, String> parameters = new HashMap<>();
        parameters.put("id", goodsCode);
        vo.parameters = parameters;
        Invoker.getInstance().setOnEchoResultCallback(this.callback);
        Invoker.getInstance().exec(vo);
    }

    /**
     * 查询积分抵扣金额
     *
     */
    public void queryIntegralActivityBack() {
        CommandVo vo = new CommandVo();
        vo.typeEnum = CommandTypeEnum.COMMAND_SALE;
        vo.url = SaleInterface.IntegralActivityBack;
        vo.contentType = HttpHandler.ContentType_APP;
        vo.requestMode = HttpHandler.RequestMode_POST;
        Map<String, String> parameters = new HashMap<>();
        parameters.put("integral", String.valueOf(deductIntegral));
        vo.parameters = parameters;
        Invoker.getInstance().setOnEchoResultCallback(this.callback);
        Invoker.getInstance().exec(vo);
    }

    /**
     * 退货
     */
    public void returnGoods() {
        //发送查询
        CommandVo vo = new CommandVo();
        vo.typeEnum = CommandTypeEnum.COMMAND_SALE;
        vo.url = SaleInterface.BarCodeBack;
        vo.contentType = HttpHandler.ContentType_APP;
        vo.requestMode = HttpHandler.RequestMode_POST;
        Map<String, String> parameters = new HashMap<>();
        JSONArray array = new JSONArray();
        for (ReturnGoodsInformation item : returnGoods) {
            array.put(item.getJsonString());
        }
        parameters.put("myJson", array.toString());
        vo.parameters = parameters;
        Invoker.getInstance().setOnEchoResultCallback(this.callback);
        Invoker.getInstance().exec(vo);
    }

    /**
     * 查询结果返回接口
     */
    private Invoker.OnExecResultCallback callback = new Invoker.OnExecResultCallback() {
        @Override
        public void execResult(CommandResponse result) {
            switch (result.url) {
                case SaleInterface.BarCodeBackById:         //条码查询
                    if (result.success) {
                        ReturnGoodsInformation item = new ReturnGoodsInformation(result.data);
                        if (!TextUtils.isEmpty(vipPhone) && !item.memberTel.equals(vipPhone)) {
                            queryGoodsInformationResult.setValue(new OperateResult(new OperateError(-1, "非当前会员所购买服饰", null)));
                            return;
                        }
                        if (TextUtils.isEmpty(vipPhone)) {
                            vipPhone = item.memberTel;
                        }
                        vipIntegral = item.balanceIntegral;         //会员积分
                        deductIntegral += item.deductionIntegral;       //扣除积分
                        integralBalance = vipIntegral - deductIntegral;     //积分差额=会员积分-总的扣除积分
                        refundDeduction += item.deductionIntegral;
                        returnGoods.add(item);
//                        queryGoodsInformationResult.setValue(new OperateResult(new OperateInUserView(null)));
                        queryIntegralActivityBack();
                    } else {
                        queryGoodsInformationResult.setValue(new OperateResult(new OperateError(result.code, result.msg, null)));
                    }
                    break;
                case SaleInterface.IntegralActivityBack:         //积分抵扣金额
                    if (result.success) {
                        refundDeduction = Float.parseFloat(result.data); //退款减扣=积分差额所抵扣的钱
                        queryGoodsInformationResult.setValue(new OperateResult(new OperateInUserView(null)));
                    } else {
                        queryGoodsInformationResult.setValue(new OperateResult(new OperateError(result.code, result.msg, null)));
                    }
                    break;
                case SaleInterface.BarCodeBack:             //退货
                    if (result.success) {
                        //清空returnGoods list
                        returnGoods.clear();
                        vipPhone = "";
                        vipIntegral = 0;
                        deductIntegral = 0;
                        integralBalance = 0;
                        refundDeduction = 0;
                        returnGoodsResult.setValue(new OperateResult(new OperateInUserView(null)));
                    } else {
                        returnGoodsResult.setValue(new OperateResult(new OperateError(result.code, result.msg, null)));
                    }
                    break;
                default:
                    break;
            }
        }
    };

    public String getVipPhone() {
        return vipPhone;
    }

    public String getVipIntegral() {
        return String.valueOf(vipIntegral);
    }

    public String getIntegralBalance() {
        return String.valueOf(integralBalance);
    }

    public String getDeductIntegral() {
        return String.valueOf(deductIntegral);
    }

    public String getRefundDeduction() {
        return String.valueOf(refundDeduction);
    }

    public int getNumCount() {
        return returnGoods.size();
    }

    public float getRefundCount() {
        return refundCount;
    }

    public void removeGoods(int position) {
        ReturnGoodsInformation item = returnGoods.get(position);
        deductIntegral -= item.deductionIntegral;       //扣除积分
        integralBalance = vipIntegral - deductIntegral;     //积分差额=会员积分-总的扣除积分
        refundDeduction -= item.deductionIntegral;
        returnGoods.remove(position);
        queryIntegralActivityBack();
    }
}
