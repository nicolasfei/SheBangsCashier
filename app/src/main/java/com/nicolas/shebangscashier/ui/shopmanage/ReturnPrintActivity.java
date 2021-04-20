package com.nicolas.shebangscashier.ui.shopmanage;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.nicolas.scannerlibrary.PPdaScanner;
import com.nicolas.scannerlibrary.Scanner;
import com.nicolas.shebangscashier.BaseActivity;
import com.nicolas.shebangscashier.R;
import com.nicolas.shebangscashier.cashier.MyKeeper;
import com.nicolas.shebangscashier.common.OperateResult;
import com.nicolas.shebangscashier.common.ReturnPrintData;
import com.nicolas.toollibrary.BruceDialog;

public class ReturnPrintActivity extends BaseActivity implements View.OnClickListener {

    private ReturnPrintViewModel viewModel;
    private TextView user;
    private EditText goodsCode;
    private Scanner scanner;                        //扫描头

    private TextView goodsCodeValue, goodsID, goodsClass, goodsCodeStatus;
    private TextView branch, price, id;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retuan_print);
        viewModel = new ViewModelProvider(this).get(ReturnPrintViewModel.class);

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
        this.goodsCode = findViewById(R.id.goods_code);
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

        goodsCodeValue = findViewById(R.id.goodsCode);
        goodsID = findViewById(R.id.goodsID);
        goodsClass = findViewById(R.id.goodsClass);
        goodsCodeStatus = findViewById(R.id.goodsCodeStatus);
        branch = findViewById(R.id.branch);
        price = findViewById(R.id.price);
        id = findViewById(R.id.id);

        viewModel.getQueryReturnPrintResult().observe(this, new Observer<OperateResult>() {

            @Override
            public void onChanged(OperateResult operateResult) {
                if (operateResult.getSuccess() != null) {
                    updateReturnPrint();
                }
                if (operateResult.getError() != null) {
                    Toast.makeText(ReturnPrintActivity.this, operateResult.getError().getErrorMsg(), Toast.LENGTH_SHORT).show();
                }
                dismissProgressDialog();
            }
        });

        viewModel.getReturnPrintResult().observe(this, new Observer<OperateResult>() {

            @Override
            public void onChanged(OperateResult operateResult) {
                if (operateResult.getSuccess() != null) {
                    Toast.makeText(ReturnPrintActivity.this, "打印成功", Toast.LENGTH_SHORT).show();
                }
                if (operateResult.getError() != null) {
                    Toast.makeText(ReturnPrintActivity.this, operateResult.getError().getErrorMsg(), Toast.LENGTH_SHORT).show();
                }
                dismissProgressDialog();
            }
        });

        updateReturnPrint();
    }

    private void updateReturnPrint() {
        ReturnPrintData data = viewModel.getData();
        this.goodsCodeValue.setText(("商品条码：" + data.sId));
        this.goodsID.setText(("货  号：" + data.goodsId));
        this.goodsClass.setText(("商品类别：" + data.goodsClassName));
        this.goodsCodeStatus.setText(("条码状态：" + data.state));
        this.branch.setText(("所在分店：" + data.area));
        this.price.setText(("售  价：" + data.salePrice));
        this.id.setText(("编  号：" + data.id));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.print:
                if (viewModel.getData() == null) {
                    return;
                }
                showProgressDialog("打印中...");
                viewModel.printReturnGoods();
                break;
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
        BruceDialog.showSingleChoiceDialog(R.string.employees, ReturnPrintActivity.this, MyKeeper.getInstance().getEmployeesName(),
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
        this.viewModel.queryBarCode(barCode);
    }
}
