package com.nicolas.shebangscashier.ui.shopmanage;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

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
import com.nicolas.shebangscashier.common.EditTextListAdapter;
import com.nicolas.shebangscashier.common.GoodsTransferDataAdapter;
import com.nicolas.shebangscashier.common.OperateResult;
import com.nicolas.shebangscashier.common.ReplenishmentInformation;
import com.nicolas.shebangscashier.ui.cash.StoreReceiptActivity;
import com.nicolas.toollibrary.BruceDialog;
import com.nicolas.toollibrary.Tool;
import com.nicolas.toollibrary.imageload.ImageLoadClass;

import java.util.List;

public class GoodsTransferActivity extends BaseActivity implements View.OnClickListener {

    private DrawerLayout drawerLayout;

    private TextView user, numCount;
    private TextView acceptanceShop, goodsClassID, sId, code, goodsID, transferTime;
    private RadioGroup transferStatusChip;

    private boolean isStatusChipClear = false;

    private GoodsTransferViewModel viewModel;
    private GoodsTransferDataAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods_transfer);

        this.viewModel = new ViewModelProvider(this).get(GoodsTransferViewModel.class);

        this.drawerLayout = findViewById(R.id.drawer_layout);
        this.user = findClickView(R.id.user);
        this.updateUser();
        this.numCount = findViewById(R.id.numCount);

        //查询条件
        this.acceptanceShop = findClickView(R.id.acceptanceShop);
        this.goodsClassID = findClickView(R.id.goodsClassID);
        this.sId = findClickView(R.id.sId);
        this.code = findClickView(R.id.code);
        this.goodsID = findClickView(R.id.goodsID);
        this.transferTime = findClickView(R.id.transferTime);
        this.transferStatusChip = findClickView(R.id.transferStatusChip);


        PullRefreshSwipeMenuListView listView = findViewById(R.id.listView);
        this.adapter = new GoodsTransferDataAdapter(this, viewModel.getDataList(), new GoodsTransferDataAdapter.OnOperateListener() {
            @Override
            public void onOperate(int position) {
                //删除调货
                deleteGoodsTransfer(position);
            }

            @Override
            public void onOperate2(int position) {      //商品补货
                replenishment(position);
            }
        });
        listView.setAdapter(this.adapter);
        listView.setOnLoadingMoreListener(new PullRefreshListView.OnLoadingMoreListener() {
            @Override
            public void onLoadingMore() {
                viewModel.loadMoreData();
            }
        });
        listView.setOnRefreshListener(new PullRefreshListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                viewModel.refreshData();
            }
        });
        listView.enablePushLoadMore();
        listView.enablePullRefresh();


        //监听查询结果
        viewModel.getQueryResult().observe(this, new Observer<OperateResult>() {
            @Override
            public void onChanged(OperateResult operateResult) {
                if (operateResult.getSuccess() != null) {
                    adapter.notifyDataSetChanged();
                    updateCount();
                }
                if (operateResult.getError() != null) {
                    Toast.makeText(GoodsTransferActivity.this, operateResult.getError().getErrorMsg(), Toast.LENGTH_SHORT).show();
                }
                dismissProgressDialog();
                if (listView.isPullToRefreshing()) {
                    listView.refreshFinish();
                }
                if (listView.isPushLoadingMore()) {
                    listView.loadMoreFinish();
                }
            }
        });

        //监听调货取消结果
        viewModel.getDeleteResult().observe(this, new Observer<OperateResult>() {
            @Override
            public void onChanged(OperateResult operateResult) {
                if (operateResult.getSuccess() != null) {
                    adapter.notifyDataSetChanged();
                }
                if (operateResult.getError() != null) {
                    Toast.makeText(GoodsTransferActivity.this, operateResult.getError().getErrorMsg(), Toast.LENGTH_SHORT).show();
                }
                dismissProgressDialog();
            }
        });

        //监听补货查询结果
        viewModel.getQueryGoodsByGoodsIdResult().observe(this, new Observer<OperateResult>() {
            @Override
            public void onChanged(OperateResult operateResult) {
                if (operateResult.getSuccess() != null) {
                    showReplenishmentDialog(viewModel.getReplenishmentInformation());
                }
                if (operateResult.getError() != null) {
                    BruceDialog.showPromptDialog(GoodsTransferActivity.this,
                            operateResult.getError().getErrorMsg());
                }
                dismissProgressDialog();
            }
        });

        //监听补货提交结果
        viewModel.getReplenishmentSubmitResult().observe(this, new Observer<OperateResult>() {
            @Override
            public void onChanged(OperateResult operateResult) {
                if (operateResult.getSuccess() != null) {
                    Toast.makeText(GoodsTransferActivity.this, "补货成功", Toast.LENGTH_SHORT).show();
                }
                if (operateResult.getError() != null) {
                    BruceDialog.showPromptDialog(GoodsTransferActivity.this,
                            operateResult.getError().getErrorMsg());
                }
                dismissProgressDialog();
            }
        });

        //查询最新数据
        this.query();
    }

    /**
     * 补货对话框
     *
     * @param information 补货信息
     */
    private void showReplenishmentDialog(ReplenishmentInformation information) {
        View view = LayoutInflater.from(this).inflate(R.layout.replenishment_dialog, null);
        ImageView photo = view.findViewById(R.id.photo);
        TextView att1 = view.findViewById(R.id.att1);
        TextView att2 = view.findViewById(R.id.att2);
        TextView att3 = view.findViewById(R.id.att3);
        TextView att4 = view.findViewById(R.id.att4);
        TextView att5 = view.findViewById(R.id.att5);
        TextView att6 = view.findViewById(R.id.att6);
        TextView att7 = view.findViewById(R.id.att7);
        TextView att8 = view.findViewById(R.id.att8);
        TextView att10 = view.findViewById(R.id.att10);
        TextView remark = view.findViewById(R.id.remark);
        ListView listView = view.findViewById(R.id.listView);
        ImageLoadClass.getInstance().displayImage(information.img, photo, false);
        att1.setText(("补货分店:" + information.fId));
        att2.setText(("货   号:" + information.goodsId));
        att3.setText(("商家编号:" + information.sId));
        att4.setText(("顾客群:" + information.customer));
        att5.setText(("采购方式:" + information.purchaseType));
        att6.setText(("所属库存:" + information.storeRoomName));
        att7.setText(("货物类别:" + information.goodsClass));
        att8.setText(("季节装:" + information.seasonName));
        att10.setText(("审核人:" + information.accountNameCheck));
        remark.setText(("备注：" + information.remark));

        EditTextListAdapter adapter = new EditTextListAdapter(this, information.properties);
        listView.setAdapter(adapter);
        new AlertDialog.Builder(this).setView(view)
                .setTitle("货号详情")
                .setCancelable(true)
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("确认补货", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showProgressDialog("补货下单中...");
                        viewModel.replenishmentSubmit(information.goodsId, information.properties);
                        dialog.dismiss();
                    }
                })
                .create().show();
    }

    /**
     * 商品补货
     *
     * @param position 补货
     */
    private void replenishment(int position) {
        //补货
        showProgressDialog(getString(R.string.querying));
        viewModel.setCurrentReceiveStoreReceipt(position);
        viewModel.queryReplenishment(viewModel.getCurrentData().goodsId);
    }

    /**
     * 删除调货
     *
     * @param position position
     */
    private void deleteGoodsTransfer(int position) {
        BruceDialog.showWarningDialog(this, getString(R.string.isDelete), new BruceDialog.OnWarningIgnoreListener() {
            @Override
            public void onWarningIgnore() {
                showProgressDialog(getString(R.string.deleteing));
                viewModel.deleteTransferGoods(position);
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
            case R.id.acceptanceShop:
                showAcceptanceShop();
                break;
            case R.id.acceptanceShopClear:
                acceptanceShopClear();
                break;
            case R.id.goodsClassType:
                showGoodsClassIDDialog();
                break;
            case R.id.sId:
                showSidDialog();
                break;
            case R.id.code:
                showCodeDialog();
                break;
            case R.id.newGoods:
                showGoodsIDDialog();
                break;
            case R.id.goodsClassIDClear:
                goodsClassIDClear();
                break;
            case R.id.sIdClear:
                sIdClear();
                break;
            case R.id.codeClear:
                codeInputClear();
                break;
            case R.id.goodsIDClear:
                goodsIDClear();
                break;
            case R.id.transferStatusClear:
                transferStatusClear();
                break;
            case R.id.transferTime:
                transferTimeChoice(viewModel.getCondition().getTransferTime());
                break;
            case R.id.transferTimeClear:
                transferTimeReset();
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
            case R.id.transfer:       //发起调货
                startTransfer();
                break;
            default:
                break;
        }
    }

    /**
     * 根据查询条件查询
     */
    private void query() {
        showProgressDialog(getString(R.string.querying));
        viewModel.query();
    }

    /**
     * 重置所有查询条件
     */
    private void reset() {
        acceptanceShopClear();
        goodsClassIDClear();
        sIdClear();
        codeInputClear();
        goodsIDClear();
        transferStatusClear();
        transferTimeReset();
    }

    /**
     * 更新list统计数量
     */
    private void updateCount() {
        this.numCount.setText(("合计：" + viewModel.getDataList().size()));
    }

    /**
     * 发起调货--跳转到调货页面
     */
    private void startTransfer() {
        Intent intent = new Intent(this, StartTransferActivity.class);
        startActivity(intent);
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
        BruceDialog.showSingleChoiceDialog(R.string.employees, GoodsTransferActivity.this, MyKeeper.getInstance().getEmployeesName(),
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
     * 更新商品类型
     *
     * @param itemValue 商品类型
     */
    private void updateGoodsClassID(String itemValue) {
        String value = getString(R.string.goodsClassType) + "\u3000\u3000\u3000\u3000\u3000" + "<font color=\"black\">" + itemValue + "</font>";
        goodsClassID.setText(Html.fromHtml(value, Html.FROM_HTML_MODE_COMPACT));
    }

    /**
     * 商品类型对话框
     */
    private void showGoodsClassIDDialog() {
        TreeNodeViewDialog.showTreeNodeViewDialog(this, getString(R.string.goodsClassChoice),
                MyKeeper.getInstance().getGoodsClassTree(), false, new TreeNodeViewDialog.OnTreeNodeViewDialogListener() {
                    @Override
                    public void onChoice(List<TreeNode> node) {
                        if (node != null && node.size() > 0) {
                            updateGoodsClassID((node.size() == 1 ? node.get(0).name : node.get(0).name + "..."));
                            for (TreeNode attr : node) {
                                viewModel.getCondition().setGoodsClassId(attr.id);
                            }
                        }
                    }
                });
    }

    /**
     * 清空商品类型
     */
    private void goodsClassIDClear() {
        viewModel.getCondition().setGoodsClassId("");
        MyKeeper.getInstance().clearGoodsClassSelect();
        updateGoodsClassID("");
    }

    /**
     * 显示接收店铺输入对话框
     */
    private void showAcceptanceShop() {
        BruceDialog.showEditInputDialog(getString(R.string.input_acceptance), getString(R.string.shopID), InputType.TYPE_CLASS_TEXT, GoodsTransferActivity.this,
                new BruceDialog.OnInputFinishListener() {
                    @Override
                    public void onInputFinish(String itemName) {
                        if (!itemName.equals(viewModel.getCondition().getAcceptanceShop())) {
                            viewModel.getCondition().setAcceptanceShop(itemName);
                            updateAcceptanceShop(itemName);
                        }
                    }
                });
    }

    /**
     * 更新acceptanceShop
     */
    private void updateAcceptanceShop(String itemValue) {
        String value = getString(R.string.acceptanceShop) + "\u3000\u3000\u3000\u3000\u3000" + "<font color=\"black\">" + itemValue + "</font>";
        acceptanceShop.setText(Html.fromHtml(value, Html.FROM_HTML_MODE_COMPACT));
    }

    /**
     * 接收店铺清空
     */
    private void acceptanceShopClear() {
        viewModel.getCondition().setAcceptanceShop("");
        updateAcceptanceShop("");
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
                viewModel.getCondition().setsId(itemName);
            }
        });
    }

    /**
     * 清空供应商编号
     */
    private void sIdClear() {
        viewModel.getCondition().setsId("");
        updateSId("");
    }

    /**
     * 更新商品条码输入
     *
     * @param itemValue 商品条码输入
     */
    private void updateCode(String itemValue) {
        String value = getString(R.string.goods_code) + "\u3000\u3000\u3000\u3000\u3000" + "<font color=\"black\">" + itemValue + "</font>";
        code.setText(Html.fromHtml(value, Html.FROM_HTML_MODE_COMPACT));
    }

    /**
     * 商品条码输入对话框
     */
    private void showCodeDialog() {
        BruceDialog.showEditInputDialog(R.string.goods_code, R.string.goodsCodeInput, InputType.TYPE_CLASS_TEXT, this, new BruceDialog.OnInputFinishListener() {
            @Override
            public void onInputFinish(String itemName) {
                updateCode(itemName);
                viewModel.getCondition().setBarcodeId(itemName);
            }
        });
    }

    /**
     * 清空条码输入
     */
    private void codeInputClear() {
        viewModel.getCondition().setBarcodeId("");
        updateCode("");
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
                viewModel.getCondition().setGoodsId(itemName);
            }
        });
    }

    /**
     * 清空新货号
     */
    private void goodsIDClear() {
        viewModel.getCondition().setGoodsId("");
        updateGoodsID("");
    }

    /**
     * 更新发起时间
     *
     * @param itemValue 发起时间
     */
    private void updateTransferTime(String itemValue) {
        transferTime.setText(itemValue);
    }

    /**
     * 发起时间选择
     *
     * @param itemValue 发起时间
     */
    private void transferTimeChoice(String itemValue) {
        String start = "";
        String end = "";
        if (!TextUtils.isEmpty(itemValue)) {
            String[] times = itemValue.split("~");
            if (times.length >= 2) {
                start = times[0];
                end = times[1];
            }
        }
        DateTimePickerDialog.showDateSlotPickerDialog(GoodsTransferActivity.this, start, end, new DateTimePickerDialog.OnDateTimeSlotPickListener() {
            @Override
            public void OnDateTimeSlotPick(String start, String end) {
                updateTransferTime((start + "\u3000~\u3000" + end));
                viewModel.getCondition().setTransferTime((start + "~" + end));
            }
        });
    }

    /**
     * 发起时间重置
     */
    private void transferTimeReset() {
        String time = Tool.getNearlyOneDaysDateSlot();
        viewModel.getCondition().setTransferTime(time);
        updateTransferTime(time);
    }

    /**
     * 清空transferStatus
     */
    private void transferStatusClear() {
        viewModel.getCondition().setState("");
        isStatusChipClear = true;
        transferStatusChip.clearCheck();
    }
}
