package com.nicolas.shebangscashier.ui.set.printer;

import android.os.Message;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.nicolas.printerlibraryforufovo.OnPrinterDeviceGroupStatusChangeListener;
import com.nicolas.printerlibraryforufovo.PrinterDeviceGroup;
import com.nicolas.printerlibraryforufovo.PrinterManager;
import com.nicolas.shebangscashier.common.OperateInUserView;
import com.nicolas.shebangscashier.common.OperateResult;

import java.util.List;

public class PrinterViewModel extends ViewModel {
    private List<PrinterDeviceGroup> printerDeviceGroups;
    private boolean isSelfOpenOrCloseBluetooth = false;      //是否自己关闭或打开蓝牙
    private boolean isSelfStopScan = false;                  //是否自己停止蓝牙搜索

    private boolean isScanning = false;     //是否在扫描

    /**
     * 监听是否在扫描打印机
     */
    private MutableLiveData<OperateResult> isScanningPrinter;
    /**
     * 监听打印机连接状态改变
     * 连接
     * 连接中
     * 连接成功/失败
     */
    private MutableLiveData<OperateResult> printerStatusResult;
    /**
     * 监听打印机组状态改变
     * 开始扫描
     * 扫描中
     * 扫描到设备
     * 扫描完成
     * 打印机接口开/关
     */
    private MutableLiveData<OperateResult> printerGroupStatusResult;

    public PrinterViewModel() {
        printerGroupStatusResult = new MutableLiveData<>();
        printerStatusResult = new MutableLiveData<>();
        printerDeviceGroups = PrinterManager.getInstance().getPrinterGroup();
        for (int i = 0; i < printerDeviceGroups.size(); i++) {
            PrinterDeviceGroup group = printerDeviceGroups.get(i);      //获得组
            final int groupPosition = i;
            group.setOnPrinterDeviceGroupStatusChangeListener(new OnPrinterDeviceGroupStatusChangeListener() {
                @Override
                public void printerDeviceGroupStatusChange(int statusID, int status) {
                    Message msg = new Message();
                    msg.what = statusID;
                    msg.arg1 = groupPosition;
                    msg.obj = (status==1);
                    printerGroupStatusResult.setValue(new OperateResult(new OperateInUserView(msg)));
                }

                @Override
                public void printerDeviceStatusChange(int childPosition, int deviceStatus, Object status) {
                    Message msg = new Message();
                    msg.what = deviceStatus;
                    msg.arg1 = groupPosition;
                    msg.arg2 = childPosition;
                    msg.obj = status;
                    printerStatusResult.setValue(new OperateResult(new OperateInUserView(msg)));
                }
            });
        }
    }

    public LiveData<OperateResult> getPrinterGroupStatusResult() {
        return printerGroupStatusResult;
    }

    public LiveData<OperateResult> getPrinterStatusResult() {
        return printerStatusResult;
    }

    /**
     * 获取设备组
     *
     * @return 设备组
     */
    public List<PrinterDeviceGroup> getPrinterDeviceGroups() {
        return printerDeviceGroups;
    }

    /**
     * 连接打印机
     *
     * @param groupPosition 组Position
     * @param childPosition 设备Position
     */
    public void connectPrinter(int groupPosition, int childPosition) {
        PrinterManager.getInstance().connectPrinter(groupPosition, childPosition);
    }

    /**
     * 设置组接口
     *
     * @param groupPosition 组
     * @param status        接口状态
     */
    public void setPrinterGroupInterface(int groupPosition, boolean status) {
        PrinterManager.getInstance().getPrinterGroup().get(groupPosition).setSwitchOC(status);
    }

    /**
     * 扫描打印机
     */
    public void scanPrinter() {
        PrinterManager.getInstance().scanPrinter();
    }

    /**
     * 注销
     */
    public void destroy() {

    }
}
