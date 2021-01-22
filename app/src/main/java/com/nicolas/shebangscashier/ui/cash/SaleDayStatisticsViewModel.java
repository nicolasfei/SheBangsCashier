package com.nicolas.shebangscashier.ui.cash;

import android.os.Message;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.nicolas.shebangscashier.R;
import com.nicolas.shebangscashier.app.MyApp;
import com.nicolas.shebangscashier.common.DayStatistics;
import com.nicolas.shebangscashier.common.OperateError;
import com.nicolas.shebangscashier.common.OperateInUserView;
import com.nicolas.shebangscashier.common.OperateResult;
import com.nicolas.shebangscashier.communication.CommandResponse;
import com.nicolas.shebangscashier.communication.CommandTypeEnum;
import com.nicolas.shebangscashier.communication.CommandVo;
import com.nicolas.shebangscashier.communication.Invoker;
import com.nicolas.shebangscashier.communication.sale.SaleInterface;
import com.nicolas.toollibrary.HttpHandler;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class SaleDayStatisticsViewModel extends ViewModel {

    //查询条件
    private String saleTime;

    private List<DayStatistics> statistics;
    private MutableLiveData<OperateResult> queryResult;

    private int integralMoney = 0;
    private int integral = 0;

    public SaleDayStatisticsViewModel() {
        this.saleTime = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).format(new Date());
        ;
        this.statistics = new ArrayList<>();
        this.queryResult = new MutableLiveData<>();
    }

    public LiveData<OperateResult> getQueryResult() {
        return queryResult;
    }

    public String getSaleTime() {
        return saleTime;
    }

    public void setSaleTime(String saleTime) {
        this.saleTime = saleTime;
    }

    public String getGoodsNumCount() {
        int count = 0;
        for (DayStatistics d : statistics) {
            count += d.saleNum;
        }
        return String.valueOf(count);
    }

    public String getGoodsCashCount() {
        float count = 0;
        for (DayStatistics d : statistics) {
            count += d.saleTotalPrice;
        }
        return String.valueOf(count);
    }

    public String getGoodsIntegralCount() {
        return String.valueOf(integral);
    }

    public String getGoodsDeductionCount() {
        return String.valueOf(integralMoney);
    }

    public List<DayStatistics> getStatisticsList() {
        return statistics;
    }

    public void query() {
        CommandVo vo = new CommandVo();
        vo.typeEnum = CommandTypeEnum.COMMAND_SALE;
        vo.url = SaleInterface.SaleByEverDay;
        vo.contentType = HttpHandler.ContentType_APP;
        vo.requestMode = HttpHandler.RequestMode_POST;
        Map<String, String> parameters = new HashMap<>();
        parameters.put("saleTime", saleTime);
        vo.parameters = parameters;
        Invoker.getInstance().setOnEchoResultCallback(this.callback);
        Invoker.getInstance().exec(vo);
    }

    private Invoker.OnExecResultCallback callback = new Invoker.OnExecResultCallback() {
        @Override
        public void execResult(CommandResponse result) {
            switch (result.url) {
                case SaleInterface.SaleByEverDay:
                    if (result.success) {
                        try {
                            //更新数据
                            statistics.clear();     //先清空
                            integral = 0;
                            integralMoney = 0;
                            JSONArray array = new JSONArray(result.data);
                            if (array.length() == 0) {
                                Message msg = new Message();
                                msg.obj = MyApp.getInstance().getString(R.string.noData);
                                queryResult.setValue(new OperateResult(new OperateInUserView(msg)));
                            } else {
                                for (int i = 0; i < array.length(); i++) {
                                    DayStatistics d = new DayStatistics(array.getString(i));
                                    statistics.add(d);
                                }
                                integralMoney = result.integralMoney;
                                integral = result.integral;
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
}
