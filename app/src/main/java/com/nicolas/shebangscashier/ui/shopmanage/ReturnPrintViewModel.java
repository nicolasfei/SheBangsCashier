package com.nicolas.shebangscashier.ui.shopmanage;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.nicolas.shebangscashier.common.OperateError;
import com.nicolas.shebangscashier.common.OperateInUserView;
import com.nicolas.shebangscashier.common.OperateResult;
import com.nicolas.shebangscashier.common.ReturnPrintData;
import com.nicolas.shebangscashier.communication.CommandResponse;
import com.nicolas.shebangscashier.communication.CommandTypeEnum;
import com.nicolas.shebangscashier.communication.CommandVo;
import com.nicolas.shebangscashier.communication.Invoker;
import com.nicolas.shebangscashier.communication.manage.ManageInterface;
import com.nicolas.toollibrary.HttpHandler;

import java.util.HashMap;
import java.util.Map;

public class ReturnPrintViewModel extends ViewModel {

    private ReturnPrintData data;
    private MutableLiveData<OperateResult> queryReturnPrintResult;     //查询商品信息
    private MutableLiveData<OperateResult> returnPrintResult;          //打印结果

    public ReturnPrintViewModel() {
        data = new ReturnPrintData("");
        queryReturnPrintResult = new MutableLiveData<>();
        returnPrintResult = new MutableLiveData<>();
    }

    public ReturnPrintData getData() {
        return data;
    }

    public LiveData<OperateResult> getQueryReturnPrintResult() {
        return queryReturnPrintResult;
    }

    public LiveData<OperateResult> getReturnPrintResult() {
        return returnPrintResult;
    }

    /**
     * 查询商品条码
     *
     * @param barCode 条码
     */
    public void queryBarCode(String barCode) {
        CommandVo vo = new CommandVo();
        vo.typeEnum = CommandTypeEnum.COMMAND_MANAGE;
        vo.url = ManageInterface.BackGoodsPrint;
        vo.contentType = HttpHandler.ContentType_APP;
        vo.requestMode = HttpHandler.RequestMode_GET;
        Map<String, String> parameters = new HashMap<>();
        parameters.put("id", barCode);
        vo.parameters = parameters;
        Invoker.getInstance().setOnEchoResultCallback(this.callback);
        Invoker.getInstance().exec(vo);
    }

    /**
     * 打印返货条码
     */
    public void printReturnGoods() {
        //打印条码
        //提交到服务器
        CommandVo vo = new CommandVo();
        vo.typeEnum = CommandTypeEnum.COMMAND_MANAGE;
        vo.url = ManageInterface.BackGoodsPrintOK;
        vo.contentType = HttpHandler.ContentType_APP;
        vo.requestMode = HttpHandler.RequestMode_GET;
        Map<String, String> parameters = new HashMap<>();
        parameters.put("id", data.id);
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
                case ManageInterface.BackGoodsPrint:
                    if (result.success) {
                        data = new ReturnPrintData(result.data);
                        queryReturnPrintResult.setValue(new OperateResult(new OperateInUserView(null)));
                    } else {
                        queryReturnPrintResult.setValue(new OperateResult(new OperateError(result.code, result.msg, null)));
                    }
                    break;
                case ManageInterface.BackGoodsPrintOK:
                    if (result.success) {
                        returnPrintResult.setValue(new OperateResult(new OperateInUserView(null)));
                    } else {
                        returnPrintResult.setValue(new OperateResult(new OperateError(result.code, result.msg, null)));
                    }
                    break;
                default:
                    break;
            }
        }
    };
}
