package com.nicolas.shebangscashier.ui.shopmanage;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.Html;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.nicolas.componentlibrary.datetimepicker.DateTimePickerDialog;
import com.nicolas.componentlibrary.multileveltree.TreeNode;
import com.nicolas.componentlibrary.multileveltree.TreeNodeViewDialog;
import com.nicolas.componentlibrary.pullrefresh.PullRefreshListView;
import com.nicolas.componentlibrary.pullrefreshswipemenu.PullRefreshSwipeMenuListView;
import com.nicolas.shebangscashier.BaseActivity;
import com.nicolas.shebangscashier.R;
import com.nicolas.shebangscashier.cashier.MyKeeper;
import com.nicolas.shebangscashier.common.InventoryDataAdapter;
import com.nicolas.shebangscashier.common.OperateResult;
import com.nicolas.toollibrary.BruceDialog;
import com.nicolas.toollibrary.Tool;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class InventoryActivity extends BaseActivity implements View.OnClickListener {

    private DrawerLayout drawerLayout;

    private TextView user;
    private TextView havInventory, notInventory, priceTotal;
    private TextView goodsClassType, sId, code, newGoods, storageTime;
    private RadioGroup isInventoryChip, codeStatusChip, seasonChip;

    private boolean isCodeStatusChipClear = false;
    private boolean isInventoryChipClear = false;
    private boolean isSeasonChipClear = false;

    private InventoryViewModel viewModel;
    private InventoryDataAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        this.viewModel = new ViewModelProvider(this).get(InventoryViewModel.class);

        this.drawerLayout = findViewById(R.id.drawer_layout);
        this.user = findClickView(R.id.user);
        this.updateUser();

        this.havInventory = findViewById(R.id.havInventory);
        this.notInventory = findViewById(R.id.notInventory);
        this.priceTotal = findViewById(R.id.priceTotal);

        PullRefreshSwipeMenuListView pullToRefreshListView = findViewById(R.id.listView);
        this.adapter = new InventoryDataAdapter(this, this.viewModel.getListData());
        pullToRefreshListView.setAdapter(this.adapter);
        pullToRefreshListView.enablePullRefresh();
        pullToRefreshListView.enablePushLoadMore();
        pullToRefreshListView.setOnLoadingMoreListener(new PullRefreshListView.OnLoadingMoreListener() {
            @Override
            public void onLoadingMore() {
                viewModel.loadMoreData();
            }
        });

        pullToRefreshListView.setOnRefreshListener(new PullRefreshListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                viewModel.refreshData();
            }
        });

        //查询条件
        this.goodsClassType = findClickView(R.id.goodsClassType);
        this.sId = findClickView(R.id.sId);
        this.code = findClickView(R.id.code);
        this.newGoods = findClickView(R.id.newGoods);
        this.storageTime = findClickView(R.id.storageTime);
        this.updateStorageTime(viewModel.getCondition().getStorageTime().replace("~", "\u3000~\u3000"));

        this.isInventoryChip = findViewById(R.id.isInventoryChip);
        this.isInventoryChip.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (isInventoryChipClear) {
                    isInventoryChipClear = false;
                    return;
                }
                switch (checkedId) {
                    case R.id.payChip0:     //未盘点
                        viewModel.getCondition().setInventory("0");
                        break;
                    case R.id.payChip1:     //已盘点
                        viewModel.getCondition().setInventory("1");
                        break;
                    default:
                        break;
                }
            }
        });

        this.codeStatusChip = findViewById(R.id.codeStatusChip);
        this.codeStatusChip.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (isCodeStatusChipClear) {
                    isCodeStatusChipClear = false;
                    return;
                }
                switch (checkedId) {
                    case R.id.payChip0:     //正常
                        viewModel.getCondition().setCodeStatus("正常");
                        break;
                    case R.id.payChip1:     //调货中
                        viewModel.getCondition().setCodeStatus("调货中");
                        break;
                    case R.id.payChip2:     //调货市场
                        viewModel.getCondition().setCodeStatus("调货市场");
                        break;
                    default:
                        break;
                }
            }
        });

        this.seasonChip = findViewById(R.id.seasonChip);
        this.seasonChip.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (isSeasonChipClear) {
                    isSeasonChipClear = false;
                    return;
                }
                switch (checkedId) {
                    case R.id.seasonChip0:
                        viewModel.getCondition().setSeasonChip("春装");
                        break;
                    case R.id.seasonChip1:
                        viewModel.getCondition().setSeasonChip("夏装");
                        break;
                    case R.id.seasonChip2:
                        viewModel.getCondition().setSeasonChip("秋装");
                        break;
                    case R.id.seasonChip3:
                        viewModel.getCondition().setSeasonChip("冬装");
                        break;
                }
            }
        });

        findClickView(R.id.goodsClassTypeClear);
        findClickView(R.id.sIdClear);
        findClickView(R.id.codeClear);
        findClickView(R.id.newGoodsClear);
        findClickView(R.id.isInventoryClear);
        findClickView(R.id.codeStatusClear);
        findClickView(R.id.seasonClear);
        findClickView(R.id.storageTimeClear);
        findClickView(R.id.reset);
        findClickView(R.id.query);

        findClickView(R.id.startInventory);

        //监听
        viewModel.getQueryResult().observe(this, new Observer<OperateResult>() {
            @Override
            public void onChanged(OperateResult operateResult) {
                if (operateResult.getSuccess() != null) {
                    adapter.notifyDataSetChanged();
                    Message msg = operateResult.getSuccess().getMessage();
                    if (msg != null) {
                        BruceDialog.showPromptDialog(InventoryActivity.this, (String) msg.obj);
                    }
                }

                if (operateResult.getError() != null) {
                    BruceDialog.showPromptDialog(InventoryActivity.this,
                            operateResult.getError().getErrorMsg());
                    if (pullToRefreshListView.isPushLoadingMore()) {
                        pullToRefreshListView.loadMoreFinish();
                    }
                    if (pullToRefreshListView.isPullToRefreshing()) {
                        pullToRefreshListView.refreshFinish();
                    }
                    //更新查询日期
                    String queryDate = viewModel.getCondition().getStorageTime();
                    if (TextUtils.isEmpty(queryDate)) {
                        String nowDate = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).format(new Date());
                        queryDate = nowDate + "~" + nowDate;
                    }
                    pullToRefreshListView.updateContentDate(queryDate);
                }
                dismissProgressDialog();
            }
        });
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.user:
                showSalespersonDialog();
                break;
            case R.id.goodsClassType:
                showGoodsClassTypeDialog();
                break;
            case R.id.sId:
                showSidDialog();
                break;
            case R.id.code:
                showCodeInputDialog();
                break;
            case R.id.newGoods:
                showNewGoodsDialog();
                break;
            case R.id.goodsClassTypeClear:
                goodsClassTypeClear();
                break;
            case R.id.sIdClear:
                sIdClear();
                break;
            case R.id.codeClear:
                codeInputClear();
                break;
            case R.id.newGoodsClear:
                newGoodsClear();
                break;
            case R.id.isInventoryClear:
                isInventoryClear();
                break;
            case R.id.codeStatusClear:
                codeStatusClear();
                break;
            case R.id.seasonClear:
                seasonClear();
                break;
            case R.id.storageTime:
                storageTimeChoice(viewModel.getCondition().getStorageTime());
                break;
            case R.id.storageTimeClear:
                storageTimeReset();
                break;
            case R.id.reset:
                reset();
                break;
            case R.id.query:
                if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
                    drawerLayout.closeDrawer(Gravity.RIGHT, true);
                }
                query();
                break;
            case R.id.startInventory:       //开始盘点
                startInventory();
                break;
            default:
                break;
        }
    }

    /**
     * 开始盘点
     */
    private void startInventory() {
        Intent intent = new Intent(this, InventoryGoodsActivity.class);
        startActivity(intent);
    }

    /**
     * 收银员切换
     */
    private void showSalespersonDialog() {
        BruceDialog.showSingleChoiceDialog(R.string.employees, InventoryActivity.this, MyKeeper.getInstance().getEmployeesName(), new BruceDialog.OnChoiceItemListener() {
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

    /**
     * 更新商品类型
     *
     * @param itemValue 商品类型
     */
    private void updateGoodsClassType(String itemValue) {
        String value = getString(R.string.goodsClassType) + "\u3000\u3000\u3000\u3000\u3000" + "<font color=\"black\">" + itemValue + "</font>";
        goodsClassType.setText(Html.fromHtml(value, Html.FROM_HTML_MODE_COMPACT));
    }

    /**
     * 商品类型对话框
     */
    private void showGoodsClassTypeDialog() {
        TreeNodeViewDialog.showTreeNodeViewDialog(this, getString(R.string.goodsClassChoice),
                MyKeeper.getInstance().getGoodsClassTree(), false, new TreeNodeViewDialog.OnTreeNodeViewDialogListener() {
                    @Override
                    public void onChoice(List<TreeNode> node) {
                        if (node != null && node.size() > 0) {
                            updateGoodsClassType((node.size() == 1 ? node.get(0).name : node.get(0).name + "..."));
                            for (TreeNode attr : node) {
                                viewModel.getCondition().setGoodsClassType(attr.id);
                            }
                        }
                    }
                });
    }

    /**
     * 清空商品类型
     */
    private void goodsClassTypeClear() {
        viewModel.getCondition().setGoodsClassType("");
        MyKeeper.getInstance().clearGoodsClassSelect();
        updateGoodsClassType("");
    }

    /**
     * 更新供应商编号
     *
     * @param itemValue 供应商编号
     */
    private void updateSId(String itemValue) {
        String value = getString(R.string.sId) + "\u3000\u3000\u3000\u3000\u3000" + "<font color=\"black\">" + itemValue + "</font>";
        sId.setText(Html.fromHtml(value, Html.FROM_HTML_MODE_COMPACT));
    }

    /**
     * 供应商编号对话框
     */
    private void showSidDialog() {
        BruceDialog.showEditInputDialog(R.string.sId, R.string.sIdInput, InputType.TYPE_CLASS_TEXT, this, new BruceDialog.OnInputFinishListener() {
            @Override
            public void onInputFinish(String itemName) {
                updateSId(itemName);
                viewModel.getCondition().setsIdInput(itemName);
            }
        });
    }

    /**
     * 清空供应商编号
     */
    private void sIdClear() {
        viewModel.getCondition().setsIdInput("");
        updateSId("");
    }

    /**
     * 更新商品条码输入
     *
     * @param itemValue 商品条码输入
     */
    private void updateCodeInput(String itemValue) {
        String value = getString(R.string.goods_code) + "\u3000\u3000\u3000\u3000\u3000" + "<font color=\"black\">" + itemValue + "</font>";
        code.setText(Html.fromHtml(value, Html.FROM_HTML_MODE_COMPACT));
    }

    /**
     * 商品条码输入对话框
     */
    private void showCodeInputDialog() {
        BruceDialog.showEditInputDialog(R.string.goods_code, R.string.goodsCodeInput, InputType.TYPE_CLASS_TEXT, this, new BruceDialog.OnInputFinishListener() {
            @Override
            public void onInputFinish(String itemName) {
                updateCodeInput(itemName);
                viewModel.getCondition().setCodeInput(itemName);
            }
        });
    }

    /**
     * 清空条码输入
     */
    private void codeInputClear() {
        viewModel.getCondition().setCodeInput("");
        updateCodeInput("");
    }

    /**
     * 更新新货号
     *
     * @param itemValue 新货号
     */
    private void updateNewGoods(String itemValue) {
        String value = getString(R.string.goodsId) + "\u3000\u3000\u3000\u3000\u3000" + "<font color=\"black\">" + itemValue + "</font>";
        newGoods.setText(Html.fromHtml(value, Html.FROM_HTML_MODE_COMPACT));
    }

    /**
     * 新货号对话框
     */
    private void showNewGoodsDialog() {
        BruceDialog.showEditInputDialog(R.string.goodsId, R.string.goodsIdInput, InputType.TYPE_CLASS_TEXT, this, new BruceDialog.OnInputFinishListener() {
            @Override
            public void onInputFinish(String itemName) {
                updateNewGoods(itemName);
                viewModel.getCondition().setNewGoodsInput(itemName);
            }
        });
    }

    /**
     * 清空新货号
     */
    private void newGoodsClear() {
        viewModel.getCondition().setNewGoodsInput("");
        updateNewGoods("");
    }

    /**
     * 更新入库时间
     *
     * @param itemValue 入库时间
     */
    private void updateStorageTime(String itemValue) {
        storageTime.setText(itemValue);
    }

    /**
     * 入库时间选择
     *
     * @param itemValue 入库时间
     */
    private void storageTimeChoice(String itemValue) {
        String start = "";
        String end = "";
        if (!TextUtils.isEmpty(itemValue)) {
            String[] times = itemValue.split("~");
            if (times.length >= 2) {
                start = times[0];
                end = times[1];
            }
        }
        DateTimePickerDialog.showDateSlotPickerDialog(InventoryActivity.this, start, end, new DateTimePickerDialog.OnDateTimeSlotPickListener() {
            @Override
            public void OnDateTimeSlotPick(String start, String end) {
                updateStorageTime((start + "\u3000~\u3000" + end));
                viewModel.getCondition().setStorageTime((start + "~" + end));
            }
        });
    }

    /**
     * 重置入库时间
     */
    private void storageTimeReset() {
        String time = Tool.getNearlyOneDaysDateSlot();
        viewModel.getCondition().setStorageTime(time);
        updateStorageTime(time);
    }

    /**
     * 条码状态清空
     */
    private void codeStatusClear() {
        viewModel.getCondition().setCodeStatus("");
        isCodeStatusChipClear = true;
        codeStatusChip.clearCheck();
    }

    /**
     * 季节装选择清空
     */
    private void seasonClear() {
        viewModel.getCondition().setSeasonChip("");
        isSeasonChipClear = true;
        seasonChip.clearCheck();
    }

    /**
     * 是否盘点清空
     */
    private void isInventoryClear() {
        viewModel.getCondition().setInventory("");
        isInventoryChipClear = true;
        isInventoryChip.clearCheck();
    }

    /**
     * 重置所有条件
     */
    private void reset() {
        goodsClassTypeClear();
        sIdClear();
        codeInputClear();
        newGoodsClear();
        isInventoryClear();
        codeStatusClear();
        seasonClear();
        storageTimeReset();
    }

    /**
     * 查询
     */
    private void query() {
        showProgressDialog(getString(R.string.querying));
        viewModel.query();
    }
}
