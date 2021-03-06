package com.nicolas.shebangscashier.ui.cash;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.nicolas.shebangscashier.MyActivity;
import com.nicolas.shebangscashier.R;
import com.nicolas.shebangscashier.app.MyApp;
import com.nicolas.shebangscashier.cashier.MyKeeper;
import com.nicolas.shebangscashier.common.OperateResult;
import com.nicolas.shebangscashier.data.vip.Vip;
import com.nicolas.shebangscashier.ui.cash.data.SaleGoodsInformationAdapter;
import com.nicolas.shebangscashier.ui.set.printer.PrinterActivity;
import com.nicolas.toollibrary.Utils;

public class SettlementDetailedActivity extends MyActivity {

    private SettlementDetailedViewModel detailedViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale_settlement_detailed);
        detailedViewModel = new ViewModelProvider(this).get(SettlementDetailedViewModel.class);
        Intent intent = getIntent();
        if (intent != null) {
            Bundle bundle = intent.getBundleExtra("sale");
            if (bundle != null) {
                detailedViewModel.setSaleData(bundle);
            }
        }

        //分店信息
        TextView branch = findViewById(R.id.branch);
        String branchValue = getString(R.string.branch) + "\u3000\u3000\u3000\u3000" +
                "<font color=\"black\"><big>" + MyKeeper.getInstance().getBranch().name + "</big></font>";
        branch.setText(Html.fromHtml(branchValue, Html.FROM_HTML_MODE_COMPACT));

        //收银员
        TextView cashier = findViewById(R.id.cashier);
        String cashierValue = getString(R.string.cashier) + "\u3000\u3000\u3000" +
                "<font color=\"black\"><big>" + MyKeeper.getInstance().getStaff().name + "</big></font>";
        cashier.setText(Html.fromHtml(cashierValue, Html.FROM_HTML_MODE_COMPACT));

        //合计
        TextView total = findViewById(R.id.total);
        String totalValue = getString(R.string.total) + MyApp.getInstance().getString(R.string.colon) + detailedViewModel.goodsInformationList.size() + "\u3000" +
                "<font color=\"#FF5722\">" + getString(R.string.total_price) + MyApp.getInstance().getString(R.string.colon) + detailedViewModel.totalCast + "</font>";
        total.setText(Html.fromHtml(totalValue, Html.FROM_HTML_MODE_COMPACT));

        //商品list
        SwipeMenuListView saleList = findViewById(R.id.sale_list_view);
        SaleGoodsInformationAdapter goodsAdapter = new SaleGoodsInformationAdapter(this, detailedViewModel.goodsInformationList);
        saleList.setAdapter(goodsAdapter);

        //会员信息
        Vip vip = detailedViewModel.vip;
        TextView vipName = findViewById(R.id.vip);
        TextView thisIntegral = findViewById(R.id.thisIntegral);
        TextView consumeIntegral = findViewById(R.id.consumeIntegral);
        TextView residualIntegral = findViewById(R.id.residualIntegral);
        if (vip != null) {
            String vipNameS = getString(R.string.vip) + "\u3000\u3000\u3000\u3000" +
                    "<font color=\"black\">" + vip.name + "</font>";
            vipName.setText(Html.fromHtml(vipNameS, Html.FROM_HTML_MODE_COMPACT));

            String thisIntegralS = getString(R.string.this_integral) + "\t\t\t\t" +
                    "<font color=\"black\">" + detailedViewModel.saleIntegral + "</font>";
            thisIntegral.setText(Html.fromHtml(thisIntegralS, Html.FROM_HTML_MODE_COMPACT));

            String consumeIntegralS = getString(R.string.consume_integral) + "\t\t\t\t" +
                    "<font color=\"black\">" + detailedViewModel.useIntegral + "</font>";
            consumeIntegral.setText(Html.fromHtml(consumeIntegralS, Html.FROM_HTML_MODE_COMPACT));

            String residualIntegralS = getString(R.string.residual_integral) + "\t\t\t\t" +
                    "<font color=\"black\">" + detailedViewModel.surplusIntegral + "</font>";
            residualIntegral.setText(Html.fromHtml(residualIntegralS, Html.FROM_HTML_MODE_COMPACT));
        } else {
            vipName.setText(getString(R.string.vip));
            thisIntegral.setText(getString(R.string.this_integral));
            consumeIntegral.setText(getString(R.string.consume_integral));
            residualIntegral.setText(getString(R.string.residual_integral));
        }

        //收款金额
        TextView collection = findViewById(R.id.cash1);
        String collectionS = getString(R.string.collection) + "\u3000\u3000" +
                "<font color=\"black\">" + getString(R.string.money) + detailedViewModel.collection + "</font>" + "\u3000\u3000\u3000" +
                //积分抵扣
                getString(R.string.deduction) + "\u3000\u3000" +
                "<font color=\"blue\">" + getString(R.string.money) + detailedViewModel.deduction + "</font>";
        collection.setText(Html.fromHtml(collectionS, Html.FROM_HTML_MODE_COMPACT));

        //应收金额
        TextView receivable = findViewById(R.id.cash2);
        String receivableS = getString(R.string.receivable) + "\u3000\u3000" +
                "<font color=\"#FF5722\">" + getString(R.string.money) + detailedViewModel.receivable + "</font>";
        receivable.setText(Html.fromHtml(receivableS, Html.FROM_HTML_MODE_COMPACT));

        //收款合计
        TextView totalCash = findViewById(R.id.cash3);
        String totalS = getString(R.string.total_cash) + "\u3000\u3000" +
                "<font color=\"black\">" + getString(R.string.money) + detailedViewModel.collection + "</font>" + "\u3000\u3000\u3000" +
                //找零
                getString(R.string.settlement_text_change) + "\u3000\u3000\u3000\u3000\u3000" +
                "<font color=\"red\">" + getString(R.string.money) + detailedViewModel.change + "</font>";
        total.setText(Html.fromHtml(totalS, Html.FROM_HTML_MODE_COMPACT));

        Button print = findViewById(R.id.print);
        print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detailedViewModel.printerSaleBill();
            }
        });

        //打印小票结果
        detailedViewModel.getPrintSaleBillResult().observe(this, new Observer<OperateResult>() {
            @Override
            public void onChanged(OperateResult operateResult) {
                if (operateResult.getSuccess() != null) {
                    Utils.toast(SettlementDetailedActivity.this, getString(R.string.print_success));
                    showSuccessAndFinishDialog();
                }
                if (operateResult.getError() != null) {
                    Utils.toast(SettlementDetailedActivity.this, operateResult.getError().getErrorMsg());
                    //调整到蓝牙打印设置页面
                    Intent intent = new Intent(SettlementDetailedActivity.this, PrinterActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    /**
     * 显示结算成功对话框
     */
    private void showSuccessAndFinishDialog() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.settlement_success))
                .setMessage(getString(R.string.settlement_success_finish))
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SettlementDetailedActivity.this.setResult(1);
                        SettlementDetailedActivity.this.finish();
                    }
                })
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .create().show();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                setResult(0);
                finish();
                break;
            default:
                break;
        }
        return true;
    }
}
