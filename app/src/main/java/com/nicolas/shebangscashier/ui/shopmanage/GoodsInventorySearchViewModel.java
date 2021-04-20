package com.nicolas.shebangscashier.ui.shopmanage;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.nicolas.shebangscashier.common.GoodsInventoryData;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GoodsInventorySearchViewModel extends ViewModel {

    private List<GoodsInventoryData> dataList;
    private MutableLiveData<OperateResult> searchGoodsInventoryResult;     //查询商品信息

    public GoodsInventorySearchViewModel(){
        this.dataList = new ArrayList<>();
        this.searchGoodsInventoryResult = new MutableLiveData<>();
    }

    public List<GoodsInventoryData> getDataList() {
            return dataList;
    }

    public LiveData<OperateResult> getSearchGoodsInventoryResult() {
        return searchGoodsInventoryResult;
    }

    public void searchGoodsInventory(String barCode) {
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
     * 查询结果返回接口
     */
    private Invoker.OnExecResultCallback callback = new Invoker.OnExecResultCallback() {
        @Override
        public void execResult(CommandResponse result) {
            switch (result.url) {
                case ManageInterface.BackGoodsPrint:
                    if (result.success) {
                        dataList.clear();       //清空列表
                        //添加数据到列表
                        searchGoodsInventoryResult.setValue(new OperateResult(new OperateInUserView(null)));
                    } else {
                        searchGoodsInventoryResult.setValue(new OperateResult(new OperateError(result.code, result.msg, null)));
                    }
                    break;
                default:
                    break;
            }
        }
    };
}
