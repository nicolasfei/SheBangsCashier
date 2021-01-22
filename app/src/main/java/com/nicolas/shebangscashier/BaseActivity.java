package com.nicolas.shebangscashier;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.nicolas.toollibrary.AppActivityManager;
import com.nicolas.toollibrary.shakeproof.EventListener;

/**
 * BaseActivity
 * 1）：初始化基本数据，界面风格等
 */
public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppActivityManager.getInstance().addActivity(this);
        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setDisplayHomeAsUpEnabled(true);
            bar.setHomeButtonEnabled(true);
            bar.setDisplayShowHomeEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return true;
    }

    private ProgressDialog dialog;

    /**
     * Progress 对话框
     *
     * @param progressString progressString
     */
    public void showProgressDialog(String progressString) {
        if (dialog == null) {
            dialog = new ProgressDialog(this);
            dialog.setCancelable(false);
            dialog.create();
        }
        dialog.setMessage(progressString);
        dialog.show();
    }

    /**
     * 取消对话框
     */
    public void dismissProgressDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    /**
     * Find出来的View，自带防抖功能
     */
    public <T extends View> T findClickView(int id) {

        T view = (T) findViewById(id);
        view.setOnClickListener(new EventListener(this));
        return view;
    }

    @Override
    protected void onDestroy() {
        AppActivityManager.getInstance().removeActivity(this);
        super.onDestroy();
    }
}
