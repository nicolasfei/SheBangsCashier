package com.nicolas.shebangscashier.ui.shopmanage;

import android.os.Bundle;
import android.os.Message;
import android.text.Html;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.nicolas.componentlibrary.pullrefreshswipemenu.PullRefreshSwipeMenuListView;
import com.nicolas.scannerlibrary.PPdaScanner;
import com.nicolas.scannerlibrary.Scanner;
import com.nicolas.shebangscashier.BaseActivity;
import com.nicolas.shebangscashier.R;
import com.nicolas.shebangscashier.common.BarCodeInformation;
import com.nicolas.shebangscashier.common.BarCodePrintAdapter;
import com.nicolas.shebangscashier.common.OperateResult;
import com.nicolas.toollibrary.BruceDialog;

public class BarCodePrintActivity extends BaseActivity implements View.OnClickListener {

    private TextView count;
    private CheckBox checkAll;
    private Button print;
    private EditText barCode;
    private Scanner scanner;                //扫描头

    private BarCodePrintAdapter adapter;
    private BarCodePrintViewModel viewModel;
    private boolean manualCheckAll = false;     //手动设置checkAll状态

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print_barcode);
        this.viewModel = new ViewModelProvider(this).get(BarCodePrintViewModel.class);

        //初始化扫描头
        scanner = new PPdaScanner(this);
        //广播接收方式
        scanner.setOnScannerScanResultListener(new Scanner.OnScannerScanResultListener() {
            @Override
            public void scanResult(String scan) {
                handlerBarCodeInput(scan);
            }
        });

        this.barCode = findViewById(R.id.barCode);
        this.barCode.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT) {
                    handlerBarCodeInput(barCode.getText().toString());
                }
                return false;
            }
        });
        //按键监听方式
        this.barCode.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (KeyEvent.KEYCODE_ENTER == keyCode && event.getAction() == KeyEvent.ACTION_UP) {
                    handlerBarCodeInput(barCode.getText().toString());
                    return true;
                }
                return false;
            }
        });
        this.barCode.requestFocus();
        this.print = findClickView(R.id.print);
        PullRefreshSwipeMenuListView listView = findViewById(R.id.pullToRefreshListView);
        this.adapter = new BarCodePrintAdapter(this, viewModel.getDataList());
        this.adapter.setOnCheckStateChangedListener(new BarCodePrintAdapter.OnCheckStateChangedListener() {

            @Override
            public void CheckStateChanged(BarCodeInformation info) {
                updateCheckStatus();
            }
        });
        listView.setAdapter(adapter);

        this.count = findViewById(R.id.count);
        this.checkAll = findViewById(R.id.checkAll);
        this.checkAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (manualCheckAll) {
                    manualCheckAll = false;
                    return;
                }
                viewModel.setSelectAll(isChecked);
                adapter.notifyDataSetChanged();
            }
        });

        //监听查询条码结果
        viewModel.getQueryResult().observe(this, new Observer<OperateResult>() {
            @Override
            public void onChanged(OperateResult operateResult) {
                if (operateResult.getSuccess() != null) {
                    Message msg = operateResult.getSuccess().getMessage();
                    if (msg != null) {     //无订单数据
                        updateCheckStatus();                   //更新订单明细
                        BruceDialog.showPromptDialog(BarCodePrintActivity.this, (String) msg.obj);
                    } else {
                        Toast.makeText(BarCodePrintActivity.this, "查询成功", Toast.LENGTH_SHORT).show();
                    }
                }
                if (operateResult.getError() != null) {
                    BruceDialog.showPromptDialog(BarCodePrintActivity.this, operateResult.getError().getErrorMsg());
                }
                adapter.notifyDataSetChanged();
                dismissProgressDialog();
            }
        });

        //监听打印
        viewModel.getPrintBarCodeResult().observe(this, new Observer<OperateResult>() {
            @Override
            public void onChanged(OperateResult operateResult) {
                if (operateResult.getSuccess() != null) {
                    Toast.makeText(BarCodePrintActivity.this, "打印成功", Toast.LENGTH_SHORT).show();
                }
                if (operateResult.getError() != null) {
                    BruceDialog.showWarningDialog(BarCodePrintActivity.this, operateResult.getError().getErrorMsg(), null);
                }
                dismissProgressDialog();
            }
        });
    }

    /**
     * 订单被选中/被取消后更新界面
     */
    private void updateCheckStatus() {
        String total;
        if (this.viewModel.getTotalSelectNum() > 0) {
            this.print.setEnabled(true);
            this.print.setBackground(getDrawable(R.drawable.shape_rectangle_red));
            total = getString(R.string.total) + "\u3000" + getString(R.string.colon) + "<font color=\"blue\">" + this.viewModel.getTotalSelectNum() + "</font>";
        } else {
            this.print.setEnabled(false);
            this.print.setBackground(getDrawable(R.drawable.shape_rectangle_grey));
            total = getString(R.string.total);
        }
        this.count.setText(Html.fromHtml(total, Html.FROM_HTML_MODE_COMPACT));

        //更新checkAll状态
        if (this.viewModel.getIsAllSelect()) {
            if (!checkAll.isChecked()) {
                manualCheckAll = true;
                checkAll.setChecked(true);
            }
        } else {
            if (checkAll.isChecked()) {
                manualCheckAll = true;
                checkAll.setChecked(false);
            }
        }
    }

    /**
     * 处理barCode输入
     *
     * @param barCode 条码
     */
    private void handlerBarCodeInput(String barCode) {
        this.barCode.setText("");
        super.showProgressDialog(getString(R.string.querying));
        this.viewModel.queryBarCode("", barCode);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.print:
                printBarCode();

                break;
            default:
                break;
        }
    }

    private void printBarCode() {
        showProgressDialog(getString(R.string.print_barcode));
        viewModel.printBarCode();
    }
}
