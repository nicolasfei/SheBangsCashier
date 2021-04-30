package com.nicolas.shebangscashier.ui.shopmanage;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
//import com.nicolas.scannerlibrary.PPdaScanner;
//import com.nicolas.scannerlibrary.Scanner;
import com.nicolas.shebangscashier.BaseActivity;
import com.nicolas.shebangscashier.R;
import com.nicolas.shebangscashier.common.OperateResult;
import com.nicolas.toollibrary.BruceDialog;
import com.nicolas.toollibrary.Tool;
import com.nicolas.toollibrary.Utils;
import com.nicolas.toollibrary.VibratorUtil;

public class InventoryGoodsActivity extends BaseActivity implements View.OnClickListener {

    private EditText codeInput;             //条码输入
    private TextView inventoryCount;        //盘点数量合计
//    private Scanner scanner;                //扫描头
    private Button submit;

    private ArrayAdapter<String> adapter;
    private InventoryGoodsViewModel viewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory2);

        this.viewModel = new ViewModelProvider(this).get(InventoryGoodsViewModel.class);

//        //初始化扫描头
//        scanner = new PPdaScanner(this);
//        //广播接收方式
//        scanner.setOnScannerScanResultListener(new Scanner.OnScannerScanResultListener() {
//            @Override
//            public void scanResult(String scan) {
//                handlerScanResultInBroadcast(scan);
//            }
//        });
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

        this.inventoryCount = findViewById(R.id.inventoryCount);

        //list
        final SwipeMenuListView saleList = findViewById(R.id.listView);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, viewModel.getInformationList());
        saleList.setAdapter(adapter);
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
                detailsItem.setWidth(Tool.dp2px(InventoryGoodsActivity.this, 90));
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
                deleteItem.setWidth(Tool.dp2px(InventoryGoodsActivity.this, 90));
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
        saleList.setMenuCreator(creator);
        saleList.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        deleteGoods(position);
                        break;
                    case 1:
//                        deleteGoods(position);
                        break;
                }
                return true;
            }
        });

        //设置查询服务器商品数据监听
        viewModel.getQueryResult().observe(this, new Observer<OperateResult>() {
            @Override
            public void onChanged(OperateResult OperateResult) {        //销售商品查询结果
                dismissProgressDialog();
                if (OperateResult.getSuccess() != null) {
                    adapter.notifyDataSetChanged();
                    checkSettlement();
                }
                if (OperateResult.getError() != null) {
                    Utils.toast(InventoryGoodsActivity.this, OperateResult.getError().getErrorMsg());
                    //开启语音，震动提示服务
                    VibratorUtil.getInstance().showWarning();
                }
            }
        });

        //监听提交
        viewModel.getSubmitResult().observe(this, new Observer<OperateResult>() {
            @Override
            public void onChanged(OperateResult OperateResult) {        //销售商品查询结果
                dismissProgressDialog();
                if (OperateResult.getSuccess() != null) {
                    adapter.notifyDataSetChanged();
                    checkSettlement();
                    Utils.toast(InventoryGoodsActivity.this, "盘点成功!");
                }
                if (OperateResult.getError() != null) {
                    Utils.toast(InventoryGoodsActivity.this, OperateResult.getError().getErrorMsg());
                    VibratorUtil.getInstance().showWarning();
                }
            }
        });

        this.submit = findClickView(R.id.submit);
        checkSettlement();
    }

    @Override
    public void onBackPressed() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(codeInput.getWindowToken(), 0); //强制隐藏键盘
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (this.viewModel.isHavDataNotProcessed()) {
                    BruceDialog.showAlertDialog(this, getString(R.string.warning), getString(R.string.isHavDataNotProcessed),
                            new BruceDialog.OnAlertDialogListener() {
                                @Override
                                public void onSelect(boolean confirm) {
                                    if (confirm) {
                                        InventoryGoodsActivity.this.finish();
                                    }
                                }
                            });
                } else {
                    finish();
                }
                break;
        }
        return true;
    }

    /**
     * 更新合计
     */
    private void checkSettlement() {
        int count = viewModel.getInventoryCount();
        this.inventoryCount.setText(("盘点合计：" + count));
        if (count == 0) {
            if (submit.isClickable()) {
                submit.setBackground(getDrawable(R.drawable.shape_rectangle_grey));
                submit.setClickable(false);
            }
        } else {
            if (!submit.isClickable()) {
                submit.setBackground(getDrawable(R.drawable.shape_rectangle_red));
                submit.setClickable(true);
            }
        }
    }

    /**
     * 处理扫描结果--广播方式
     *
     * @param scan scan
     */
    private void handlerScanResultInBroadcast(String scan) {
        codeInput.setText("");
        queryCodeInformation(scan);
    }

    /**
     * 处理扫描结果--按键监听方式
     *
     * @param scan 扫描结果
     */
    private void handlerScanResultInKeyListen(String scan) {
        codeInput.setText("");
        queryCodeInformation(scan);
    }

    /**
     * 手动输入条码处理
     *
     * @param code 条码
     */
    private void handlerManualCodeInput(String code) {
        codeInput.setText("");
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
     * 删除商品
     *
     * @param position position
     */
    private void deleteGoods(int position) {
        viewModel.removeGoods(position);
        adapter.notifyDataSetChanged();
        checkSettlement();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        scanner.scannerOpen();
    }

    @Override
    protected void onPause() {
        super.onPause();
//        scanner.scannerSuspend();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.submit:           //盘点提交
                submitData();
                break;
            default:
                break;
        }
    }

    /**
     * 提交数据
     */
    private void submitData() {
        showProgressDialog("提交中...");
        viewModel.submitData();
    }
}
