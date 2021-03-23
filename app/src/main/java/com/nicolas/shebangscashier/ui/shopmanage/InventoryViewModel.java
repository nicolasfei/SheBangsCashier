package com.nicolas.shebangscashier.ui.shopmanage;

import android.os.Message;
import android.text.TextUtils;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.nicolas.shebangscashier.R;
import com.nicolas.shebangscashier.app.MyApp;
import com.nicolas.shebangscashier.common.InventoryData;
import com.nicolas.shebangscashier.common.NewGoodsSaleStatistics;
import com.nicolas.shebangscashier.common.OperateError;
import com.nicolas.shebangscashier.common.OperateInUserView;
import com.nicolas.shebangscashier.common.OperateResult;
import com.nicolas.shebangscashier.communication.CommandResponse;
import com.nicolas.shebangscashier.communication.CommandTypeEnum;
import com.nicolas.shebangscashier.communication.CommandVo;
import com.nicolas.shebangscashier.communication.Invoker;
import com.nicolas.shebangscashier.communication.manage.ManageInterface;
import com.nicolas.shebangscashier.communication.sale.SaleInterface;
import com.nicolas.toollibrary.HttpHandler;
import com.nicolas.toollibrary.Tool;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InventoryViewModel extends ViewModel {

    private List<InventoryData> dataList;
    private InventoryCondition condition;

    private MutableLiveData<OperateResult> queryResult;

    private int currentPage = 1;
    private int pageSize = 64;              //每一页16条记录
    private int pageCount = 0;              //一共多少也，默认为0 --- 当前查询条件下的订单总数

    public InventoryViewModel() {
        this.dataList = new ArrayList<>();
        this.queryResult = new MutableLiveData<>();
        this.condition = new InventoryCondition();
    }

    public List<InventoryData> getListData() {
        return dataList;
    }

    public LiveData<OperateResult> getQueryResult() {
        return queryResult;
    }


    public InventoryCondition getCondition() {
        return condition;
    }

    /**
     * 查询
     */
    public void query() {
        this.currentPage = 1;
        this.pageSize = 16;
        this.pageCount = 0;
        this.queryInventory();
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
            this.queryResult.setValue(new OperateResult(new OperateError(-1, MyApp.getInstance().getString(R.string.no_more_load), null)));
            return;
        }
        this.currentPage++;
        this.query();
    }

    /**
     * 搜索盘点
     */
    private void queryInventory(){
        CommandVo vo = new CommandVo();
        vo.typeEnum = CommandTypeEnum.COMMAND_MANAGE;
        vo.url = ManageInterface.PDBarCodeSearch;
        vo.contentType = HttpHandler.ContentType_APP;
        vo.requestMode = HttpHandler.RequestMode_POST;
        Map<String, String> parameters = new HashMap<>();
        parameters.put("currentPage", String.valueOf(this.currentPage));
        parameters.put("pageSize", String.valueOf(this.pageSize));
        parameters.put("pageCount", String.valueOf(this.pageCount));

        if (!TextUtils.isEmpty(condition.goodsClassType)) {     //商品类型
            parameters.put("goodsType", condition.goodsClassType);
        }
        if (!TextUtils.isEmpty(condition.sIdInput)) {           //供应商编号
            parameters.put("sId", condition.sIdInput);
        }
        if (!TextUtils.isEmpty(condition.codeInput)) {              //商品条码
            parameters.put("barcodeId", condition.codeInput);
        }
        if (!TextUtils.isEmpty(condition.newGoodsInput)) {          //新货号
            parameters.put("goodsId", condition.newGoodsInput);
        }
        if (!TextUtils.isEmpty(condition.isInventoryChip)) {
            parameters.put("pd", condition.isInventoryChip);
        }
        if (!TextUtils.isEmpty(condition.storageTime)) {            //入库时间
            parameters.put("getTime", condition.storageTime);
        }
        if (!TextUtils.isEmpty(condition.seasonChip)) {             //季节装
            parameters.put("seasonName", condition.seasonChip);
        }
        if (!TextUtils.isEmpty(condition.codeStatusChip)) {         //条码状态
            parameters.put("state", condition.codeStatusChip);
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
                case ManageInterface.PDBarCodeSearch:
                    if (result.success) {
                        try {
                            if (currentPage == 1) {         //这个是新的查询，所以要清空之前的数据
                                dataList.clear();
                            }
                            pageCount = result.total;       //更新总页数
                            //更新数据
                            JSONArray array = new JSONArray(result.data);
                            if (array.length() == 0) {
                                Message msg = new Message();
                                msg.obj = MyApp.getInstance().getString(R.string.noData);
                                queryResult.setValue(new OperateResult(new OperateInUserView(msg)));
                            } else {
                                for (int i = 0; i < array.length(); i++) {
                                    InventoryData item = new InventoryData(array.getString(i));
                                    dataList.add(item);
                                }
                                queryResult.setValue(new OperateResult(new OperateInUserView(null)));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            queryResult.setValue(new OperateResult(new OperateError(-1,
                                    MyApp.getInstance().getString(R.string.format_error), null)));
                        }
                    } else {
                        queryResult.setValue(new OperateResult(new OperateError(result.code, result.msg, null)));
                    }
                    break;
                default:
                    break;
            }
        }
    };

    public class InventoryCondition {
        private String goodsClassType = "";
        private String sIdInput = "";
        private String codeInput = "";
        private String newGoodsInput = "";
        private String isInventoryChip = "";
        private String codeStatusChip = "";
        private String seasonChip = "";
        private String storageTime = Tool.getNearlyOneDaysDateSlot();        //默认查询当天

        private boolean isUpdate = false;

        public void setInventory(String isInventoryChip) {
            if (!this.isInventoryChip.equals(isInventoryChip)) {
                this.isInventoryChip = isInventoryChip;
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

        public void setCodeStatus(String value) {
            if (!this.codeStatusChip.equals(value)) {
                this.codeStatusChip = value;
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

        public void setSeasonChip(String seasonChip) {
            if (!this.seasonChip.equals(seasonChip)) {
                this.seasonChip = seasonChip;
                this.isUpdate = true;
            }
        }

        public void setStorageTime(String value) {
            if (!this.storageTime.equals(value)) {
                this.storageTime = value;
                this.isUpdate = true;
            }
        }

        public String getStorageTime() {
            return storageTime;
        }

        public boolean isUpdate() {
            return isUpdate;
        }

        public void resetUpdate() {
            isUpdate = false;
        }
    }
}
