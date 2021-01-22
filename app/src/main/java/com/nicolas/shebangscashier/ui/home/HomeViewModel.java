package com.nicolas.shebangscashier.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.nicolas.shebangscashier.R;
import com.nicolas.shebangscashier.app.MyApp;
import com.nicolas.shebangscashier.common.ModuleNavigation;
import com.nicolas.shebangscashier.common.OperateInUserView;
import com.nicolas.shebangscashier.common.OperateResult;
import com.nicolas.shebangscashier.ui.cash.NewGoodsSaleStatisticsActivity;
import com.nicolas.shebangscashier.ui.cash.SaleActivity;
import com.nicolas.shebangscashier.ui.cash.SaleDayStatisticsActivity;
import com.nicolas.shebangscashier.ui.cash.SaleReceiptActivity;

import java.util.ArrayList;
import java.util.List;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<OperateResult> updateNavNumResult;
    private List<ModuleNavigation> content = new ArrayList<>();

    public HomeViewModel() {
        this.updateNavNumResult = new MutableLiveData<>();

        content.add(new ModuleNavigation(false, MyApp.getInstance().getString(R.string.nav_cashier_cashier), R.drawable.ic_cashier, SaleActivity.class));
        content.add(new ModuleNavigation(false, MyApp.getInstance().getString(R.string.nav_cashier_bill_query), R.drawable.ic_bill_query, SaleReceiptActivity.class));
//        content.add(new ModuleNavigation(false, MyApp.getInstance().getString(R.string.nav_cashier_vip_query), R.drawable.ic_vip_query, null));
        content.add(new ModuleNavigation(false, MyApp.getInstance().getString(R.string.nav_cashier_new_sales_query), R.drawable.ic_sales_new, NewGoodsSaleStatisticsActivity.class));
        content.add(new ModuleNavigation(false, MyApp.getInstance().getString(R.string.nav_cashier_old_sales_query), R.drawable.ic_sales_old, null));
        content.add(new ModuleNavigation(false, MyApp.getInstance().getString(R.string.nav_cashier_sales_statistics), R.drawable.ic_sale_statistics, SaleDayStatisticsActivity.class));
        content.add(new ModuleNavigation(false, MyApp.getInstance().getString(R.string.nav_cashier_remittance), R.drawable.ic_remittance, null));
        content.add(new ModuleNavigation(false, MyApp.getInstance().getString(R.string.nav_cashier_refunds), R.drawable.ic_refunds, null));
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