package com.nicolas.shebangscashier.ui.shopmanage;

import android.os.Message;
import android.text.TextUtils;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.nicolas.shebangscashier.R;
import com.nicolas.shebangscashier.app.MyApp;
import com.nicolas.shebangscashier.common.InventoryData;
import com.nicolas.shebangscashier.common.OperateError;
import com.nicolas.shebangscashier.common.OperateInUserView;
import com.nicolas.shebangscashier.common.OperateResult;
import com.nicolas.shebangscashier.common.QualityManagementData;
import com.nicolas.shebangscashier.communication.CommandResponse;
import com.nicolas.shebangscashier.communication.CommandTypeEnum;
import com.nicolas.shebangscashier.communication.CommandVo;
import com.nicolas.shebangscashier.communication.Invoker;
import com.nicolas.shebangscashier.communication.manage.ManageInterface;
import com.nicolas.toollibrary.HttpHandler;
import com.nicolas.toollibrary.Tool;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QualityManagementViewModel extends ViewModel {

    private QualityManagementCondition condition;
    private MutableLiveData<OperateResult> queryQualityResult;
    private List<QualityManagementData> dataList;


    private int currentPage = 1;
    private int pageSize = 64;              //每一页16条记录
    private int pageCount = 0;              //一共多少也，默认为0 --- 当前查询条件下的订单总数

    public QualityManagementViewModel() {
        this.condition = new QualityManagementCondition();
        this.queryQualityResult = new MutableLiveData<>();

        this.dataList = new ArrayList<>();
    }

    public QualityManagementCondition getCondition() {
        return condition;
    }

    public LiveData<OperateResult> getQueryQualityResult() {
        return queryQualityResult;
    }

    /**
     * 查询
     */
    public void query() {
        this.currentPage = 1;
        this.pageSize = 16;
        this.pageCount = 0;
        this.queryQuality();
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
            this.queryQualityResult.setValue(new OperateResult(new OperateError(-1, MyApp.getInstance().getString(R.string.no_more_load), null)));
            return;
        }
        this.currentPage++;
        this.query();
    }

    /**
     * 质检查询
     */
    private void queryQuality() {
        CommandVo vo = new CommandVo();
        vo.typeEnum = CommandTypeEnum.COMMAND_MANAGE;
        vo.url = ManageInterface.Quality;
        vo.contentType = HttpHandler.ContentType_APP;
        vo.requestMode = HttpHandler.RequestMode_POST;
        Map<String, String> parameters = new HashMap<>();
        parameters.put("currentPage", String.valueOf(this.currentPage));
        parameters.put("pageSize", String.valueOf(this.pageSize));
        parameters.put("pageCount", String.valueOf(this.pageCount));
        if (!TextUtils.isEmpty(condition.goodsCode)) {          //商品条码
            parameters.put("barcodeId", condition.goodsCode);
        }
        if (!TextUtils.isEmpty(condition.goodsID)) {            //新货号
            parameters.put("goodsId", condition.goodsID);
        }
        if (!TextUtils.isEmpty(condition.id)) {                 //编号
            parameters.put("qualityClassId", condition.id);
        }
        if (!TextUtils.isEmpty(condition.registerTime)) {       //登记时间
            parameters.put("createTime", condition.registerTime);
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
                case ManageInterface.Quality:
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
                                queryQualityResult.setValue(new OperateResult(new OperateInUserView(msg)));
                            } else {
                                for (int i = 0; i < array.length(); i++) {
                                    QualityManagementData item = new QualityManagementData(array.getString(i));
                                    dataList.add(item);
                                }
                                queryQualityResult.setValue(new OperateResult(new OperateInUserView(null)));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            queryQualityResult.setValue(new OperateResult(new OperateError(-1,
                                    MyApp.getInstance().getString(R.string.format_error), null)));
                        }
                    } else {
                        queryQualityResult.setValue(new OperateResult(new OperateError(result.code, result.msg, null)));
                    }
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 商品条码是否已经存在
     *
     * @param barCode 条码
     * @return yes/no
     */
    public boolean isHaveBarCode(String barCode) {
        for (QualityManagementData data : dataList) {
            if (barCode.equals(data.barCodeId)) {
                return true;
            }
        }
        return false;
    }


    public class QualityManagementCondition {
        public String goodsCode = "";
        public String id = "";
        public String goodsID = "";
        public String registerTime = Tool.getNearlyOneDaysDateSlot();        //默认查询当天

        private boolean isUpdate = false;

        public void setGoodsCode(String value) {
            if (!this.goodsCode.equals(value)) {
                this.goodsCode = value;
                this.isUpdate = true;
            }
        }

        public void setId(String value) {
            if (!this.id.equals(value)) {
                this.id = value;
                this.isUpdate = true;
            }
        }

        public void setGoodsID(String value) {
            if (!this.goodsID.equals(value)) {
                this.goodsID = value;
                this.isUpdate = true;
            }
        }

        public void setRegisterTime(String value) {
            if (!this.registerTime.equals(value)) {
                this.registerTime = value;
                this.isUpdate = true;
            }
        }

        public String getId() {
            return id;
        }

        public String getRegisterTime() {
            return registerTime;
        }

        public boolean isUpdate() {
            return isUpdate;
        }

        public void resetUpdate() {
            isUpdate = false;
        }
    }
}
