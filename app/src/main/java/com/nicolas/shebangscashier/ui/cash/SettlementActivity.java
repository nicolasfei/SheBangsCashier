package com.nicolas.shebangscashier.ui.cash;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.donkingliang.groupedadapter.widget.StickyHeaderLayout;
import com.nicolas.shebangscashier.MyActivity;
import com.nicolas.shebangscashier.R;
import com.nicolas.shebangscashier.cashier.MyKeeper;
import com.nicolas.shebangscashier.common.OperateResult;
import com.nicolas.shebangscashier.ui.cash.data.SaleGoodsInformation;
import com.nicolas.shebangscashier.ui.cash.data.SettlementGroupAdapter;
import com.nicolas.shebangscashier.ui.cash.data.SettlementRecyclerViewAdapter;
import com.nicolas.toollibrary.BruceDialog;
import com.nicolas.toollibrary.Tool;
import com.nicolas.toollibrary.Utils;

import java.util.ArrayList;

public class SettlementActivity extends MyActivity implements View.OnClickListener {

    private SettlementRecyclerViewAdapter adapter;
    private SettlementGroupAdapter groupAdapter;
    private SettlementViewModel settlementViewModel;

    private TextView vipPhone, useIntegral, payMethod, cash, actualPayment, change;
    private Button settlement;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale_settlement);
        settlementViewModel = ViewModelProviders.of(this).get(SettlementViewModel.class);
        groupAdapter = new SettlementGroupAdapter(this);

        RecyclerView mRecyclerView = findViewById(R.id.settlementRecyclerView);
        mRecyclerView.setAdapter(groupAdapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutCompat.VERTICAL));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        //吸顶
        StickyHeaderLayout stickyHeaderLayout = findViewById(R.id.stickyHeaderLayout);
        stickyHeaderLayout.setSticky(true);

        vipPhone = findViewById(R.id.vipPhone);
        useIntegral = findViewById(R.id.useIntegral);
        payMethod = findViewById(R.id.payMethod);
        cash = findViewById(R.id.cash);
        vipPhone.setOnClickListener(this);
        useIntegral.setOnClickListener(this);
        payMethod.setOnClickListener(this);
        cash.setOnClickListener(this);

        useIntegral.setClickable(false);    //会员积分，在没有会员的情况不能点击
