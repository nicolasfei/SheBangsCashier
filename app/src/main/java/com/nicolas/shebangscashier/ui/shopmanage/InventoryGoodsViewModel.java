package com.nicolas.shebangscashier.ui.shopmanage;

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
import com.nicolas.shebangscashier.communication.manage.ManageInterface;
import com.nicolas.toollibrary.HttpHandler;
import com.nicolas.toollibrary.Tool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class InventoryGoodsViewModel extends ViewModel {

    private ArrayList<String> arrayList;                    //待售卖的商品list

    private MutableLiveData<InputFormState> inputFormState; //输入格式是否正确
    private MutableLiveData<OperateResult> queryResult;     //查询商品信息
    private MutableLiveData<OperateResult> submitResult;    //盘点提交结果

    public InventoryGoodsViewModel() {
        this.arrayList = new ArrayList<>();

        this.inputFormState = new MutableLiveData<>();
        this.queryResult = new MutableLiveData<>();
        this.submitResult = new MutableLiveData<>();
    }

    public MutableLiveData<OperateResult> getQueryResult() {
        return queryResult;
    }

    public MutableLiveData<OperateResult> getSubmitResult() {
        return submitResult;
    }

    public MutableLiveData<InputFormState> getInputFormState() {
        return inputFormState;
    }

    public ArrayList<String> getInformationList() {
        return arrayList;
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
     * 获取盘点数量
     *
     * @return 盘点数量
     */
    public int getInventoryCount() {
        return arrayList.size();
    }

    /**
     * 条码查询
     *
     * @param goodsCode 条码
     */
    public void queryGoodsInformation(String goodsCode) {
        //一次最多盘点50个
        if (arrayList.size() >= 50) {
            queryResult.setValue(new OperateResult(new OperateError(-1, MyApp.getInstance().getString(R.string.too_more), null)));
            return;
        }

        //检查是否重复商品条码
        for (String goods : arrayList) {
            if (goods.equals(goodsCode)) {
                queryResult.setValue(new OperateResult(new OperateError(-1, MyApp.getInstance().getString(R.string.repeat_code), null)));
                return;
            }
        }
        //发送查询
        CommandVo vo = new CommandVo();
        vo.typeEnum = CommandTypeEnum.COMMAND_MANAGE;
        vo.url = ManageInterface.PDInventoryById;
        vo.contentType = HttpHandler.ContentType_APP;
        vo.requestMode = HttpHandler.RequestMode_POST;
        Map<String, String> parameters = new HashMap<>();
        parameters.put("id", goodsCode);
        vo.parameters = parameters;
        Invoker.getInstance().setOnEchoResultCallback(this.callback);
        Invoker.getInstance().exec(vo);
    }

    /**
     * 提交数据
     */
    public void submitData() {
        //发送查询
        CommandVo vo = new CommandVo();
        vo.typeEnum = CommandTypeEnum.COMMAND_MANAGE;
        vo.url = ManageInterface.PDInventoryUp;
        vo.contentType = HttpHandler.ContentType_APP;
        vo.requestMode = HttpHandler.RequestMode_POST;
        Map<String, String> parameters = new HashMap<>();
        StringBuilder builder = new StringBuilder();
        for (String item : arrayList) {
            builder.append(item.split("：")[1]).append(",");
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
                case ManageInterface.PDInventoryById:
                    if (result.success) {
                        arrayList.add((arrayList.size()+"："+result.data));
                        queryResult.setValue(new OperateResult(new OperateInUserView(null)));
                    } else {
                        queryResult.setValue(new OperateResult(new OperateError(result.code, result.msg, null)));
                    }
                    break;
                case ManageInterface.PDInventoryUp:
                    if (result.success) {
                        arrayList.clear();
                        submitResult.setValue(new OperateResult(new OperateInUserView(null)));
                    } else {
                        submitResult.setValue(new OperateResult(new OperateError(result.code, result.msg, null)));
                    }
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 删除某个元素
     *
     * @param position 位置
     */
    public void removeGoods(int position) {
        this.arrayList.remove(position);
    }

    /**
     * 是否还有未处理的数据
     *
     * @return yes/no
     */
    public boolean isHavDataNotProcessed() {
        return arrayList.size() > 0;
    }
}
