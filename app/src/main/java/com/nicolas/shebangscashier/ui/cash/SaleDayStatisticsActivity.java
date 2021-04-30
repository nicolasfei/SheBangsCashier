package com.nicolas.shebangscashier.ui.cash;

import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.nicolas.componentlibrary.datetimepicker.DateTimePickerDialog;
import com.nicolas.componentlibrary.pullrefresh.PullRefreshListView;
import com.nicolas.shebangscashier.BaseActivity;
import com.nicolas.shebangscashier.R;
import com.nicolas.shebangscashier.common.DayStatisticsAdapter;
import com.nicolas.shebangscashier.common.OperateResult;
import com.nicolas.toollibrary.BruceDialog;

public class SaleDayStatisticsActivity extends BaseActivity implements View.OnClickListener {

    private SaleDayStatisticsViewModel viewModel;
    private TextView numCount, cashCount, integral, deduction;
    private Button print;
    private DayStatisticsAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sale_day_statistics);
        this.viewModel = new ViewModelProvider(this).get(SaleDayStatisticsViewModel.class);

        findClickView(R.id.date);
        findClickView(R.id.query);
        this.print = findClickView(R.id.print);

        this.numCount = findViewById(R.id.text1);
        this.cashCount = findViewById(R.id.text3);
        this.integral = findViewById(R.id.text2);
        this.deduction = findViewById(R.id.text4);

        this.adapter = new DayStatisticsAdapter(this, viewModel.getStatisticsList());
        PullRefreshListView listView = findViewById(R.id.listView);
        listView.setAdapter(this.adapter);
        listView.disablePushLoadMore();
        listView.enablePullRefresh();
        listView.setOnRefreshListener(new PullRefreshListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                viewModel.query();
            }
        });
        View head = LayoutInflater.from(this).inflate(R.layout.sale_statistics_item, null, false);
        TextView v1 = head.findViewById(R.id.item1);
        v1.setText("商品类别");
        TextView v2 = head.findViewById(R.id.item2);
        v2.setText("数量");
        TextView v3 = head.findViewById(R.id.item3);
        v3.setText("总价");
        listView.addHeaderView(head);

        //监听查询结果
        this.viewModel.getQueryResult().observe(this, new Observer<OperateResult>() {
            @Override
            public void onChanged(OperateResult operateResult) {
                if (operateResult.getSuccess() != null) {
                    updateDate();
                }
                if (operateResult.getError() != null) {
                    BruceDialog.showPromptDialog(SaleDayStatisticsActivity.this, operateResult.getError().getErrorMsg());
                }

                updateDate();
                dismissProgressDialog();
                if (listView.isPullToRefreshing()) {
                    listView.refreshFinish();
                }
                listView.updateContentDate(viewModel.getSaleTime());
            }
        });

        //默认查询今天的销售统计
        queryStatistics();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.date:
                DateTimePickerDialog.showDatePickerDialog(this, viewModel.getSaleTime(), new DateTimePickerDialog.OnDateTimePickListener() {
                    @Override
                    public void OnDateTimePick(String dateTime) {
                        if (!TextUtils.isEmpty(dateTime)) {
                            viewModel.setSaleTime(dateTime);
                        }
                    }
                });
                break;
            case R.id.query:
                queryStatistics();
                break;
            case R.id.print:
                printStatistics();
                break;
        }
    }

    /**
     * 打印统计
     */
    private void printStatistics() {

    }

    /**
     * 查询统计
     */
    private void queryStatistics() {
        showProgressDialog(getString(R.string.querying));
        viewModel.query();
    }

    private void updateDate() {
        //数量合计
        String numCountValue = getString(R.string.num_count) + getString(R.string.colon_zh) + viewModel.getGoodsNumCount();
        this.numCount.setText(numCountValue);

        //收款合计
        String cashCountValue = getString(R.string.total_cash) + getString(R.string.colon_zh) + viewModel.getGoodsCashCount();
        this.cashCount.setText(cashCountValue);

        //积分使用
        String integralValue = getString(R.string.intergeralUse) + getString(R.string.colon_zh) + viewModel.getGoodsIntegralCount();
        this.integral.setText(integralValue);

        //抵扣金额
        String deductionValue = getString(R.string.intergeralDeduction) + getString(R.string.colon_zh) + viewModel.getGoodsDeductionCount();
        this.deduction.setText(deductionValue);

        this.adapter.notifyDataSetChanged();

        if (viewModel.getStatisticsList().size() == 0) {
            if (print.isClickable()) {
                print.setBackground(getDrawable(R.drawable.shape_rectangle_grey));
                print.setClickable(false);
            }
        } else {
            if (!print.isClickable()) {
                print.setBackground(getDrawable(R.drawable.shape_rectangle_red));
                print.setClickable(true);
            }
        }
    }
}
