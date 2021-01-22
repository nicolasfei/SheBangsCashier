package com.nicolas.shebangscashier.ui.set;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.nicolas.shebangscashier.R;
import com.nicolas.shebangscashier.app.MyApp;
import com.nicolas.shebangscashier.common.ModuleNavigation;
import com.nicolas.shebangscashier.common.OperateInUserView;
import com.nicolas.shebangscashier.common.OperateResult;
import com.nicolas.shebangscashier.ui.set.printer.PrinterActivity;

import java.util.ArrayList;
import java.util.List;

public class SetViewModel extends ViewModel {

    private MutableLiveData<OperateResult> updateNavNumResult;
    private List<ModuleNavigation> content = new ArrayList<>();

    public SetViewModel() {
        this.updateNavNumResult = new MutableLiveData<>();
//        content.add(new ModuleNavigation(true, MyApp.getInstance().getString(R.string.nav_dev_manager), 0, null));
//        content.add(new ModuleNavigation(false, WarehouseApp.getInstance().getString(R.string.nav_set_scan), R.drawable.ic_scan_dev, null));
        content.add(new ModuleNavigation(false, MyApp.getInstance().getString(R.string.nav_set_print), R.drawable.ic_printer, PrinterActivity.class));
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