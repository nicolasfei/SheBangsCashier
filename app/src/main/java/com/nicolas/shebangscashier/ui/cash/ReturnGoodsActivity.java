package com.nicolas.shebangscashier.ui.cash;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.nicolas.printerlibraryforufovo.PrinterManager;
import com.nicolas.scannerlibrary.CameraScanner;
import com.nicolas.shebangscashier.BaseActivity;
import com.nicolas.shebangscashier.R;
import com.nicolas.shebangscashier.ScanActivity;
import com.nicolas.shebangscashier.cashier.MyKeeper;
import com.nicolas.shebangscashier.common.OperateResult;
import com.nicolas.shebangscashier.common.ReturnGoodsAdapter;
import com.nicolas.shebangscashier.ui.set.printer.PrintContent;
import com.nicolas.toollibrary.BruceDialog;
import com.nicolas.toollibrary.Tool;

public class ReturnGoodsActivity extends BaseActivity implements View.OnClickListener {

    private ReturnGoodsViewModel viewModel;

    private EditText codeInput;             //条码输入
    private TextView user;                  //收银员

    private TextView vipPhone;              //会员手机
    private TextView vipIntegral;           //会员积分
    private TextView deductIntegral;        //扣除积分
    private TextView integralBalance;       //积分差额
    private TextView refundDeduction;       //退款减扣

    private TextView numCount;              //数量合计
    private TextView refundCount;           //退款合计

    private Button goodsBack;               //退货按钮

    private ReturnGoodsAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_return_goods);

        this.viewModel = new ViewModelProvider(this).get(ReturnGoodsViewModel.class);

        this.vipPhone = findViewById(R.id.vipPhone);
        this.vipIntegral = findViewById(R.id.vipIntegral);
        this.deductIntegral = findViewById(R.id.deductIntegral);
        this.integralBalance = findViewById(R.id.integralBalance);
        this.refundDeduction = findViewById(R.id.refundDeduction);
        this.numCount = findViewById(R.id.numCount);
        this.refundCount = findViewById(R.id.refundCount);

        //扫描按钮
        findClickView(R.id.scan);
        this.codeInput = findViewById(R.id.code);
        this.codeInput.requestFocus();
        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                viewModel.codeDataChanged(codeInput.getText().toString());
            }
        };
        this.codeInput.addTextChangedListener(afterTextChangedListener);
        this.codeInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT) {
                    handlerManualCodeInput(codeInput.getText().toString());
                }
                return false;
            }
        });

        goodsBack = findClickView(R.id.goodsBack);

        SwipeMenuListView listView = findViewById(R.id.listView);
        this.adapter = new ReturnGoodsAdapter(this, viewModel.getInformations());
        listView.setAdapter(this.adapter);
        //左滑菜单
        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                // create "look" item
                SwipeMenuItem detailsItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                detailsItem.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9,
                        0xCE)));
                // set item width
                detailsItem.setWidth(Tool.dp2px(ReturnGoodsActivity.this, 90));
                // set item title
                detailsItem.setTitle(getString(R.string.details));
                // set item title fontsize
                detailsItem.setTitleSize(18);
                // set item title font color
                detailsItem.setTitleColor(Color.WHITE);
                // add to menu