//        payMethod.setText((SettlementActivity.this.getString(R.string.settlement_pay_case) + ":" + BranchKeeper.getInstance().payCase.getPayment()[0]));    //默认第一个支付方式
//        settlementViewModel.setPayType(BranchKeeper.getInstance().payCase.getPayment()[0]);

        actualPayment = findViewById(R.id.textView5);
        change = findViewById(R.id.textView11);
        settlement = findViewById(R.id.button13);
        settlement.setOnClickListener(this);
        settlement.setClickable(false);

        //设置销售数据转结算数据监听
        settlementViewModel.getSettlementChangeResult().observe(this, new Observer<OperateResult>() {
            @Override
            public void onChanged(OperateResult OperateResult) {
                if (OperateResult.getSuccess() != null) {
                    groupAdapter.setContent(settlementViewModel.getSettlementGroup());
                    groupAdapter.notifyDataSetChanged();
                }
            }
        });

        //设置查询会员信息监听
        settlementViewModel.getVipOperateResult().observe(this, new Observer<OperateResult>() {
            @Override
            public void onChanged(OperateResult operateResult) {
                BruceDialog.dismissProgressDialog();
                if (operateResult.getSuccess() != null) {
                    vipPhone.setText((SettlementActivity.this.getString(R.string.settlement_text_vipId) + ":" + settlementViewModel.getVip().tel));
                    useIntegral.setClickable(true);
                    useIntegral.setCompoundDrawablesRelativeWithIntrinsicBounds(getDrawable(R.drawable.ic_integral), null, getDrawable(R.drawable.ic_navigate_next_grey_24dp), null);
                    if (operateResult.getSuccess().getMessage() != null) {
                        BruceDialog.showPromptDialog(SettlementActivity.this, (String) operateResult.getSuccess().getMessage().obj);
                    }
                }
                if (operateResult.getError() != null) {
                    Utils.toast(SettlementActivity.this, operateResult.getError().getErrorMsg());
                    useIntegral.setClickable(false);
                    useIntegral.setCompoundDrawablesRelativeWithIntrinsicBounds(getDrawable(R.drawable.ic_integral_none), null, getDrawable(R.drawable.ic_navigate_next_grey_24dp), null);
                }
            }
        });

        //销售提交监听
        settlementViewModel.getSettlementResult().observe(this, new Observer<OperateResult>() {
            @Override
            public void onChanged(OperateResult OperateResult) {
                BruceDialog.dismissProgressDialog();
                if (OperateResult.getSuccess() != null) {
                    jumpToPrintBillActivity();
                    Utils.toast(SettlementActivity.this, R.string.sale_success);
                }
                if (OperateResult.getError() != null) {
                    Utils.toast(SettlementActivity.this, OperateResult.getError().getErrorMsg());
                }
            }
        });

        //设置积分兑换监听
        settlementViewModel.getExchangeIntegralResult().observe(this, new Observer<OperateResult>() {
            @Override
            public void onChanged(OperateResult OperateResult) {
                if (OperateResult.getSuccess() != null) {
                    useIntegral.setText((SettlementActivity.this.getString(R.string.settlement_text_integral) + ":" + settlementViewModel.getUseIntegral() + "(" + getString(R.string.vip_integral_deduction) + settlementViewModel.getUseIntegralDeduction() + ")"));
                }
                if (OperateResult.getError() != null) {
                    Utils.toast(SettlementActivity.this, OperateResult.getError().getErrorMsg());
                }
            }
        });

        //设置结算收银监听
        settlementViewModel.getActualPaymentResult().observe(this, new Observer<OperateResult>() {
            @Override
            public void onChanged(OperateResult OperateResult) {
                if (OperateResult.getSuccess() != null) {
                    cash.setText((SettlementActivity.this.getString(R.string.settlement_settlement_cash) + ":" +
                            (SettlementActivity.this.getString(R.string.money) + settlementViewModel.getActualPayment())));
                    actualPayment.setText((SettlementActivity.this.getString(R.string.settlement_text_payment) +
                            (SettlementActivity.this.getString(R.string.money) + settlementViewModel.getActualPayment())));
                    change.setText((SettlementActivity.this.getString(R.string.settlement_text_change) +
                            (SettlementActivity.this.getString(R.string.money) + settlementViewModel.getChange())));
                    if (!settlement.isClickable()) {
                        settlement.setBackground(getDrawable(R.drawable.shape_rectangle_red));
                        settlement.setClickable(true);
                    }
                }
                if (OperateResult.getError() != null) {
                    if (settlement.isClickable()) {
                        settlement.setBackground(getDrawable(R.drawable.shape_rectangle_grey));
                        settlement.setClickable(false);
                    }
                    Utils.toast(SettlementActivity.this, OperateResult.getError().getErrorMsg());
                }
            }
        });

        //销售数据转换
        ArrayList<SaleGoodsInformation> list = getIntent().getParcelableArrayListExtra("groupMsgs");
        settlementViewModel.transformSaleGoodsInformation(list);
    }

    /**
     * 调转到结算明细对话框
     */
    private void jumpToPrintBillActivity() {
        Bundle bundle = settlementViewModel.getSubmitSaleData();
        Intent intent = new Intent(SettlementActivity.this, SettlementDetailedActivity.class);
        intent.putExtra("sale", bundle);
        startActivityForResult(intent, 3);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != 3) {
            return;
        }
        switch (resultCode) {
            case 0:
                break;
            case 1:
                setResult(1);
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.vipPhone:
                showInputVipDialog();
                break;
            case R.id.useIntegral:
                showUseVipIntegralDialog();
                break;
            case R.id.payMethod:
                showPayMethodDialog();
                break;
            case R.id.cash:
                showCashDialog();
                break;
            case R.id.button13:     //提交收银
                BruceDialog.showProgressDialog(SettlementActivity.this, getString(R.string.sale_submitting));
                settlementViewModel.submitSale();
                break;
            default:
                break;
        }
    }

    /**
     * 支付对话框
     */
    private void showCashDialog() {
        BruceDialog.showEditInputDialog(R.string.settlement_text_payment, R.string.input_settlement_cash, InputType.TYPE_CLASS_PHONE,
                SettlementActivity.this, new BruceDialog.OnInputFinishListener() {
                    @Override
                    public void onInputFinish(String itemName) {
                        if (!TextUtils.isEmpty(itemName)) {
                            settlementViewModel.setActualPayment(Float.parseFloat(itemName));
                        }
                    }
                });
    }

    /**
     * 支付方式选择对话框
     */
    private void showPayMethodDialog() {
        BruceDialog.showSingleChoiceDialog(R.string.settlement_pay_case, this, MyKeeper.getInstance().getPayment(),
                new BruceDialog.OnChoiceItemListener() {
                    @Override
                    public void onChoiceItem(String itemName) {
                        settlementViewModel.setPayType(itemName);
                        payMethod.setText((SettlementActivity.this.getString(R.string.settlement_pay_case) + ":" + itemName));
                    }
                });
    }

    /**
     * 会员积分对话框
     */
    private void showUseVipIntegralDialog() {
        BruceDialog.showEditInputDialog(this.getString(R.string.settlement_text_integral), "当前可兑换积分为" + settlementViewModel.getVip().integral + ","+
        this.getString(R.string.input_exchange_integral), InputType.TYPE_CLASS_NUMBER,
                SettlementActivity.this, new BruceDialog.OnInputFinishListener() {
            @Override
            public void onInputFinish(String itemName) {
                if (!TextUtils.isEmpty(itemName)) {
                    settlementViewModel.setUseIntegral(Integer.parseInt(itemName));
                }
            }
        });
    }

    /**
     * Vip电话输入dialog
     */
    private void showInputVipDialog() {
        BruceDialog.showEditInputDialog(R.string.settlement_text_vipId, R.string.input_settlement_phone, InputType.TYPE_CLASS_PHONE,
                SettlementActivity.this, new BruceDialog.OnInputFinishListener() {
                    @Override
                    public void onInputFinish(String itemName) {
                        if (!TextUtils.isEmpty(itemName)) {
                            queryVipInformation(itemName);
                        }
                    }
                });
    }

    /**
     * 查询会员信息
     *
     * @param tel 会员电话号码
     */
    private void queryVipInformation(String tel) {
        if (Tool.isTelephoneNumber(tel)) {
            BruceDialog.showProgressDialog(this, getString(R.string.querying));
            settlementViewModel.queryVipInformation(tel);
        } else {
            BruceDialog.showWarningDialog(this, R.string.notTelIgnore, new BruceDialog.OnWarningIgnoreListener() {
                @Override
                public void onWarningIgnore() {
                    BruceDialog.showProgressDialog(SettlementActivity.this, getString(R.string.querying));
                    settlementViewModel.queryVipInformation(tel);
                }
            });
        }
    }
}
