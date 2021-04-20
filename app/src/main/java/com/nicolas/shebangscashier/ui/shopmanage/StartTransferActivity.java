package com.nicolas.shebangscashier.ui.shopmanage;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
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
import com.nicolas.scannerlibrary.PPdaScanner;
import com.nicolas.scannerlibrary.Scanner;
import com.nicolas.shebangscashier.BaseActivity;
import com.nicolas.shebangscashier.R;
import com.nicolas.shebangscashier.common.OperateResult;
import com.nicolas.shebangscashier.common.StartTransferAdapter;
import com.nicolas.toollibrary.BruceDialog;
import com.nicolas.toollibrary.Tool;

public class StartTransferActivity extends BaseActivity implements View.OnClickListener {

    private StartTransferViewModel viewModel;
    private TextView acceptanceShop;
    private EditText transferCode;
    private Button submitAll;

    private Scanner scanner;                        //扫描头
    private StartTransferAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_transfer);

        this.viewModel = new ViewModelProvider(this).get(StartTransferViewModel.class);

        //初始化扫描头
        scanner = new PPdaScanner(this);
        //广播接收方式
        scanner.setOnScannerScanResultListener(new Scanner.OnScannerScanResultListener() {
            @Override
            public void scanResult(String scan) {
                handlerBarCodeInput(scan);
            }
        });
        this.acceptanceShop = findClickView(R.id.acceptanceShop);
        this.transferCode = findViewById(R.id.transferCode);
        this.transferCode.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT) {
                    handlerBarCodeInput(transferCode.getText().toString());
                }
                return false;
            }
        });
        //按键监听方式
        this.transferCode.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (KeyEvent.KEYCODE_ENTER == keyCode && event.getAction() == KeyEvent.ACTION_UP) {
                    handlerBarCodeInput(transferCode.getText().toString());
                    return true;
                }
                return false;
            }
        });
        this.transferCode.requestFocus();

        //SwipeMenuListView
        SwipeMenuListView listView = findViewById(R.id.swipeMenuListView);
        this.adapter = new StartTransferAdapter(this, viewModel.getDataList());
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
                detailsItem.setWidth(Tool.dp2px(StartTransferActivity.this, 90));
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
                deleteItem.setWidth(Tool.dp2px(StartTransferActivity.this, 90));
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

        //提交
        submitAll = findClickView(R.id.submitAll);
        updateSubmitStatus();

        //监听调货提交结果
        viewModel.getSubmitResult().observe(this, new Observer<OperateResult>() {
            @Override
            public void onChanged(OperateResult operateResult) {
                if (operateResult.getSuccess() != null) {
                    Toast.makeText(StartTransferActivity.this, getString(R.string.submitSuccess), Toast.LENGTH_SHORT).show();
                    adapter.notifyDataSetChanged();
                }
                if (operateResult.getError() != null) {
                    Toast.makeText(StartTransferActivity.this, operateResult.getError().getErrorMsg(), Toast.LENGTH_SHORT).show();
                }
                dismissProgressDialog();
            }
        });

        //监听查询
        viewModel.getQueryResult().observe(this, new Observer<OperateResult>() {
            @Override
            public void onChanged(OperateResult operateResult) {
                if (operateResult.getSuccess() != null) {
                    Toast.makeText(StartTransferActivity.this, getString(R.string.submitSuccess), Toast.LENGTH_SHORT).show();
                    adapter.notifyDataSetChanged();
                    updateSubmitStatus();
                }
                if (operateResult.getError() != null) {
                    Toast.makeText(StartTransferActivity.this, operateResult.getError().getErrorMsg(), Toast.LENGTH_SHORT).show();
                }

                dismissProgressDialog();
            }
        });
    }

    /**
     * 提交状态更新
     */
    private void updateSubmitStatus() {
        if (viewModel.getDataList().size() == 0) {
            if (submitAll.isClickable()) {
                submitAll.setBackground(getDrawable(R.drawable.shape_rectangle_grey));
                submitAll.setClickable(false);
            }
        } else {
            if (!submitAll.isClickable()) {
                submitAll.setBackground(getDrawable(R.drawable.shape_rectangle_red));
                submitAll.setClickable(true);
            }
        }
    }

    /**
     * 删除一条记录
     *
     * @param position position
     */
    private void deleteGoods(int position) {
        viewModel.deleteGoods(position);
        adapter.notifyDataSetChanged();
        updateSubmitStatus();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.submitAll:
                viewModel.submitAll();
                break;
            case R.id.acceptanceShop:
                BruceDialog.showEditInputDialog(getString(R.string.acceptanceShop), getString(R.string.input_acceptance), InputType.TYPE_CLASS_TEXT,
                        this, new BruceDialog.OnInputFinishListener() {
                            @Override
                            public void onInputFinish(String itemName) {
                                if (!TextUtils.isEmpty(itemName)) {
                                    acceptanceShop.setText(itemName);
                                }
                            }
                        }
                );
                break;
            default:
                break;
        }
    }

    /**
     * 处理barCode输入
     *
     * @param barCode 条码
     */
    private void handlerBarCodeInput(String barCode) {
        String fid = acceptanceShop.getText().toString();
        if (TextUtils.isEmpty(fid)) {
            BruceDialog.showPromptDialog(this, getString(R.string.choiceFidFirst));
            return;
        }
        this.transferCode.setText("");
        super.showProgressDialog(getString(R.string.querying));
        this.viewModel.queryBarCode(fid, barCode);
    }
}
