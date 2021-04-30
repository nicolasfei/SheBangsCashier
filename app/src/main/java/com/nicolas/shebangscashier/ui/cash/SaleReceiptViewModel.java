package com.nicolas.shebangscashier.ui.cash;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.nicolas.printerlibraryforufovo.PrinterManager;
import com.nicolas.shebangscashier.R;
import com.nicolas.shebangscashier.app.MyApp;
import com.nicolas.shebangscashier.common.GoodsInformation;
import com.nicolas.shebangscashier.common.OperateError;
import com.nicolas.shebangscashier.common.OperateInUserView;
import com.nicolas.shebangscashier.common.OperateResult;
import com.nicolas.shebangscashier.communication.CommandResponse;
import com.nicolas.shebangscashier.communication.CommandTypeEnum;
import com.nicolas.shebangscashier.communication.CommandVo;
import com.nicolas.shebangscashier.communication.Invoker;
import com.nicolas.shebangscashier.communication.sale.SaleInterface;
import com.nicolas.shebangscashier.ui.cash.data.SettlementGoodsInformation;
import com.nicolas.shebangscashier.ui.set.printer.PrintContent;
import com.nicolas.toollibrary.HttpHandler;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SaleReceiptViewModel extends ViewModel {

    private MutableLiveData<OperateResult> receiptQueryResult;     //小票信息查询结果
    private List<GoodsInformation> goodsList;

    public SaleReceiptViewModel() {
        this.receiptQueryResult = new MutableLiveData<OperateResult>();
        this.goodsList = new ArrayList<>();
    }

    public LiveData<OperateResult> getReceiptQueryResult() {
        return receiptQueryResult;
    }

    public List<GoodsInformation> getGoodsList() {
        return goodsList;
    }

    /**
     * 通过条码查询小票
     *
     * @param value code
     */
    public void queryCode(String value) {
        //发送查询
        CommandVo vo = new CommandVo();
        vo.typeEnum = CommandTypeEnum.COMMAND_SALE;
        vo.url = SaleInterface.SaleReceipt;
        vo.contentType = HttpHandler.ContentType_APP;
        vo.requestMode = HttpHandler.RequestMode_POST;
        Map<String, String> parameters = new HashMap<>();
        parameters.put("barcodeId", value);
        vo.parameters = parameters;
        Invoker.getInstance().setOnEchoResultCallback(this.callback);
        Invoker.getInstance().exec(vo);
    }

    /**
     * 通过小票码查询小票
     *
     * @param value receipt
     */
    public void queryReceipt(String value) {
        //发送查询
        CommandVo vo = new CommandVo();
        vo.typeEnum = CommandTypeEnum.COMMAND_SALE;
        vo.url = SaleInterface.SaleReceipt;
        vo.contentType = HttpHandler.ContentType_APP;
        vo.requestMode = HttpHandler.RequestMode_POST;
        Map<String, String> parameters = new HashMap<>();
        parameters.put("receiptId", value);
        vo.parameters = parameters;
        Invoker.getInstance().setOnEchoResultCallback(this.callback);
        Invoker.getInstance().exec(vo);
    }

    /**
     * 查询上一次销售小票
     */
    public void queryLastReceipt() {
        CommandVo vo = new CommandVo();
        vo.typeEnum = CommandTypeEnum.COMMAND_SALE;
        vo.url = SaleInterface.SaleReceipt;
        vo.contentType = HttpHandler.ContentType_APP;
        vo.requestMode = HttpHandler.RequestMode_POST;
        Map<String, String> parameters = new HashMap<>();
        vo.parameters = parameters;
        parameters.put("receiptId", "");
        parameters.put("barcodeId", "");
        Invoker.getInstance().setOnEchoResultCallback(this.callback);
        Invoker.getInstance().exec(vo);
    }

    /**
     * 删除商品
     *
     * @param position pos
     */
    public void deleteGoods(int position) {
        if (goodsList.size() > position) {
            goodsList.remove(position);
        }
    }

    /**
     * 获取商品总数
     *
     * @return 商品总数
     */
    public int getGoodsCount() {
        int count = 0;
        for (GoodsInformation goods : goodsList) {
            count += goods.saleNum;
        }
        return count;
    }

    /**
     * 获取商品总价
     *
     * @return 商品总价
     */
    public float getGoodsTotalPrice() {
        float price = 0;
        for (GoodsInformation goods : goodsList) {
            price += goods.saleTotalPrice;
        }
        return price;
    }

    /**
     * 查询结果返回接口
     */
    private Invoker.OnExecResultCallback callback = new Invoker.OnExecResultCallback() {
        @Override
        public void execResult(CommandResponse result) {
            switch (result.url) {
                case SaleInterface.SaleReceipt:
                    if (result.success) {
                        try {
                            JSONArray array = new JSONArray(result.data);
                            if (array.length()==0){
                                receiptQueryResult.setValue(new OperateResult(new OperateError(-1, MyApp.getInstance().getString(R.string.noData), null)));
                                return;
                            }
                            for (int i = 0; i < array.length(); i++) {
                                GoodsInformation goods = new GoodsInformation(array.getString(i));
                                boolean isRepeat = false;
                                for (GoodsInformation item : goodsList) {
                                    if (item.id.equals(goods.id)) {
                                        isRepeat = true;
                                        break;
                                    }
                                }
                                if (isRepeat) {      //重复
                                    receiptQueryResult.setValue(new OperateResult(new OperateError(-1, MyApp.getInstance().getString(R.string.repeat_receipt), null)));
                                    return;
                                }
                                goodsList.add(goods);
                                receiptQueryResult.setValue(new OperateResult(new OperateInUserView(null)));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            receiptQueryResult.setValue(new OperateResult(new OperateError(-1, MyApp.getInstance().getString(R.string.date_format_error), null)));
                        }
                    } else {
                        receiptQueryResult.setValue(new OperateResult(new OperateError(result.code, result.msg, null)));
                    }
                    break;
                default:
                    break;
            }
        }
    };
}
