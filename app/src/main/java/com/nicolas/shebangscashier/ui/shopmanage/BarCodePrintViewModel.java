package com.nicolas.shebangscashier.ui.shopmanage;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.nicolas.printerlibraryforufovo.PrinterManager;
import com.nicolas.shebangscashier.R;
import com.nicolas.shebangscashier.app.MyApp;
import com.nicolas.shebangscashier.common.BarCodeInformation;
import com.nicolas.shebangscashier.common.OperateError;
import com.nicolas.shebangscashier.common.OperateInUserView;
import com.nicolas.shebangscashier.common.OperateResult;
import com.nicolas.shebangscashier.communication.CommandResponse;
import com.nicolas.shebangscashier.communication.CommandTypeEnum;
import com.nicolas.shebangscashier.communication.CommandVo;
import com.nicolas.shebangscashier.communication.Invoker;
import com.nicolas.shebangscashier.communication.manage.ManageInterface;
import com.nicolas.shebangscashier.ui.set.printer.PrintContent;
import com.nicolas.toollibrary.HttpHandler;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BarCodePrintViewModel extends ViewModel {
    private MutableLiveData<OperateResult> queryResult;
    private MutableLiveData<OperateResult> printBarCodeResult;
    private List<BarCodeInformation> dataList;

    public BarCodePrintViewModel() {
        this.queryResult = new MutableLiveData<>();
        this.printBarCodeResult = new MutableLiveData<>();
        this.dataList = new ArrayList<>();
    }

    public LiveData<OperateResult> getQueryResult() {
        return queryResult;
    }

    public List<BarCodeInformation> getDataList() {
        return dataList;
    }

    public LiveData<OperateResult> getPrintBarCodeResult() {
        return printBarCodeResult;
    }

    /**
     * 发起调货--查询
     */
    public void queryBarCode(String orderId, String id) {
        CommandVo vo = new CommandVo();
        vo.typeEnum = CommandTypeEnum.COMMAND_MANAGE;
        vo.url = ManageInterface.BarCodeByOrderId;
        vo.contentType = HttpHandler.ContentType_APP;
        vo.requestMode = HttpHandler.RequestMode_POST;
        Map<String, String> parameters = new HashMap<>();
        if (!TextUtils.isEmpty(orderId)) {
            parameters.put("orderId", orderId);
        }
        if (!TextUtils.isEmpty(id)) {
            parameters.put("id", id);
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
                case ManageInterface.BarCodeByOrderId:
                    if (result.success) {
                        try {
                            dataList.clear();
                            //更新数据
                            JSONArray array = new JSONArray(result.data);
                            if (array.length() == 0) {
                                Message msg = new Message();
                                msg.obj = MyApp.getInstance().getString(R.string.noData);
                                queryResult.setValue(new OperateResult(new OperateInUserView(msg)));
                            } else {
                                for (int i = 0; i < array.length(); i++) {
                                    BarCodeInformation item = new BarCodeInformation(array.getString(i));
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

    /**
     * 设置全部选择状态
     *
     * @param isSelect 选择状态
     */
    public void setSelectAll(boolean isSelect) {
        for (BarCodeInformation info : dataList) {
            info.isSelect = isSelect;
        }
    }

    /**
     * 获取选中订单数
     *
     * @return 选中订单的商品总数
     */
    public int getTotalSelectNum() {
        int totalSelectNum = 0;
        for (BarCodeInformation info : dataList) {
            if (info.isSelect) {
                totalSelectNum++;
            }
        }
        return totalSelectNum;
    }

    /**
     * 订单是否全部被选中
     *
     * @return 是否被选中
     */
    public boolean getIsAllSelect() {
        return getTotalSelectNum() == dataList.size();
    }

    /**
     * 打印条码
     */
    public void printBarCode() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    for (BarCodeInformation info : dataList) {
                        if (info.isSelect) {
                            PrinterManager.getInstance().printLabelBlue(PrintContent.getBarcodeLabel(info));
                            try {
                                Thread.sleep(200);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            printBarCodeResult.setValue(new OperateResult(new OperateInUserView(null)));
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            printBarCodeResult.setValue(new OperateResult(new OperateError(-1, e.getMessage(), null)));
                        }
                    });
                }
            }
        }).start();
    }

    /**
     * handler
     */
    private static Handler handler = new Handler();
}
