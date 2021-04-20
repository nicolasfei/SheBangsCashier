package com.nicolas.shebangscashier.ui.shopmanage;

import com.nicolas.scannerlibrary.PPdaScanner;
import com.nicolas.scannerlibrary.Scanner;
import com.nicolas.shebangscashier.BaseActivity;
import com.nicolas.shebangscashier.R;
import com.nicolas.shebangscashier.cashier.MyKeeper;
import com.nicolas.shebangscashier.common.GoodsInventoryAdapter;
import com.nicolas.shebangscashier.common.OperateResult;
import com.nicolas.toollibrary.BruceDialog;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

public class GoodsInventorySearchActivity extends BaseActivity implements View.OnClickListener {

    private GoodsInventorySearchViewModel viewModel;
    private TextView user;
    private EditText goodsCode;
    private Scanner scanner;                        //扫描头

    private GoodsInventoryAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods_inventory_search);

        this.viewModel = new ViewModelProvider(this).get(GoodsInventorySearchViewModel.class);
        this.user = findClickView(R.id.user);
        this.updateUser();

        //初始化扫描头
        scanner = new PPdaScanner(this);
        //广播接收方式
        scanner.setOnScannerScanResultListener(new Scanner.OnScannerScanResultListener() {
            @Override
            public void scanResult(String scan) {
                handlerBarCodeInput(scan);
            }
        });
        this.goodsCode = findViewById(R.id.transferCode);
        this.goodsCode.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT) {
                    handlerBarCodeInput(goodsCode.getText().toString());
                }
                return false;
            }
        });
        //按键监听方式
        this.goodsCode.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (KeyEvent.KEYCODE_ENTER == keyCode && event.getAction() == KeyEvent.ACTION_UP) {
                    handlerBarCodeInput(goodsCode.getText().toString());
                    return true;
                }
                return false;
            }
        });
        this.goodsCode.requestFocus();

        ListView listView = findViewById(R.id.listView);
        this.adapter = new GoodsInventoryAdapter(this, viewModel.getDataList());
        listView.setAdapter(this.adapter);

        //监听查询结果
        viewModel.getSearchGoodsInventoryResult().observe(this, new Observer<OperateResult>() {
            @Override
            public void onChanged(OperateResult operateResult) {
                if (operateResult.getSuccess() != null) {
                    adapter.notifyDataSetChanged();
                    if (operateResult.getSuccess().getMessage() != null) {
                        Toast.makeText(GoodsInventorySearchActivity.this, (String) operateResult.getSuccess().getMessage().obj,
                                Toast.LENGTH_SHORT).show();
                    }
                    if (operateResult.getError() != null) {
                        Toast.makeText(GoodsInventorySearchActivity.this, operateResult.getError().getErrorMsg(), Toast.LENGTH_SHORT).show();
                    }
                    dismissProgressDialog();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.user:
                showSalespersonDialog();
            default:
                break;
        }
    }

    /**
     * 更新收银员
     */
    private void updateUser() {
        this.user.setText(MyKeeper.getInstance().getStaff().name);
    }

    /**
     * 收银员切换
     */
    private void showSalespersonDialog() {
        BruceDialog.showSingleChoiceDialog(R.string.employees, GoodsInventorySearchActivity.this, MyKeeper.getInstance().getEmployeesName(),
                new BruceDialog.OnChoiceItemListener() {
                    @Override
                    public void onChoiceItem(String itemName) {
                        if (!TextUtils.isEmpty(itemName)) {
                            MyKeeper.getInstance().setStaff(itemName);
                            updateUser();
                        }
                    }
                });
    }

    /**
     * 处理barCode输入
     *
     * @param barCode 条码
     */
    private void handlerBarCodeInput(String barCode) {
        this.goodsCode.setText("");
        super.showProgressDialog(getString(R.string.querying));
        this.viewModel.searchGoodsInventory(barCode);
    }
}
