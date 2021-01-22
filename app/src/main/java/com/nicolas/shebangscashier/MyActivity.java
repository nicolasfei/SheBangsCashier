package com.nicolas.shebangscashier;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.nicolas.shebangscashier.cashier.MyKeeper;


/**
 * 对全局静态类所保存的数据进行null检测
 */
public class MyActivity extends BaseActivity {
    private String TAG = "MyActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

//    public boolean checkMemoryRecovery() {
//        //判断静态变量MyKeeper是否被系统回收
//        return MyKeeper.getInstance().getStaff() == null;
//    }
}