//                menu.addMenuItem(detailsItem);

                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                deleteItem.setBackground(new ColorDrawable(Color.RED));
                // set item width
                deleteItem.setWidth(Tool.dp2px(ReturnGoodsActivity.this, 90));
                // set item title
                deleteItem.setTitle(getString(R.string.delete));
                // set item title fontsize
                deleteItem.setTitleSize(18);
                // set item title font color
                deleteItem.setTitleColor(Color.WHITE);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };
        //添加左滑菜单
        listView.setMenuCreator(creator);
        listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        deleteGoods(position);
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

        //收银员
        user = findClickView(R.id.user);
        updateUser();

        //查询监听
        this.viewModel.getQueryGoodsInformationResult().observe(this, new Observer<OperateResult>() {
            @Override
            public void onChanged(OperateResult operateResult) {
                dismissProgressDialog();
                if (operateResult.getSuccess()!=null){
                    adapter.notifyDataSetChanged();
                    updateGoodsStatisticsData();
                }
                if (operateResult.getError()!=null){
                    BruceDialog.showPromptDialog(ReturnGoodsActivity.this, operateResult.getError().getErrorMsg());
                }
            }
        });

        //退货监听
        this.viewModel.getReturnGoodsResult().observe(this, new Observer<OperateResult>() {
            @Override
            public void onChanged(OperateResult operateResult) {
                dismissProgressDialog();
                if (operateResult.getSuccess()!=null){
                    showPrintDialog();
                }
                if (operateResult.getError()!=null){
                    BruceDialog.showPromptDialog(ReturnGoodsActivity.this, operateResult.getError().getErrorMsg());
                }
            }
        });

        updateGoodsStatisticsData();
    }

    /**
     * 是否打印小票对话框
     */
    private void showPrintDialog() {
        new AlertDialog.Builder(this)
                .setTitle("打印小票")
                .setMessage("是否打印退货小票？")
                .setNegativeButton("不打印", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        goodsBackSuccess();
                    }
                })
                .setPositiveButton("打印", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        PrinterManager.getInstance().printPosBill(PrintContent.getBackReceipt(ReturnGoodsActivity.this, viewModel.getInformations()));
                        goodsBackSuccess();
                    }
                })
                .setCancelable(false)
                .create().show();

    }

    /**
     * 退货成功
     */
    private void goodsBackSuccess(){
        viewModel.removeAllGoods();
        adapter.notifyDataSetChanged();
        updateGoodsStatisticsData();
    }

    /**
     * 删除商品
     *
     * @param position position
     */
    private void deleteGoods(int position) {
        showProgressDialog("删除中...");
        viewModel.removeGoods(position);
    }

    /**
     * 更新统计数据
     */
    private void updateGoodsStatisticsData() {
        updateDeductIntegral();
        updateIntegralBalance();
        updateRefundDeduction();
        updateCount();
    }


    /**
     * 处理扫描结果--广播方式
     *
     * @param scan scan
     */
    private void handlerScanResultInBroadcast(String scan) {
        codeInput.setText(scan);
        queryCodeInformation(scan);
    }

    /**
     * 处理扫描结果--按键监听方式
     *
     * @param scan 扫描结果
     */
    private void handlerScanResultInKeyListen(String scan) {
        codeInput.setText(scan);
        queryCodeInformation(scan);
    }

    /**
     * 手动输入条码处理
     *
     * @param code 条码
     */
    private void handlerManualCodeInput(String code) {
        Tool.hideSoftInput(this);
        queryCodeInformation(code);
    }

    /**
     * 查询销售商品信息
     *
     * @param goodsCode 商品条码
     */
    private void queryCodeInformation(String goodsCode) {
        showProgressDialog(getString(R.string.query_goods));
        viewModel.queryGoodsInformation(goodsCode);
    }

    /**
     * 更新收银员
     */
    private void updateUser() {
        this.user.setText(MyKeeper.getInstance().getStaff().name);
    }

    /**
     * 更新会员手机
     */
    private void updateVipPhone() {
        this.vipPhone.setText(("会员手机：" + viewModel.getVipPhone()));
    }

    /**
     * 更新会员积分
     */
    private void updateVipIntegral() {
        this.vipIntegral.setText(("会员积分：" + viewModel.getVipIntegral()));
    }

    /**
     * 更新积分差额
     */
    private void updateIntegralBalance() {
        this.integralBalance.setText(("积分差额：" + viewModel.getIntegralBalance()));
    }

    /**
     * 更新扣除积分
     */
    private void updateDeductIntegral() {
        this.deductIntegral.setText(("扣除积分：" + viewModel.getDeductIntegral()));
    }

    /**
     * 更新退款减扣
     */
    private void updateRefundDeduction() {
        this.refundDeduction.setText(("退款减扣：" + viewModel.getRefundDeduction()));
    }

    /**
     * 更新合计
     */
    private void updateCount() {
        this.numCount.setText(("数量合计：" + viewModel.getNumCount()));
        this.refundCount.setText(("退款合计：" + viewModel.getRefundCount()));
        if (viewModel.getNumCount() == 0) {
            if (goodsBack.isClickable()) {
                goodsBack.setBackground(getDrawable(R.drawable.shape_rectangle_grey));
                goodsBack.setClickable(false);
            }
        } else {
            if (!goodsBack.isClickable()) {
                goodsBack.setBackground(getDrawable(R.drawable.shape_rectangle_red));
                goodsBack.setClickable(true);
            }
        }
    }

    /**
     * 收银员切换
     */
    private void showUserDialog() {
        BruceDialog.showSingleChoiceDialog(R.string.employees, ReturnGoodsActivity.this,
                MyKeeper.getInstance().getEmployeesName(), new BruceDialog.OnChoiceItemListener() {
                    @Override
                    public void onChoiceItem(String itemName) {
                        if (!TextUtils.isEmpty(itemName)) {
                            MyKeeper.getInstance().setStaff(itemName);
                            updateUser();
                        }
                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.goodsBack:
                returnGoods();
                break;
            case R.id.user:
                showUserDialog();
                break;
            case R.id.scan:
                onScanBarcode();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "扫码取消！", Toast.LENGTH_LONG).show();
            } else {
                handlerScanResultInKeyListen(result.getContents());
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 调用zxing扫描条形码
     */
    public void onScanBarcode() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ONE_D_CODE_TYPES);
        integrator.setCaptureActivity(ScanActivity.class);  //如果不需要竖屏显示 ，忽略这个
        integrator.setPrompt("扫描条形码");
        integrator.setCameraId(0);
        integrator.setBeepEnabled(true);
        integrator.initiateScan();
    }

    /**
     * 退货
     */
    private void returnGoods() {
        super.showProgressDialog("退货中...");
        viewModel.returnGoods();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
