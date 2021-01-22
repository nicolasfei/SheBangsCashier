package com.nicolas.shebangscashier.ui.cash;

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

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.nicolas.scannerlibrary.PPdaScanner;
import com.nicolas.scannerlibrary.Scanner;
import com.nicolas.shebangscashier.MyActivity;
import com.nicolas.shebangscashier.R;
import com.nicolas.shebangscashier.cashier.MyKeeper;
import com.nicolas.shebangscashier.common.OperateResult;
import com.nicolas.shebangscashier.ui.cash.data.OldSaleDialog;
import com.nicolas.shebangscashier.ui.cash.data.SaleGoodsInformation;
import com.nicolas.shebangscashier.ui.cash.data.SaleGoodsInformationAdapter;
import com.nicolas.toollibrary.BruceDialog;
import com.nicolas.toollibrary.Tool;
import com.nicolas.toollibrary.Utils;


public class SaleActivity extends MyActivity implements View.OnClickListener {

    private SaleGoodsInformationAdapter goodsAdapter;
    private EditText codeInput;             //条码输入
    private Scanner scanner;                //扫描头
    private Button settlement;              //结算
    private SaleViewModel viewModel;
    private TextView salesperson;           //收银员
    private TextView goodsCount;            //商品数量
    private TextView priceCount;            //总价合计

    private String lastCode = "";           //上一次条码输入

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale);
        viewModel = new ViewModelProvider(this).get(SaleViewModel.class);

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
        this.codeInput = findViewById(R.id.codeInput);
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

        //收银员
        salesperson = findClickView(R.id.salesperson);
        updateSalesperson();
        //商品数量
        goodsCount = findViewById(R.id.goodsCount);
        //价格合计
        priceCount = findViewById(R.id.priceCount);
        //旧货销售
        findClickView(R.id.oldSale);

        //货物list
        final SwipeMenuListView saleList = findViewById(R.id.sale_list_view);
        settlement = findClickView(R.id.settlement);

        goodsAdapter = new SaleGoodsInformationAdapter(this, viewModel.getGoodsInformationList());
        saleList.setAdapter(goodsAdapter);
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
                detailsItem.setWidth(Tool.dp2px(SaleActivity.this, 90));
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
                deleteItem.setWidth(Tool.dp2px(SaleActivity.this, 90));
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
//                        showGoodsDetails(position);
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
        viewModel.getQueryGoodsInformationResult().observe(this, new Observer<OperateResult>() {
            @Override
            public void onChanged(OperateResult OperateResult) {        //销售商品查询结果
                dismissProgressDialog();
                if (OperateResult.getSuccess() != null) {
                    goodsAdapter.notifyDataSetChanged();
                    checkSettlement();
                }
                if (OperateResult.getError() != null) {
                    Utils.toast(SaleActivity.this, OperateResult.getError().getErrorMsg());
                }
            }
        });

        //设置结算按钮状态
        checkSettlement();
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
     * 检测结算按钮
     */
    private void checkSettlement() {
        if (viewModel.getGoodsCount() == 0) {
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
        goodsCount.setText((getString(R.string.num) + viewModel.getGoodsCount()));
        priceCount.setText((getString(R.string.total_price) + getString(R.string.money) + viewModel.getPriceTotal()));
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
     * 查询旧货商品信息
     *
     * @param oldGoods 旧货
     */
    private void queryOldGoodsInfo(SaleGoodsInformation oldGoods) {
        viewModel.addOldGoodsSale(oldGoods);
        goodsAdapter.notifyDataSetChanged();
        checkSettlement();
    }

    /**
     * 删除商品
     *
     * @param position position
     */
    private void deleteGoods(int position) {
        viewModel.removeGoods(position);
        goodsAdapter.notifyDataSetChanged();
        checkSettlement();
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.salesperson:
                //收银员切换
                showSalespersonDialog();
                break;
            case R.id.oldSale:
                //旧货销售
                oldGoodsSale();
                break;
            case R.id.settlement:
                //结算
                saleSettlement();
                break;
            default:
                break;
        }
    }

    /**
     * 更新收银员
     */
    private void updateSalesperson() {
            this.salesperson.setText(MyKeeper.getInstance().getStaff().name);
    }

    /**
     * 收银员切换
     */
    private void showSalespersonDialog() {
        BruceDialog.showSingleChoiceDialog(R.string.employees, SaleActivity.this, MyKeeper.getInstance().getEmployeesName(), new BruceDialog.OnChoiceItemListener() {
            @Override
            public void onChoiceItem(String itemName) {
                if (!TextUtils.isEmpty(itemName)) {
                    MyKeeper.getInstance().setStaff(itemName);
                    updateSalesperson();
                }
            }
        });
    }

    /**
     * 旧货销售
     */
    private void oldGoodsSale() {
        final OldSaleDialog dialog = new OldSaleDialog(this);
        dialog.setTitle(R.string.old_sale);
        dialog.setOnOldGoodsSaleDialogListener(new OldSaleDialog.OnOldGoodsSaleDialogListener() {
            @Override
            public void saleOldGoods(String goodsType, int num, float price) {
                SaleGoodsInformation oldGoods = new SaleGoodsInformation("");
                oldGoods.amount = num;
                oldGoods.price = price;
                oldGoods.totalPrice = price * num;
                oldGoods.goodsClassName = goodsType;
                oldGoods.id = getString(R.string.no_code_old_id);
                dialog.dismiss();
                queryOldGoodsInfo(oldGoods);        //添加旧货
            }
        });
        dialog.show();
    }

    /**
     * 结算
     */
    private void saleSettlement() {
        Intent intent = new Intent(this, SettlementActivity.class);
        intent.putParcelableArrayListExtra("groupMsgs", viewModel.getGoodsInformationList());
        startActivityForResult(intent, 2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != 2) {
            return;
        }
        switch (resultCode) {
            case 0:
                break;
            case 1:     //收银完成，开始新的收银
                codeInput.setText("");
                viewModel.startNewCashier();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
//        scanner.scannerClose();
        super.onDestroy();
    }
}
