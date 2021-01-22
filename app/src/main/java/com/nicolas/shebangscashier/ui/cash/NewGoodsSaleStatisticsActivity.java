package com.nicolas.shebangscashier.ui.cash;

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
import com.nicolas.componentlibrary.pullrefreshswipemenu.PullRefreshSwipeMenuListView;
import com.nicolas.shebangscashier.BaseActivity;
import com.nicolas.shebangscashier.R;
import com.nicolas.shebangscashier.cashier.MyKeeper;
import com.nicolas.shebangscashier.common.NewGoodsSaleStatisticsAdapter;
import com.nicolas.shebangscashier.common.OperateResult;
import com.nicolas.toollibrary.BruceDialog;
import com.nicolas.toollibrary.Tool;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NewGoodsSaleStatisticsActivity extends BaseActivity implements View.OnClickListener {

    private DrawerLayout drawerLayout;

    private TextView user;
    private TextView numCount, priceCount;
    private TextView goodsClassType, sIdInput, vipPhone, codeInput, newGoodsInput, receiptCode, saleTime;
    private RadioGroup payChip, seasonChip;

    private NewGoodsSaleStatisticsAdapter adapter;
    private NewGoodsSaleStatisticsViewModel viewModel;

    private boolean isPayChipClear = false;
    private boolean isSeasonChipClear = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_goods_sale_statistics);
        this.viewModel = new ViewModelProvider(this).get(NewGoodsSaleStatisticsViewModel.class);

        this.drawerLayout = findViewById(R.id.drawer_layout);
        this.user = findClickView(R.id.user);
        this.updateUser();

        this.numCount = findViewById(R.id.numCount);
        this.priceCount = findViewById(R.id.priceCount);

        PullRefreshSwipeMenuListView pullToRefreshListView = findViewById(R.id.pullToRefreshListView);
        this.adapter = new NewGoodsSaleStatisticsAdapter(this, this.viewModel.getListData());
        pullToRefreshListView.setAdapter(this.adapter);

        //查询条件
        this.goodsClassType = findClickView(R.id.goodsClassType);
        this.sIdInput = findClickView(R.id.sIdInput);
        this.vipPhone = findClickView(R.id.vipPhone);
        this.codeInput = findClickView(R.id.codeInput);
        this.newGoodsInput = findClickView(R.id.newGoodsInput);
        this.receiptCode = findClickView(R.id.receiptCode);
        this.saleTime = findClickView(R.id.saleTime);
        this.updateSaleTime(viewModel.getCondition().getSaleTime().replace("~", "\u3000~\u3000"));

        this.payChip = findViewById(R.id.payChip);
        this.payChip.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (isPayChipClear) {
                    isPayChipClear = false;
                    return;
                }
                switch (checkedId) {
                    case R.id.payChip0:     //微信
                        viewModel.getCondition().setPayMethod("微信");
                        break;
                    case R.id.payChip1:     //支付宝
                        viewModel.getCondition().setPayMethod("支付宝");
                        break;
                    case R.id.payChip2:     //现金
                        viewModel.getCondition().setPayMethod("现金");
                        break;
                    case R.id.payChip3:     //刷卡
                        viewModel.getCondition().setPayMethod("刷卡");
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
        findClickView(R.id.vipPhoneClear);
        findClickView(R.id.codeInputClear);
        findClickView(R.id.newGoodsClear);
        findClickView(R.id.receiptCodeClear);
        findClickView(R.id.payClear);
        findClickView(R.id.seasonClear);
        findClickView(R.id.saleTimeClear);
        findClickView(R.id.reset);
        findClickView(R.id.query);

        //监听查询结果
        viewModel.getQuerySaleResult().observe(this, new Observer<OperateResult>() {
            @Override
            public void onChanged(OperateResult operateResult) {
                if (operateResult.getSuccess() != null) {
                    adapter.notifyDataSetChanged();
                    Message msg = operateResult.getSuccess().getMessage();
                    if (msg != null) {
                        BruceDialog.showPromptDialog(NewGoodsSaleStatisticsActivity.this, (String) msg.obj);
                    } else {
                        viewModel.queryStatistics();
                    }
                }

                if (operateResult.getError() != null) {
                    BruceDialog.showPromptDialog(NewGoodsSaleStatisticsActivity.this,
                            operateResult.getError().getErrorMsg());
                    if (pullToRefreshListView.isPushLoadingMore()) {
                        pullToRefreshListView.loadMoreFinish();
                    }
                    if (pullToRefreshListView.isPullToRefreshing()) {
                        pullToRefreshListView.refreshFinish();
                    }
                    //更新查询日期
                    String queryDate = viewModel.getCondition().getSaleTime();
                    if (TextUtils.isEmpty(queryDate)) {
                        String nowDate = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).format(new Date());
                        queryDate = nowDate + "~" + nowDate;
                    }
                    pullToRefreshListView.updateContentDate(queryDate);
                    dismissProgressDialog();
                }
            }
        });

        //监听统计查询结果
        viewModel.getQueryStatisticsResult().observe(this, new Observer<OperateResult>() {
            @Override
            public void onChanged(OperateResult operateResult) {
                if (operateResult.getError() != null) {
                    BruceDialog.showPromptDialog(NewGoodsSaleStatisticsActivity.this,
                            operateResult.getError().getErrorMsg());
                }
                updateStatistics();
                dismissProgressDialog();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.new_goods_statistics_menu, menu);
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
            case R.id.vipPhone:
                showVipPhoneDialog();
                break;
            case R.id.codeInput:
                showCodeInputDialog();
                break;
            case R.id.newGoods:
                showNewGoodsDialog();
                break;
            case R.id.receiptCode:
                showReceiptCodeDialog();
                break;
            case R.id.goodsClassTypeClear:
                goodsClassTypeClear();
                break;
            case R.id.sIdClear:
                sIdClear();
                break;
            case R.id.vipPhoneClear:
                vipPhoneClear();
                break;
            case R.id.codeInputClear:
                codeInputClear();
                break;
            case R.id.newGoodsClear:
                newGoodsClear();
                break;
            case R.id.receiptCodeClear:
                receiptCodeClear();
                break;
            case R.id.payClear:
                payClear();
                break;
            case R.id.seasonClear:
                seasonClear();
                break;
            case R.id.saleTime:
                saleTimeChoice(viewModel.getCondition().getSaleTime());
                break;
            case R.id.saleTimeClear:
                saleTimeReset();
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
            default:
                break;
        }
    }

    /**
     * 收银员切换
     */
    private void showSalespersonDialog() {
        BruceDialog.showSingleChoiceDialog(R.string.employees, NewGoodsSaleStatisticsActivity.this, MyKeeper.getInstance().getEmployeesName(), new BruceDialog.OnChoiceItemListener() {
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
     * 会员手机对话框
     */
    private void showVipPhoneDialog() {
        BruceDialog.showEditInputDialog(R.string.vipPhone, R.string.vipPhoneInput, InputType.TYPE_CLASS_TEXT, this, new BruceDialog.OnInputFinishListener() {
            @Override
            public void onInputFinish(String itemName) {
                updateVipPhone(itemName);
                viewModel.getCondition().setVipPhone(itemName);
            }
        });
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
     * 小票编号对话框
     */
    private void showReceiptCodeDialog() {
        BruceDialog.showEditInputDialog(R.string.receipt, R.string.receiptInput, InputType.TYPE_CLASS_TEXT, this, new BruceDialog.OnInputFinishListener() {
            @Override
            public void onInputFinish(String itemName) {
                updateReceiptCode(itemName);
                viewModel.getCondition().setReceiptCode(itemName);
            }
        });
    }

    private void goodsClassTypeClear() {
        viewModel.getCondition().setGoodsClassType("");
        MyKeeper.getInstance().clearGoodsClassSelect();
        updateGoodsClassType("");
    }

    private void updateSId(String itemValue) {
        String value = getString(R.string.sId) + "\u3000\u3000\u3000\u3000\u3000" + "<font color=\"black\">" + itemValue + "</font>";
        sIdInput.setText(Html.fromHtml(value, Html.FROM_HTML_MODE_COMPACT));
    }

    private void sIdClear() {
        viewModel.getCondition().setsIdInput("");
        updateSId("");
    }

    private void updateVipPhone(String itemValue) {
        String value = getString(R.string.vipPhone) + "\u3000\u3000\u3000\u3000\u3000" + "<font color=\"black\">" + itemValue + "</font>";
        vipPhone.setText(Html.fromHtml(value, Html.FROM_HTML_MODE_COMPACT));
    }

    private void vipPhoneClear() {
        viewModel.getCondition().setVipPhone("");
        updateVipPhone("");
    }

    private void updateCodeInput(String itemValue) {
        String value = getString(R.string.goods_code) + "\u3000\u3000\u3000\u3000\u3000" + "<font color=\"black\">" + itemValue + "</font>";
        codeInput.setText(Html.fromHtml(value, Html.FROM_HTML_MODE_COMPACT));
    }

    private void codeInputClear() {
        viewModel.getCondition().setCodeInput("");
        updateCodeInput("");
    }

    private void updateNewGoods(String itemValue) {
        String value = getString(R.string.goodsId) + "\u3000\u3000\u3000\u3000\u3000" + "<font color=\"black\">" + itemValue + "</font>";
        newGoodsInput.setText(Html.fromHtml(value, Html.FROM_HTML_MODE_COMPACT));
    }

    private void newGoodsClear() {
        viewModel.getCondition().setNewGoodsInput("");
        updateNewGoods("");
    }

    private void updateReceiptCode(String itemValue) {
        String value = getString(R.string.receipt) + "\u3000\u3000\u3000\u3000\u3000" + "<font color=\"black\">" + itemValue + "</font>";
        receiptCode.setText(Html.fromHtml(value, Html.FROM_HTML_MODE_COMPACT));
    }

    private void receiptCodeClear() {
        viewModel.getCondition().setReceiptCode("");
        updateReceiptCode("");
    }

    private void payClear() {
        viewModel.getCondition().setPayMethod("");
        isPayChipClear = true;
        payChip.clearCheck();
    }

    private void seasonClear() {
        viewModel.getCondition().setSeasonChip("");
        isSeasonChipClear = true;
        seasonChip.clearCheck();
    }

    private void saleTimeChoice(String itemValue) {
        String start = "";
        String end = "";
        if (!TextUtils.isEmpty(itemValue)) {
            String[] times = itemValue.split("~");
            if (times.length >= 2) {
                start = times[0];
                end = times[1];
            }
        }
        DateTimePickerDialog.showDateSlotPickerDialog(NewGoodsSaleStatisticsActivity.this, start, end, new DateTimePickerDialog.OnDateTimeSlotPickListener() {
            @Override
            public void OnDateTimeSlotPick(String start, String end) {
                updateSaleTime((start + "\u3000~\u3000" + end));
                viewModel.getCondition().setSaleTime((start + "~" + end));
            }
        });
    }

    private void updateSaleTime(String itemValue) {
        saleTime.setText(itemValue);
    }

    private void saleTimeReset() {
        String time = Tool.getNearlyOneDaysDateSlot();
        viewModel.getCondition().setSaleTime(time);
        updateSaleTime(time);
    }

    private void updateStatistics() {
        String numCountValue = "数量合计：￥" + viewModel.getSaleNum();
        String priceCountValue = "售价合计：￥" + viewModel.getSaleTotalPrice();

        numCount.setText(numCountValue);
        priceCount.setText(priceCountValue);
    }

    private void reset() {
        goodsClassTypeClear();
        sIdClear();
        vipPhoneClear();
        codeInputClear();
        newGoodsClear();
        receiptCodeClear();
        payClear();
        seasonClear();
        saleTimeReset();
    }

    private void query() {
        showProgressDialog(getString(R.string.querying));
        viewModel.query();
    }
}
