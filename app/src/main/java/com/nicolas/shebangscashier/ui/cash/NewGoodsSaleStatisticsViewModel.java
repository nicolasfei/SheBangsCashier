package com.nicolas.shebangscashier.ui.cash;

import android.os.Message;
import android.text.TextUtils;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.nicolas.shebangscashier.R;
import com.nicolas.shebangscashier.app.MyApp;
import com.nicolas.shebangscashier.common.NewGoodsSaleStatistics;
import com.nicolas.shebangscashier.common.OperateError;
import com.nicolas.shebangscashier.common.OperateInUserView;
import com.nicolas.shebangscashier.common.OperateResult;
import com.nicolas.shebangscashier.communication.CommandResponse;
import com.nicolas.shebangscashier.communication.CommandTypeEnum;
import com.nicolas.shebangscashier.communication.CommandVo;
import com.nicolas.shebangscashier.communication.Invoker;
import com.nicolas.shebangscashier.communication.sale.SaleInterface;
import com.nicolas.toollibrary.HttpHandler;
import com.nicolas.toollibrary.Tool;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewGoodsSaleStatisticsViewModel extends ViewModel {

    private List<NewGoodsSaleStatistics> statistics;
    private float saleTotalPrice;
    private int saleNum;

    private NewGoodsSaleStatisticsCondition condition;

    private MutableLiveData<OperateResult> querySaleResult;
    private MutableLiveData<OperateResult> queryStatisticsResult;

    private int currentPage = 1;
    private int pageSize = 64;              //每一页16条记录
    private int pageCount = 0;              //一共多少也，默认为0 --- 当前查询条件下的订单总数

    public NewGoodsSaleStatisticsViewModel() {
        this.statistics = new ArrayList<>();
        this.querySaleResult = new MutableLiveData<>();
        this.queryStatisticsResult = new MutableLiveData<>();

        this.condition = new NewGoodsSaleStatisticsCondition();
    }

    public List<NewGoodsSaleStatistics> getListData() {
        return statistics;
    }

    public LiveData<OperateResult> getQuerySaleResult() {
        return querySaleResult;
    }

    public LiveData<OperateResult> getQueryStatisticsResult() {
        return queryStatisticsResult;
    }

    public NewGoodsSaleStatisticsCondition getCondition() {
        return condition;
    }

    public int getSaleNum() {
        return saleNum;
    }

    public float getSaleTotalPrice() {
        return saleTotalPrice;
    }

    /**
     * 查询
     */
    public void query() {
        this.currentPage = 1;
        this.pageSize = 16;
        this.pageCount = 0;
        this.querySale();
    }

    /**
     * 刷新数据
     */
    public void refreshData() {
        query();
    }

    /**
     * 加载更多数据
     */
    public void loadMoreData() {
        if (this.currentPage >= (this.pageCount % this.pageSize == 0 ? this.pageCount / this.pageSize : this.pageCount / this.pageSize + 1)) {
            this.querySaleResult.setValue(new OperateResult(new OperateError(-1, MyApp.getInstance().getString(R.string.no_more_load), null)));
            return;
        }
        this.currentPage++;
        this.query();
    }

    /**
     * 查询销售
     */
    public void querySale() {
        CommandVo vo = new CommandVo();
        vo.typeEnum = CommandTypeEnum.COMMAND_SALE;
        vo.url = SaleInterface.SaleQuery;
        vo.contentType = HttpHandler.ContentType_APP;
        vo.requestMode = HttpHandler.RequestMode_POST;
        Map<String, String> parameters = new HashMap<>();
        parameters.put("currentPage", String.valueOf(this.currentPage));
        parameters.put("pageSize", String.valueOf(this.pageSize));
        parameters.put("pageCount", String.valueOf(this.pageCount));

        if (!TextUtils.isEmpty(condition.goodsClassType)) {
            parameters.put("goodsClassId", condition.goodsClassType);
        }
        if (!TextUtils.isEmpty(condition.sIdInput)) {
            parameters.put("sId", condition.sIdInput);
        }
        if (!TextUtils.isEmpty(condition.vipPhone)) {
            parameters.put("tel", condition.vipPhone);
        }
        if (!TextUtils.isEmpty(condition.codeInput)) {
            parameters.put("barcodeId", condition.codeInput);
        }
        if (!TextUtils.isEmpty(condition.newGoodsInput)) {
            parameters.put("goodsId", condition.newGoodsInput);
        }
        if (!TextUtils.isEmpty(condition.receiptCode)) {
            parameters.put("receiptId", condition.receiptCode);
        }
        if (!TextUtils.isEmpty(condition.payMethod)) {
            parameters.put("payType", condition.payMethod);
        }
        if (!TextUtils.isEmpty(condition.seasonChip)) {
            parameters.put("seasonName", condition.seasonChip);
        }
        if (!TextUtils.isEmpty(condition.saleTime)) {
            parameters.put("saleTime", condition.saleTime);
        }

        vo.parameters = parameters;
        Invoker.getInstance().setOnEchoResultCallback(this.callback);
        Invoker.getInstance().exec(vo);
    }

    /**
     * 查询销售统计
     */
    public void queryStatistics() {
        CommandVo vo = new CommandVo();
        vo.typeEnum = CommandTypeEnum.COMMAND_SALE;
        vo.url = SaleInterface.SaleStatistics;
        vo.contentType = HttpHandler.ContentType_APP;
        vo.requestMode = HttpHandler.RequestMode_POST;
        Map<String, String> parameters = new HashMap<>();
        parameters.put("currentPage", String.valueOf(this.currentPage));
        parameters.put("pageSize", String.valueOf(this.pageSize));
        parameters.put("pageCount", String.valueOf(this.pageCount));

        if (!TextUtils.isEmpty(condition.goodsClassType)) {
            parameters.put("goodsClassId", condition.goodsClassType);
        }
        if (!TextUtils.isEmpty(condition.sIdInput)) {
            parameters.put("sId", condition.sIdInput);
        }
        if (!TextUtils.isEmpty(condition.vipPhone)) {
            parameters.put("tel", condition.vipPhone);
        }
        if (!TextUtils.isEmpty(condition.codeInput)) {
            parameters.put("barcodeId", condition.codeInput);
        }
        if (!TextUtils.isEmpty(condition.newGoodsInput)) {
            parameters.put("goodsId", condition.newGoodsInput);
        }
        if (!TextUtils.isEmpty(condition.receiptCode)) {
            parameters.put("receiptId", condition.receiptCode);
        }
        if (!TextUtils.isEmpty(condition.payMethod)) {
            parameters.put("payType", condition.payMethod);
        }
        if (!TextUtils.isEmpty(condition.seasonChip)) {
            parameters.put("seasonName", condition.seasonChip);
        }
        if (!TextUtils.isEmpty(condition.saleTime)) {
            parameters.put("saleTime", condition.saleTime);
        }

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
                case SaleInterface.SaleQuery:
                    if (result.success) {
                        try {
                            if (currentPage == 1) {         //这个是新的查询，所以要清空之前的数据
                                statistics.clear();
                            }
                            pageCount = result.total;       //更新总页数
                            //更新数据
                            JSONArray array = new JSONArray(result.data);
                            if (array.length() == 0) {
                                Message msg = new Message();
                                msg.obj = MyApp.getInstance().getString(R.string.noData);
                                querySaleResult.setValue(new OperateResult(new OperateInUserView(msg)));
                            } else {
                                for (int i = 0; i < array.length(); i++) {
                                    NewGoodsSaleStatistics item = new NewGoodsSaleStatistics(array.getString(i));
                                    statistics.add(item);
                                }
                                querySaleResult.setValue(new OperateResult(new OperateInUserView(null)));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            querySaleResult.setValue(new OperateResult(new OperateError(-1,
                                    MyApp.getInstance().getString(R.string.format_error), null)));
                        }
                    } else {
                        querySaleResult.setValue(new OperateResult(new OperateError(result.code, result.msg, null)));
                    }
                    break;
                case SaleInterface.SaleStatistics:
                    if (result.success) {
                        saleNum = result.saleNum;
                        saleTotalPrice = result.saleTotalPrice;
                        queryStatisticsResult.setValue(new OperateResult(new OperateInUserView(null)));
                    } else {
                        queryStatisticsResult.setValue(new OperateResult(new OperateError(result.code, result.msg, null)));
                    }
                    break;
                default:
                    break;
            }
        }
    };


    public class NewGoodsSaleStatisticsCondition {
        private String goodsClassType = "";
        private String sIdInput = "";
        private String vipPhone = "";
        private String codeInput = "";
        private String newGoodsInput = "";
        private String receiptCode = "";
        private String payMethod = "";
        private String seasonChip = "";
        private String saleTime = Tool.getNearlyOneDaysDateSlot();        //默认查询当天

        private boolean isUpdate = false;

        public void setPayMethod(String pay) {
            if (!this.payMethod.equals(pay)) {
                this.payMethod = pay;
                this.isUpdate = true;
            }
        }

        public void setGoodsClassType(String goodsClassType) {
            if (!this.goodsClassType.equals(goodsClassType)) {
                this.goodsClassType = goodsClassType;
                this.isUpdate = true;
            }
        }

        public void setsIdInput(String sIdInput) {
            if (!this.sIdInput.equals(sIdInput)) {
                this.sIdInput = sIdInput;
                this.isUpdate = true;
            }
        }

        public void setVipPhone(String vipPhone) {
            if (!this.vipPhone.equals(vipPhone)) {
                this.vipPhone = vipPhone;
                this.isUpdate = true;
            }
        }

        public void setCodeInput(String codeInput) {
            if (!this.codeInput.equals(codeInput)) {
                this.codeInput = codeInput;
                this.isUpdate = true;
            }
        }

        public void setNewGoodsInput(String newGoodsInput) {
            if (!this.newGoodsInput.equals(newGoodsInput)) {
                this.newGoodsInput = newGoodsInput;
                this.isUpdate = true;
            }
        }

        public void setReceiptCode(String receiptCode) {
            if (!this.receiptCode.equals(receiptCode)) {
                this.receiptCode = receiptCode;
                this.isUpdate = true;
            }
        }

        public void setSeasonChip(String seasonChip) {
            if (!this.seasonChip.equals(seasonChip)) {
                this.seasonChip = seasonChip;
                this.isUpdate = true;
            }
        }

        public void setSaleTime(String saleTime) {
            if (!this.saleTime.equals(saleTime)) {
                this.saleTime = saleTime;
                this.isUpdate = true;
            }
        }

        public String getSaleTime() {
            return saleTime;
        }

        public boolean isUpdate() {
            return isUpdate;
        }

        public void resetUpdate() {
            isUpdate = false;
        }
    }
}
