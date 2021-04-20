package com.nicolas.shebangscashier.ui.cash;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.nicolas.scannerlibrary.PPdaScanner;
import com.nicolas.scannerlibrary.Scanner;
import com.nicolas.shebangscashier.BaseActivity;
import com.nicolas.shebangscashier.R;
import com.nicolas.shebangscashier.cashier.MyKeeper;
import com.nicolas.shebangscashier.common.OperateResult;
import com.nicolas.shebangscashier.common.ReturnGoodsAdapter;
import com.nicolas.toollibrary.BruceDialog;
import com.nicolas.toollibrary.Tool;

public class ReturnGoodsActivity extends BaseActivity implements View.OnClickListener {

    private ReturnGoodsViewModel viewModel;

    private EditText codeInput;             //条码输入
    private Scanner scanner;                //扫描头
    private TextView user;                  //收银员

    private TextView vipPhone;              //会员手机
    private TextView vipIntegral;           //会员积分
    private TextView deductIntegral;        //扣除积分
    private TextView integralBalance;       //积分差额
    private TextView refundDeduction;       //退款减扣

    private TextView numCount;              //数量合计
    private TextView refundCount;           //退款合计

    private String lastCode = "";           //上一次条码输入

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

        //初始化扫描头
        scanner = new PPdaScanner(this);
        //广播接收方式
        scanner.setOnScannerScanResultListener(new Scanner.OnScannerScanResultListener() {
            @Override
            public void scanResult(String scan) {
                handlerScanResultInBroadcast(scan);
            }
        });
        //codeInput
        this.codeInput = findViewById(R.id.code);
        //按键监听方式
        this.codeInput.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (KeyEvent.KEYCODE_ENTER == keyCode && event.getAction() == KeyEvent.ACTION_UP) {
                    handlerScanResultInKeyListen(codeInput.getText().toString());
                    return true;
                }
                return false;
            }
        });
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

        findClickView(R.id.goodsBack);

        SwipeMenuListView listView = findViewById(R.id.listView);
        this.adapter = new ReturnGoodsAdapter(this, viewModel.getInformations());
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

        //设置监听
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

        this.viewModel.getReturnGoodsResult().observe(this, new Observer<OperateResult>() {
            @Override
            public void onChanged(OperateResult operateResult) {
                dismissProgressDialog();
                if (operateResult.getSuccess()!=null){
                    adapter.notifyDataSetChanged();
                }
                if (operateResult.getError()!=null){
                    BruceDialog.showPromptDialog(ReturnGoodsActivity.this, operateResult.getError().getErrorMsg());
                }
            }
        });

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
//        adapter.notifyDataSetChanged();
//        updateGoodsStatisticsData();
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
        lastCode = scan;
        queryCodeInformation(scan);
    }

    /**
     * 处理扫描结果--按键监听方式
     *
     * @param scan 扫描结果
     */
    private void handlerScanResultInKeyListen(String scan) {
        String currCode;
        if (!TextUtils.isEmpty(lastCode)) {
            currCode = scan.substring(0, scan.length() - lastCode.length());
        } else {
            currCode = scan;
        }
        codeInput.setText(currCode);
        lastCode = currCode;
        queryCodeInformation(currCode);
    }

    /**
     * 手动输入条码处理
     *
     * @param code 条码
     */
    private void handlerManualCodeInput(String code) {
        lastCode = code;
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
            default:
                break;
        }
    }

    /**
     * 退货
     */
    private void returnGoods() {
        super.showProgressDialog("退货中...");
        viewModel.returnGoods();
    }
}
