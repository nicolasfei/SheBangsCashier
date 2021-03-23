package com.nicolas.shebangscashier.ui.cash;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.text.InputType;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
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
import com.nicolas.shebangscashier.common.GoodsInformationAdapter;
import com.nicolas.shebangscashier.common.OperateResult;
import com.nicolas.shebangscashier.communication.sale.SaleReceipt;
import com.nicolas.toollibrary.BruceDialog;
import com.nicolas.toollibrary.Tool;
import com.nicolas.toollibrary.VibratorUtil;

public class SaleReceiptActivity extends BaseActivity implements View.OnClickListener {

    private SaleReceiptViewModel viewModel;
    private PPdaScanner scanner;
    private TextView user;
    private TextView receipt;
    private EditText code;
    private TextView numCount, priceCount;
    private Button print;

    private GoodsInformationAdapter goodsAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale_receipt);

        this.viewModel = new ViewModelProvider(this).get(SaleReceiptViewModel.class);

        //初始化扫描头
        this.scanner = new PPdaScanner(this);
        this.scanner.setOnScannerScanResultListener(new Scanner.OnScannerScanResultListener() {
            @Override
            public void scanResult(String scan) {
                handlerCodeInput(scan);
            }
        });
        //初始化组件View
        user = findClickView(R.id.user);
        updateUser();

        //打印小票
        this.print = findClickView(R.id.print);

        //合计
        this.numCount = findViewById(R.id.numCount);
        this.priceCount = findViewById(R.id.priceCount);

        //小票
        receipt = findViewById(R.id.receipt);
        receipt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInputReceiptDialog();
            }
        });
        receipt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT) {
                    handlerReceiptInput(receipt.getText().toString());
                }
                return false;
            }
        });

        //条码
        code = findViewById(R.id.code);
        code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                code.selectAll();
            }
        });
        code.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT) {
                    handlerCodeInput(code.getText().toString());
                }
                return false;
            }
        });
        code.requestFocus();

        //SwipeMenuListView
        final SwipeMenuListView saleList = findViewById(R.id.swipeMenuListView);
        goodsAdapter = new GoodsInformationAdapter(this, viewModel.getGoodsList());
        saleList.setAdapter(goodsAdapter);
        //左滑菜单
        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                deleteItem.setBackground(new ColorDrawable(Color.GRAY));
                // set item width
                deleteItem.setWidth(Tool.dp2px(SaleReceiptActivity.this, 90));
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
                    default:
                        break;
                }
                return true;
            }
        });

        //查询更新
        viewModel.getReceiptQueryResult().observe(this, new Observer<OperateResult>() {
            @Override
            public void onChanged(OperateResult operateResult) {
                if (operateResult.getSuccess() != null) {
                    updateCount();
                }
                if (operateResult.getError() != null) {
                    VibratorUtil.getInstance().showWarning();
                    BruceDialog.showPromptDialog(SaleReceiptActivity.this, operateResult.getError().getErrorMsg());
                }

                dismissProgressDialog();
            }
        });

        updateCount();
        //默认查询上一次销售小票
        showProgressDialog(getString(R.string.querying));
        viewModel.queryLastReceipt();
    }

    /**
     * 小票编号输入
     */
    private void showInputReceiptDialog() {
        BruceDialog.showEditInputDialog(getString(R.string.receipt), getString(R.string.receipt_input), InputType.TYPE_CLASS_TEXT,
                SaleReceiptActivity.this, new BruceDialog.OnInputFinishListener() {
                    @Override
                    public void onInputFinish(String itemName) {
                        String receiptCode = receipt.getText().toString();
                        if (!itemName.equals(receiptCode)) {
                            handlerReceiptInput(itemName);
                        }
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        scanner.scannerOpen();
    }

    @Override
    protected void onPause() {
        super.onPause();
        scanner.scannerSuspend();
    }

    /**
     * 删除商品
     *
     * @param position pos
     */
    private void deleteGoods(int position) {
        viewModel.deleteGoods(position);
        this.updateCount();
    }

    /**
     * 小票输入
     *
     * @param value 小票
     */
    private void handlerReceiptInput(String value) {
        receipt.setText(value);
        showProgressDialog(getString(R.string.querying));
        viewModel.queryReceipt(value);
        code.requestFocus();
    }

    /**
     * 条码输入
     *
     * @param value 条码
     */
    private void handlerCodeInput(String value) {
        code.setText("");
        showProgressDialog(getString(R.string.querying));
        viewModel.queryCode(value);
    }

    /**
     * 合计更新
     */
    private void updateCount() {
        String numCountValue = getString(R.string.num) + getString(R.string.colon_zh) + "<font color=\"black\"><big>" + viewModel.getGoodsCount() + "</big></font>";
        this.numCount.setText(Html.fromHtml(numCountValue, Html.FROM_HTML_MODE_COMPACT));

        String priceCountValue = getString(R.string.total_price) + getString(R.string.colon_zh) + "<font color=\"black\"><big>" + viewModel.getGoodsTotalPrice() + "</big></font>";
        this.priceCount.setText(Html.fromHtml(priceCountValue, Html.FROM_HTML_MODE_COMPACT));

        this.goodsAdapter.notifyDataSetChanged();

        if (viewModel.getGoodsList().size() == 0) {
            if (print.isClickable()) {
                print.setBackground(getDrawable(R.drawable.shape_rectangle_grey));
                print.setClickable(false);
            }
        } else {
            if (!print.isClickable()) {
                print.setBackground(getDrawable(R.drawable.shape_rectangle_red));
                print.setClickable(true);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.user:
                //收银员切换
                showSalespersonDialog();
                break;
            case R.id.print:
                //打印小票
                printReceipt();
                break;
            default:
                break;
        }
    }

    /**
     * 打印小票
     */
    private void printReceipt() {

    }

    /**
     * 收银员切换
     */
    private void showSalespersonDialog() {
        BruceDialog.showSingleChoiceDialog(R.string.employees, SaleReceiptActivity.this, MyKeeper.getInstance().getEmployeesName(), new BruceDialog.OnChoiceItemListener() {
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
     * 更新收银员
     */
    private void updateUser() {
        this.user.setText(MyKeeper.getInstance().getStaff().name);
    }
}
