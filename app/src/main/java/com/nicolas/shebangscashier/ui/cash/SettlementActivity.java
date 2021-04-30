package com.nicolas.shebangscashier.ui.cash;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class SettlementActivity extends MyActivity implements View.OnClickListener {
    private static final String TAG = "SettlementActivity";
    private static final int NH_REQUEST_CODE = 1;
    private static final int BILL_REQUEST_CODE = 3;

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
        updatePayMethod(settlementViewModel.getPayType());
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
                }
                if (OperateResult.getError() != null) {
                    Utils.toast(SettlementActivity.this, OperateResult.getError().getErrorMsg());
                }
                updateSettlementStatus();
            }
        });

        //销售数据转换
        ArrayList<SaleGoodsInformation> list = getIntent().getParcelableArrayListExtra("groupMsgs");
        settlementViewModel.transformSaleGoodsInformation(list);
    }

    /**
     * 可以结算状态更新
     * 结算方式和实付金额必须输入
     */
    private void updateSettlementStatus() {
        if (TextUtils.isEmpty(settlementViewModel.getPayType()) || !settlementViewModel.isSetActualPayment()) {
            if (settlement.isClickable()) {
                settlement.setBackground(getDrawable(R.drawable.shape_rectangle_grey));
                settlement.setClickable(false);
            }
        } else {
            if (!settlement.isClickable()) {
                settlement.setBackground(getDrawable(R.drawable.shape_rectangle_red));
                settlement.setClickable(true);
            }
        }
    }

    /**
     * 调转到结算明细对话框
     */
    private void jumpToPrintBillActivity() {
        Bundle bundle = settlementViewModel.getSubmitSaleData();
        Intent intent = new Intent(SettlementActivity.this, SettlementDetailedActivity.class);
        intent.putExtra("sale", bundle);
        startActivityForResult(intent, BILL_REQUEST_CODE);
    }

    /**
     * 跳转到支付页面
     *
     * @param payType 支付方式
     * @param money   须支付金额
     */
    private void JumpToCashActivity(String payType, float money) {
        Intent intent = new Intent();
        intent.setClassName("com.comlink.mispos", "com.comlink.mispos.activity.TransActivity");
        Bundle bundle = new Bundle();
        try {
            JSONObject object = new JSONObject();
            object.put("posNo", MyKeeper.getInstance().getBranch().fId);                   //设备号
            object.put("tellerNo", TextUtils.isEmpty(MyKeeper.getInstance().getStaff().idCard) ? "123456"
                    : MyKeeper.getInstance().getStaff().idCard);                 //操作员号--电话号码
            /**
             * 为C，表示是正数（消费交易）
             * 为D，表示是负数（撤销交易）
             * 为R，表示是负数（退货交易）
             * 为I，金额无效（查询余额交易）
             * 为N，金额无效（签到交易）
             * 为S，金额无效（结算交易）
             * 为T，金额无效（成功交易补录）
             * 为P，金额无效（补打签购单）
             * 为G，表示扫码交易消费订单查询
             * 为Z，表示扫码交易撤销订单查询
             * 为J，表示扫码交易退货订单查询
             * 为0[零]，表示（结帐、交易一览等管理类交易）
             */
            object.put("tranType", "C");                                //交易类型标志--
            DecimalFormat df = new DecimalFormat("000000000000");
            object.put("tranAmount", df.format((long) (money * 100)));          //金额--12位，以分为单位，不足左补‘0’
            /**
             * 左靠，右补空格；
             * 传入收银系统所选择的支付方式：
             * 001：银行卡
             * 002: 自有预付卡
             * 003：第三方预付卡
             * 004：积分兑换
             * 005：O2O支付
             * 006: DCC
             * 007: 银行卡扫码支付
             * 008：数字货币
             * 009：刷脸支付
             */
            String t;
            switch (payType) {
                case "刷卡":
                    t = "001";
                    break;
                case "支付宝":
                case "微信":
                default:
                    t = "005";
                    break;
            }
            object.put("payType", t);              //支付方式
            object.put("invoiceNo", "");    //票据号---该字段为消费成功后返回的票据号，在做撤消、退货或补打交易时,需要传入原消费交易的票据号，MIS从保存的消费成功信息中获取，如果MIS没有保存票据号则传空格
            object.put("authNo", "");       //授权号---该字段为消费成功后返回的授权号，在做退货交易时,需要传入原消费交易的授权号，MIS从保存的消费成功信息中获取，如果MIS没有保存授权号则传空格
            object.put("traceNo", "");      //凭证号---该字段为消费成功后返回的凭证号，在做退货交易时,需要传入原消费交易的授权号，MIS从保存的消费成功信息中获取，如果MIS没有保存授权号则传空格
            object.put("batchNo", "");      //批次号---该字段为消费成功后返回的批次号，在做退货交易时,需要传入原消费交易的批次号，MIS从保存的消费成功信息中获取，如果MIS没有保存批次号则传空格
            object.put("tranDate", "");     //原交易日期时间---该字段为消费成功后返回的日期时间，在做退货交易时,需要传入原消费交易的日期时间，MIS从保存的消费成功信息中获取，如果MIS没有保存日期时间则传空格
            object.put("refNo", "");        //参考号---该字段为消费成功后返回的参考号，在做退货交易时,需要传入原消费交易的参考号，MIS从保存的消费成功信息中获取，如果MIS没有保存参考号则传空格
            object.put("scanNo", "");       //扫码信息--扫码支付交易MIS可以传入扫码信息，条码或者二维码，可以不填
            object.put("merTradeNo", "");   //扫码支付商户订单号--扫码支付交易原订单号（扫码消费订单查询G、扫码撤销D、扫码撤销订单查询Z、扫码退货R填原消费交易返回订单号，退货订单查询J填退货交易返回订单号）
            object.put("misOrderNo", "");   //MIS交易订单号---BankPos系统保存该交易订单号（可以不填）
            object.put("voucher", settlementViewModel.getSaleVoucher());     //每笔交易的订单号，必须唯一
            object.put("orderNo", "");
            object.put("cardNo", "");
            object.put("expDate", "");
            bundle.putString("in", object.toString());
            Log.d(TAG, "JumpToCashActivity: " + object.toString());
            intent.putExtras(bundle);
            try {
                startActivityForResult(intent, NH_REQUEST_CODE);
            } catch (ActivityNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(SettlementActivity.this, "MisPOS 未安装 ", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(SettlementActivity.this, "json error", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        String ret;
        switch (requestCode) {
            case BILL_REQUEST_CODE:         //明细返回
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
                break;
            case NH_REQUEST_CODE:           //农行返回结果
                switch (resultCode) {
                    case RESULT_OK:         // 成功处理
                        if (data != null && data.getExtras() != null) {
                            ret = data.getExtras().getString("out");
                            Log.d(TAG, "onActivityResult: 交易：" + ret);
                            successfulTrade();
                        }
                        break;
                    case RESULT_FIRST_USER: // 出错处理
                        if (data != null) {
                            ret = data.getExtras().getString("out");
                            Log.d(TAG, "onActivityResult: RESULT_FIRST_USER " + ret);
                            Toast.makeText(this, "支付失败：" + ret, Toast.LENGTH_SHORT).show();
                        }
                        break;
                    default:
                        break;
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 支付成功
     */
    private void successfulTrade() {
        Toast.makeText(this, "支付成功", Toast.LENGTH_SHORT).show();
        submit2Service();
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
                cashSubmit();
                break;
            default:
                break;
        }
    }

    private void cashSubmit() {
        if (!settlementViewModel.getPayType().equals(MyKeeper.getInstance().getPayment()[2])) {                     //除现金外的其他收银方式
            JumpToCashActivity(settlementViewModel.getPayType(), settlementViewModel.getSaleTotal());
        } else {
            submit2Service();
        }
    }

    private void submit2Service() {
        BruceDialog.showProgressDialog(SettlementActivity.this, getString(R.string.sale_submitting));
        settlementViewModel.submitSale();
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
                        updatePayMethod(itemName);
                        updateSettlementStatus();
                    }
                });
    }

    /**
     * 更新支付方式
     *
     * @param method 支付方式
     */
    private void updatePayMethod(String method) {
        payMethod.setText((SettlementActivity.this.getString(R.string.settlement_pay_case) + ":" + method));
    }

    /**
     * 会员积分对话框
     */
    private void showUseVipIntegralDialog() {
        BruceDialog.showEditInputDialog(this.getString(R.string.settlement_text_integral), "当前可兑换积分为" + settlementViewModel.getVip().integral + "," +
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
