package com.nicolas.shebangscashier.ui.manage;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.nicolas.shebangscashier.R;
import com.nicolas.shebangscashier.app.MyApp;
import com.nicolas.shebangscashier.common.ModuleNavigation;
import com.nicolas.shebangscashier.common.OperateInUserView;
import com.nicolas.shebangscashier.common.OperateResult;
import com.nicolas.shebangscashier.ui.cash.SaleActivity;
import com.nicolas.shebangscashier.ui.cash.StoreReceiptActivity;
import com.nicolas.shebangscashier.ui.shopmanage.GoodsTransferActivity;
import com.nicolas.shebangscashier.ui.shopmanage.InventoryActivity;
import com.nicolas.shebangscashier.ui.shopmanage.InventoryGoodsActivity;
import com.nicolas.shebangscashier.ui.shopmanage.QualityManagementActivity;
import com.nicolas.shebangscashier.ui.shopmanage.ReceiveTransferActivity;
import com.nicolas.shebangscashier.ui.shopmanage.ReturnPrintActivity;

import java.util.ArrayList;
import java.util.List;

public class ManageFragmentViewModel extends ViewModel {

    private MutableLiveData<OperateResult> updateNavNumResult;
    private List<ModuleNavigation> content = new ArrayList<>();

    public ManageFragmentViewModel() {
        this.updateNavNumResult = new MutableLiveData<>();

        content.add(new ModuleNavigation(false, MyApp.getInstance().getString(R.string.nav_cashier_inventory), R.drawable.ic_inventory, InventoryGoodsActivity.class));
        content.add(new ModuleNavigation(false, MyApp.getInstance().getString(R.string.nav_cashier_inventory_query), R.drawable.ic_inventory_query, InventoryActivity.class));
        content.add(new ModuleNavigation(false, MyApp.getInstance().getString(R.string.nav_cashier_receiver), R.drawable.ic_receiver, StoreReceiptActivity.class));
        content.add(new ModuleNavigation(false, MyApp.getInstance().getString(R.string.nav_cashier_transfer_in), R.drawable.ic_transfer, GoodsTransferActivity.class));
        content.add(new ModuleNavigation(false, MyApp.getInstance().getString(R.string.nav_cashier_transfer_out), R.drawable.ic_transfer, ReceiveTransferActivity.class));
        content.add(new ModuleNavigation(false, MyApp.getInstance().getString(R.string.nav_cashier_back_print), R.drawable.ic_return_goods, ReturnPrintActivity.class));
        content.add(new ModuleNavigation(false, MyApp.getInstance().getString(R.string.nav_quality_managemeng), R.drawable.ic_return_goods, QualityManagementActivity.class));
    }

    public LiveData<OperateResult> getUpdateNavNumResult() {
        return updateNavNumResult;
    }

    /**
     * 更新ModuleNavigation通知数字
     *
     * @param position position
     * @param num      num
     */
    public void updateModuleNoticeNum(int position, int num) {
        ModuleNavigation item = content.get(position);
        if (item.getNavigationNum() != num) {
            item.setNavigationNum(num);
            updateNavNumResult.setValue(new OperateResult(new OperateInUserView(null)));
        }
    }

    public ModuleNavigation getModuleNavigation(int position) {
        return content.get(position);
    }

    public List<ModuleNavigation> getModuleNavigationList() {
        return content;
    }
}