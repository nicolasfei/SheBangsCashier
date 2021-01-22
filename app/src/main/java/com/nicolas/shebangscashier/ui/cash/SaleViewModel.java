package com.nicolas.shebangscashier.ui.cash;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.nicolas.shebangscashier.R;
import com.nicolas.shebangscashier.app.MyApp;
import com.nicolas.shebangscashier.common.InputFormState;
import com.nicolas.shebangscashier.common.OperateError;
import com.nicolas.shebangscashier.common.OperateInUserView;
import com.nicolas.shebangscashier.common.OperateResult;
import com.nicolas.shebangscashier.communication.CommandResponse;
import com.nicolas.shebangscashier.communication.CommandTypeEnum;
import com.nicolas.shebangscashier.communication.CommandVo;
import com.nicolas.shebangscashier.communication.Invoker;
import com.nicolas.shebangscashier.communication.sale.SaleInterface;
import com.nicolas.shebangscashier.ui.cash.data.SaleGoodsInformation;
import com.nicolas.toollibrary.HttpHandler;
import com.nicolas.toollibrary.Tool;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SaleViewModel extends ViewModel {
    private static final String TAG = "SaleViewModel";
    private ArrayList<SaleGoodsInformation> saleGoods;               //待售卖的商品list
    private int priceTotal;                                     //商品总价

    private MutableLiveData<InputFormState> inputFormState;             //输入格式是否正确
    private MutableLiveData<OperateResult> queryGoodsInformationResult;       //查询商品信息

    public SaleViewModel() {
        this.saleGoods = new ArrayList<>();

        this.inputFormState = new MutableLiveData<>();
        this.queryGoodsInformationResult = new MutableLiveData<>();
    }

    public LiveData<InputFormState> getInputFormState() {
        return inputFormState;
    }

    public LiveData<OperateResult> getQueryGoodsInformationResult() {
        return queryGoodsInformationResult;
    }

    /**
     * 获取待售卖的商品list
     *
     * @return 商品list
     */
    public ArrayList<SaleGoodsInformation> getGoodsInformationList() {
        return saleGoods;
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
     * 添加旧货销售
     *
     * @param old 旧货
     */
    public void addOldGoodsSale(SaleGoodsInformation old) {
        saleGoods.add(old);
        priceTotal += old.totalPrice;
    }

    /**
     * 删除货物
     *
     * @param position position
     */
    public void removeGoods(int position) {
        priceTotal -= saleGoods.get(position).totalPrice;
        saleGoods.remove(position);
    }

    /**
     * 查询已添加的商品数量
     *
     * @return 已添加的商品数量
     */
    public int getGoodsCount() {
        return saleGoods.size();
    }

    /**
     * 查询已添加的商品数量总价
     *
     * @return 已添加的商品数量总价
     */
    public String getPriceTotal() {
        return new DecimalFormat("###.00").format(priceTotal);
    }

    /**
     * 通过条码查询商品信息
     *
     * @param goodsCode 商品条码
     */
    public void queryGoodsInformation(String goodsCode) {
        //检查是否重复商品条码
        for (SaleGoodsInformation goods : saleGoods) {
            if (goods.id.equals(goodsCode)) {
                queryGoodsInformationResult.setValue(new OperateResult(new OperateError(-1, MyApp.getInstance().getString(R.string.repeat_code), null)));
                return;
            }
        }
        Log.d(TAG, "queryGoodsInformation: goodsCode is " + goodsCode);
        //发送查询
        CommandVo vo = new CommandVo();
        vo.typeEnum = CommandTypeEnum.COMMAND_SALE;
        vo.url = SaleInterface.SaleQueryCode;
        vo.contentType = HttpHandler.ContentType_APP;
        vo.requestMode = HttpHandler.RequestMode_POST;
        Map<String, String> parameters = new HashMap<>();
        parameters.put("id", goodsCode);
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
                case SaleInterface.SaleQueryCode:
                    if (result.success) {
                        SaleGoodsInformation item = new SaleGoodsInformation(result.data);
                        saleGoods.add(item);
                        priceTotal += item.totalPrice;
                        queryGoodsInformationResult.setValue(new OperateResult(new OperateInUserView(null)));
                    } else {
                        queryGoodsInformationResult.setValue(new OperateResult(new OperateError(result.code, result.msg, null)));
                    }
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 开始新的收银
     */
    public void startNewCashier() {
        priceTotal = 0;
        saleGoods.clear();
    }
}
