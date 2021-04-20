package com.nicolas.shebangscashier.ui.shopmanage;

import android.os.Message;
import android.text.TextUtils;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.nicolas.shebangscashier.R;
import com.nicolas.shebangscashier.app.MyApp;
import com.nicolas.shebangscashier.common.GoodsTransferData;
import com.nicolas.shebangscashier.common.InventoryData;
import com.nicolas.shebangscashier.common.OperateError;
import com.nicolas.shebangscashier.common.OperateInUserView;
import com.nicolas.shebangscashier.common.OperateResult;
import com.nicolas.shebangscashier.common.ReplenishmentInformation;
import com.nicolas.shebangscashier.communication.CommandResponse;
import com.nicolas.shebangscashier.communication.CommandTypeEnum;
import com.nicolas.shebangscashier.communication.CommandVo;
import com.nicolas.shebangscashier.communication.Invoker;
import com.nicolas.shebangscashier.communication.manage.ManageInterface;
import com.nicolas.toollibrary.HttpHandler;
import com.nicolas.toollibrary.Tool;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GoodsTransferViewModel extends ViewModel {

    private GoodsTransferCondition condition;
    private int currentPage = 1;
    private int pageSize = 64;              //每一页16条记录
    private int pageCount = 0;              //一共多少也，默认为0 --- 当前查询条件下的订单总数

    private MutableLiveData<OperateResult> queryResult;
    private MutableLiveData<OperateResult> deleteResult;
    private MutableLiveData<OperateResult> queryGoodsByGoodsIdResult;
    private MutableLiveData<OperateResult> replenishmentSubmitResult;
    private List<GoodsTransferData> dataList;

    private GoodsTransferData currentData;
    private ReplenishmentInformation information;

    private int deletePosBak;

    public GoodsTransferViewModel() {
        this.dataList = new ArrayList<>();
        this.queryResult = new MutableLiveData<>();
        this.deleteResult = new MutableLiveData<>();
        this.queryGoodsByGoodsIdResult = new MutableLiveData<>();
        this.replenishmentSubmitResult = new MutableLiveData<OperateResult>();
        this.condition = new GoodsTransferCondition();
    }

    public GoodsTransferCondition getCondition() {
        return condition;
    }

    public LiveData<OperateResult> getQueryResult() {
        return queryResult;
    }

    public LiveData<OperateResult> getDeleteResult() {
        return deleteResult;
    }

    public LiveData<OperateResult> getQueryGoodsByGoodsIdResult() {
        return queryGoodsByGoodsIdResult;
    }

    public LiveData<OperateResult> getReplenishmentSubmitResult() {
        return replenishmentSubmitResult;
    }

    public ReplenishmentInformation getReplenishmentInformation() {
        return information;
    }

    public List<GoodsTransferData> getDataList() {
        return dataList;
    }

    public GoodsTransferData getCurrentData() {
        return currentData;
    }

    /**
     * 查询
     */
    public void query() {
        this.currentPage = 1;
        this.pageSize = 16;
        this.pageCount = 0;
        this.queryTransfer();
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
     * 发起调货--查询
     */
    private void queryTransfer() {
        CommandVo vo = new CommandVo();
        vo.typeEnum = CommandTypeEnum.COMMAND_MANAGE;
        vo.url = ManageInterface.TransferGoodsSearchFrom;
        vo.contentType = HttpHandler.ContentType_APP;
        vo.requestMode = HttpHandler.RequestMode_POST;
        Map<String, String> parameters = new HashMap<>();
        parameters.put("currentPage", String.valueOf(this.currentPage));
        parameters.put("pageSize", String.valueOf(this.pageSize));
        parameters.put("pageCount", String.valueOf(this.pageCount));

        if (!TextUtils.isEmpty(condition.goodsClassId)) {     //商品类型
            parameters.put("goodsType", condition.goodsClassId);
        }
        if (!TextUtils.isEmpty(condition.sId)) {           //供应商编号
            parameters.put("sId", condition.sId);
        }
        if (!TextUtils.isEmpty(condition.barcodeId)) {              //商品条码
            parameters.put("barcodeId", condition.barcodeId);
        }
        if (!TextUtils.isEmpty(condition.goodsId)) {          //新货号
            parameters.put("goodsId", condition.goodsId);
        }
        if (!TextUtils.isEmpty(condition.okTime)) {             //调货接收时间
            parameters.put("okTime", condition.okTime);
        }
        if (!TextUtils.isEmpty(condition.transferTime)) {            //调货发起时间
            parameters.put("transferTime", condition.transferTime);
        }
        if (!TextUtils.isEmpty(condition.branchTo)) {             //季节装
            parameters.put("branchTo", condition.branchTo);
        }
        if (!TextUtils.isEmpty(condition.state)) {         //条码状态
            parameters.put("state", condition.state);
        }

        vo.parameters = parameters;
        Invoker.getInstance().setOnEchoResultCallback(this.callback);
        Invoker.getInstance().exec(vo);
    }

    /**
     * 当前选中的data
     *
     * @param pos pos
     */
    public void setCurrentReceiveStoreReceipt(int pos) {
        this.currentData = dataList.get(pos);
    }

    /**
     * 调货删除
     *
     * @param position position
     */
    public void deleteTransferGoods(int position) {
        this.deletePosBak = position;
        CommandVo vo = new CommandVo();
        vo.typeEnum = CommandTypeEnum.COMMAND_MANAGE;
        vo.url = ManageInterface.TransferGoodsCancel;
        vo.contentType = HttpHandler.ContentType_APP;
        vo.requestMode = HttpHandler.RequestMode_POST;
        Map<String, String> parameters = new HashMap<>();
        parameters.put("id", dataList.get(position).id);
        vo.parameters = parameters;
        Invoker.getInstance().setOnEchoResultCallback(this.callback);
        Invoker.getInstance().exec(vo);
    }

    /**
     * 查询补货信息
     *
     * @param goodsId 货号
     */
    public void queryReplenishment(String goodsId) {
        CommandVo vo = new CommandVo();
        vo.typeEnum = CommandTypeEnum.COMMAND_MANAGE;
        vo.url = ManageInterface.GoodsByGoodsId;
        vo.contentType = HttpHandler.ContentType_APP;
        vo.requestMode = HttpHandler.RequestMode_GET;
        Map<String, String> parameters = new HashMap<>();
        parameters.put("goodsId", goodsId);
        vo.parameters = parameters;
        Invoker.getInstance().setOnEchoResultCallback(this.callback);
        Invoker.getInstance().exec(vo);
    }

    /**
     * 补货提交
     *
     * @param goodsId    货号
     * @param properties 属性
     */
    public void replenishmentSubmit(String goodsId, List<ReplenishmentInformation.Property> properties) {
        boolean isHav = false;
        JSONArray array = new JSONArray();
        for (ReplenishmentInformation.Property property : properties) {
            if (property.val > 0) {
                isHav = true;
                JSONObject object = new JSONObject();
                try {
                    object.put("propertyId", property.id);
                    object.put("orderNum", property.val);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                array.put(object.toString());
            }
        }
        if (!isHav) {
            queryGoodsByGoodsIdResult.setValue(new OperateResult(new OperateError(-1, "未补货", null)));
            return;
        }
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("gId", goodsId);
            jsonObject.put("orderData", array.toString());
            CommandVo vo = new CommandVo();
            vo.typeEnum = CommandTypeEnum.COMMAND_MANAGE;
            vo.url = ManageInterface.OrderByBranch;
            vo.contentType = HttpHandler.ContentType_JSON;
            vo.requestMode = HttpHandler.RequestMode_POST;
            Map<String, String> parameters = new HashMap<>();
            parameters.put("myJson", jsonObject.toString());
            vo.parameters = parameters;
            Invoker.getInstance().setOnEchoResultCallback(this.callback);
            Invoker.getInstance().exec(vo);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 查询结果返回接口
     */
    private Invoker.OnExecResultCallback callback = new Invoker.OnExecResultCallback() {
        @Override
        public void execResult(CommandResponse result) {
            switch (result.url) {
                case ManageInterface.TransferGoodsSearchFrom:
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
                                    GoodsTransferData item = new GoodsTransferData(array.getString(i));
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
                case ManageInterface.TransferGoodsCancel:
                    if (result.success) {
                        dataList.remove(deletePosBak);
                        deleteResult.setValue(new OperateResult(new OperateInUserView(null)));
                    } else {
                        deleteResult.setValue(new OperateResult(new OperateError(result.code, result.msg, null)));
                    }
                    break;
                case ManageInterface.GoodsByGoodsId:
                    if (result.success) {
                        information = new ReplenishmentInformation(result.data);
                        information.setProperties(result.property);
                        queryGoodsByGoodsIdResult.setValue(new OperateResult(new OperateInUserView(null)));
                    } else {
                        queryGoodsByGoodsIdResult.setValue(new OperateResult(new OperateError(result.code, result.msg, null)));
                    }
                    break;
                case ManageInterface.OrderByBranch:
                    if (result.success) {
                        replenishmentSubmitResult.setValue(new OperateResult(new OperateInUserView(null)));
                    } else {
                        replenishmentSubmitResult.setValue(new OperateResult(new OperateError(result.code, result.msg, null)));
                    }
                    break;
                default:
                    break;
            }
        }
    };

    public class GoodsTransferCondition {
        private String acceptanceShop = "";       //接收店铺
        private String goodsClassId = "";
        private String sId = "";
        private String barcodeId = "";
        private String goodsId = "";
        private String state = "";
        private String okTime = "";
        private String branchTo = "";
        private String transferTime = Tool.getNearlyOneDaysDateSlot();        //默认查询当天

        private boolean isUpdate = false;

        public void setAcceptanceShop(String acceptanceShop) {
            if (!this.acceptanceShop.equals(acceptanceShop)) {
                this.acceptanceShop = acceptanceShop;
                this.isUpdate = true;
            }
        }

        public String getAcceptanceShop() {
            return acceptanceShop;
        }

        public void setGoodsClassId(String value) {
            if (!this.goodsClassId.equals(value)) {
                this.goodsClassId = value;
                this.isUpdate = true;
            }
        }

        public void setsId(String value) {
            if (!this.sId.equals(value)) {
                this.sId = value;
                this.isUpdate = true;
            }
        }

        public void setState(String value) {
            if (!this.state.equals(value)) {
                this.state = value;
                this.isUpdate = true;
            }
        }

        public void setBarcodeId(String value) {
            if (!this.barcodeId.equals(value)) {
                this.barcodeId = value;
                this.isUpdate = true;
            }
        }

        public void setGoodsId(String value) {
            if (!this.goodsId.equals(value)) {
                this.goodsId = value;
                this.isUpdate = true;
            }
        }

        public void setOkTime(String value) {
            if (!this.okTime.equals(value)) {
                this.okTime = value;
                this.isUpdate = true;
            }
        }

        public String getOkTime() {
            return okTime;
        }

        public void setBranchTo(String branchTo) {
            this.branchTo = branchTo;
        }

        public void setTransferTime(String value) {
            if (!this.transferTime.equals(value)) {
                this.transferTime = value;
                this.isUpdate = true;
            }
        }

        public String getTransferTime() {
            return transferTime;
        }

        public boolean isUpdate() {
            return isUpdate;
        }

        public void resetUpdate() {
            isUpdate = false;
        }
    }
}
