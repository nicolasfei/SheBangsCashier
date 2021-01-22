package com.nicolas.shebangscashier.ui.receive;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.nicolas.shebangscashier.BaseActivity;
import com.nicolas.shebangscashier.MyActivity;

public class ReceiveGoodsActivity extends MyActivity {

    private ReceiveGoodsViewModel viewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.viewModel = new ViewModelProvider(this).get(ReceiveGoodsViewModel.class);
    }
}
