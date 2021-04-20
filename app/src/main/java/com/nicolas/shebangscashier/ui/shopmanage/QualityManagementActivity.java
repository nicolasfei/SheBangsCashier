package com.nicolas.shebangscashier.ui.shopmanage;

import com.nicolas.componentlibrary.datetimepicker.DateTimePickerDialog;
import com.nicolas.scannerlibrary.PPdaScanner;
import com.nicolas.scannerlibrary.Scanner;
import com.nicolas.shebangscashier.BaseActivity;
import com.nicolas.shebangscashier.R;
import com.nicolas.shebangscashier.cashier.MyKeeper;
import com.nicolas.shebangscashier.common.GoodsInventoryAdapter;
import com.nicolas.shebangscashier.common.OperateResult;
import com.nicolas.toollibrary.BruceDialog;
import com.nicolas.toollibrary.Tool;

import android.os.Bundle;
import android.text.Html;
import android.text.InputType;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

public class QualityManagementActivity extends BaseActivity implements View.OnClickListener {

    private DrawerLayout drawerLayout;

    private TextView user, numCount;
    private TextView id, goodsID, registerTime;
    private QualityManagementViewModel viewModel;

    private EditText goodsCode;
    private Scanner scanner;                        //扫描头

    private GoodsInventoryAdapter adapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quality_management);

        this.viewModel = new ViewModelProvider(this).get(QualityManagementViewModel.class);

        this.drawerLayout = findViewById(R.id.drawer_layout);
        this.user = findClickView(R.id.user);
        this.updateUser();
        this.numCount = findViewById(R.id.numCount);

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

        //查询条件
        this.goodsID = findClickView(R.id.goodsID);
        this.registerTime = findClickView(R.id.registerTime);
        this.id = findClickView(R.id.id);
        findClickView(R.id.idClear);
        findClickView(R.id.goodsIDClear);
        findClickView(R.id.registerTimeClear);
        findClickView(R.id.reset);
        findClickView(R.id.query);

        //监听查询结果
        viewModel.getQueryQualityResult().observe(this, new Observer<OperateResult>() {
            @Override
            public void onChanged(OperateResult operateResult) {
                if (operateResult.getSuccess() != null) {
                    adapter.notifyDataSetChanged();
                    if (operateResult.getSuccess().getMessage() != null) {
                        BruceDialog.showPromptDialog(QualityManagementActivity.this, (String) operateResult.getSuccess().getMessage().obj);
                    }
                }
                if (operateResult.getError() != null) {
                    Toast.makeText(QualityManagementActivity.this, operateResult.getError().getErrorMsg(), Toast.LENGTH_SHORT).show();
                }
                dismissProgressDialog();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.user:
                showSalespersonDialog();
                break;
            case R.id.goodsID:              //新货号
                showGoodsIDDialog();
                break;
            case R.id.goodsIDClear:
                goodsIdClear();
                break;
            case R.id.registerTime:         //登记时间
                registerTimeChoice(viewModel.getCondition().getRegisterTime());
                break;
            case R.id.registerTimeClear:
                registerTimeReset();
                break;
            case R.id.id:                   //编号
                showIdDialog();
                break;
            case R.id.idClear:
                idClear();
                break;
            case R.id.reset:
                resetCondition();
                break;
            case R.id.query:
                query();
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.inventory_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.menu_screen:
                this.drawerLayout.openDrawer(GravityCompat.END);
                break;
            default:
                break;
        }
        return true;
    }

    /**
     * 清空条件
     */
    private void resetCondition() {
        idClear();
        goodsIdClear();
        registerTimeReset();
        viewModel.getCondition().setGoodsCode("");
    }

    /**
     * 查询
     */
    private void query() {
        showProgressDialog(getString(R.string.querying));
        viewModel.query();
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
        BruceDialog.showSingleChoiceDialog(R.string.employees, QualityManagementActivity.this, MyKeeper.getInstance().getEmployeesName(),
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
     * 更新新货号
     *
     * @param itemValue 新货号
     */
    private void updateGoodsID(String itemValue) {
        String value = getString(R.string.goodsId) + "\u3000\u3000\u3000\u3000\u3000" + "<font color=\"black\">" + itemValue + "</font>";
        goodsID.setText(Html.fromHtml(value, Html.FROM_HTML_MODE_COMPACT));
    }

    /**
     * 新货号对话框
     */
    private void showGoodsIDDialog() {
        BruceDialog.showEditInputDialog(R.string.goodsId, R.string.goodsIdInput, InputType.TYPE_CLASS_TEXT, this, new BruceDialog.OnInputFinishListener() {
            @Override
            public void onInputFinish(String itemName) {
                updateGoodsID(itemName);
                viewModel.getCondition().setGoodsID(itemName);
            }
        });
    }

    /**
     * 清空新货号
     */
    private void goodsIdClear() {
        viewModel.getCondition().setGoodsID("");
        updateGoodsID("");
    }

    /**
     * 更新商品编号
     *
     * @param itemValue 商品条码
     */
    private void updateID(String itemValue) {
        String value = getString(R.string.id) + "\u3000\u3000\u3000\u3000\u3000" + "<font color=\"black\">" + itemValue + "</font>";
        id.setText(Html.fromHtml(value, Html.FROM_HTML_MODE_COMPACT));
    }

    /**
     * 商品编号对话框
     */
    private void showIdDialog() {
        BruceDialog.showEditInputDialog(R.string.goodsCode, R.string.goodsCodeInput, InputType.TYPE_CLASS_TEXT, this, new BruceDialog.OnInputFinishListener() {
            @Override
            public void onInputFinish(String itemName) {
                updateID(itemName);
                viewModel.getCondition().setId(itemName);
            }
        });
    }

    /**
     * 清空商品编号
     */
    private void idClear() {
        viewModel.getCondition().setId("");
        updateID("");
    }

    /**
     * 更新登记时间
     *
     * @param itemValue 登记时间
     */
    private void updateRegisterTime(String itemValue) {
        registerTime.setText(itemValue);
    }

    /**
     * 入库时间选择
     *
     * @param itemValue 入库时间
     */
    private void registerTimeChoice(String itemValue) {
        String start = "";
        String end = "";
        if (!TextUtils.isEmpty(itemValue)) {
            String[] times = itemValue.split("~");
            if (times.length >= 2) {
                start = times[0];
                end = times[1];
            }
        }
        DateTimePickerDialog.showDateSlotPickerDialog(QualityManagementActivity.this, start, end, new DateTimePickerDialog.OnDateTimeSlotPickListener() {
            @Override
            public void OnDateTimeSlotPick(String start, String end) {
                updateRegisterTime((start + "\u3000~\u3000" + end));
                viewModel.getCondition().setRegisterTime((start + "~" + end));
            }
        });
    }

    /**
     * 重置入库时间
     */
    private void registerTimeReset() {
        String time = Tool.getNearlyOneDaysDateSlot();
        viewModel.getCondition().setRegisterTime(time);
        updateRegisterTime(time);
    }

    /**
     * 处理barCode输入
     *
     * @param barCode 条码
     */
    private void handlerBarCodeInput(String barCode) {
        this.goodsCode.setText("");
        if (viewModel.isHaveBarCode(barCode)) {
            BruceDialog.showPromptDialog(this, getString(R.string.repeat_code));
            return;
        }
        super.showProgressDialog(getString(R.string.querying));
        viewModel.getCondition().setGoodsCode(barCode);
        this.viewModel.query();
    }
}
