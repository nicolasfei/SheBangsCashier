package com.nicolas.shebangscashier.ui.shopmanage;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.nicolas.shebangscashier.R;
import com.nicolas.shebangscashier.app.MyApp;
import com.nicolas.shebangscashier.common.GoodsInformation;
import com.nicolas.shebangscashier.common.OperateError;
import com.nicolas.shebangscashier.common.OperateInUserView;
import com.nicolas.shebangscashier.common.OperateResult;
import com.nicolas.shebangscashier.common.StartTransferInformation;
import com.nicolas.shebangscashier.communication.CommandResponse;
import com.nicolas.shebangscashier.communication.CommandTypeEnum;
import com.nicolas.shebangscashier.communication.CommandVo;
import com.nicolas.shebangscashier.communication.Invoker;
import com.nicolas.shebangscashier.communication.manage.ManageInterface;
import com.nicolas.shebangscashier.communication.sale.SaleInterface;
import com.nicolas.toollibrary.HttpHandler;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StartTransferViewModel extends ViewModel {

    private List<StartTransferInformation> informationList;
    private MutableLiveData<OperateResult> queryResult;     //查询商品信息
    private MutableLiveData<OperateResult> submitResult;    //盘点提交结果

    public StartTransferViewModel() {
        this.informationList = new ArrayList<>();
        this.queryResult = new MutableLiveData<>();
        this.submitResult = new MutableLiveData<>();
    }

    public List<StartTransferInformation> getDataList() {
        return informationList;
    }

    public MutableLiveData<OperateResult> getQueryResult() {
        return queryResult;
    }

    public MutableLiveData<OperateResult> getSubmitResult() {
        return submitResult;
    }

    public void deleteGoods(int position) {
        if (informationList != null && informationList.size() >= position) {
            informationList.remove(position);
        }
    }

    /**
     * 调货查询
     *
     * @param fid 分店
     * @param id  商品条码
     */
    public void queryBarCode(String fid, String id) {
        //发起调货
        CommandVo vo = new CommandVo();
        vo.typeEnum = CommandTypeEnum.COMMAND_MANAGE;
        vo.url = ManageInterface.TransferGoodsBarCodeById;
        vo.contentType = HttpHandler.ContentType_APP;
        vo.requestMode = HttpHandler.RequestMode_POST;
        Map<String, String> parameters = new HashMap<>();
        parameters.put("fid", fid);
        parameters.put("id", id);
        vo.parameters = parameters;
        Invoker.getInstance().setOnEchoResultCallback(this.callback);
        Invoker.getInstance().exec(vo);
    }

    /**
     * 提交调货
     */
    public void submitAll() {
        //发起调货
        CommandVo vo = new CommandVo();
        vo.typeEnum = CommandTypeEnum.COMMAND_MANAGE;
        vo.url = ManageInterface.TransferGoodsOK;
        vo.contentType = HttpHandler.ContentType_APP;
        vo.requestMode = HttpHandler.RequestMode_POST;
        Map<String, String> parameters = new HashMap<>();
        StringBuilder builder = new StringBuilder();
        for (StartTransferInformation info : informationList) {
            builder.append(info.id).append(",");
        }
        String value = builder.toString();
        parameters.put("idList", value.substring(0, value.length() - 1));
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
                case ManageInterface.TransferGoodsOK:
                    if (result.success) {
                        informationList.clear();
                        submitResult.setValue(new OperateResult(new OperateInUserView(null)));
                    } else {
                        submitResult.setValue(new OperateResult(new OperateError(result.code, result.msg, null)));
                    }
                    break;
                case ManageInterface.TransferGoodsBarCodeById:
                    if (result.success) {
                        StartTransferInformation info = new StartTransferInformation(result.data);
                        for (StartTransferInformation in : informationList) {
                            if (in.id.equals(info.id)) {
                                queryResult.setValue(new OperateResult(new OperateError(-1, "条码重复", null)));
                                return;
                            }
                        }
                        informationList.add(info);
                        queryResult.setValue(new OperateResult(new OperateInUserView(null)));
                    } else {
                        queryResult.setValue(new OperateResult(new OperateError(result.code, result.msg, null)));
                    }
                    break;
                default:
                    break;
            }
        }
    };
}
