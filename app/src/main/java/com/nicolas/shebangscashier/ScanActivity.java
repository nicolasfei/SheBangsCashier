package com.nicolas.shebangscashier;

import android.os.Bundle;
import android.view.KeyEvent;

import com.journeyapps.barcodescanner.CaptureActivity;
import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.journeyapps.barcodescanner.camera.CameraManager;

public class ScanActivity extends CaptureActivity {

    DecoratedBarcodeView dbvCustom;

    private CaptureManager captureManager;
    private CameraManager cameraManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        dbvCustom = findViewById(R.id.dbv_custom);
        captureManager = new CaptureManager(this, dbvCustom);
        captureManager.initializeFromIntent(getIntent(), savedInstanceState);
        captureManager.decode();
        cameraManager = new CameraManager(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        dbvCustom.setTorchOff(); // 关闭手电筒
        captureManager.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        captureManager.onResume();
        dbvCustom.setTorchOn(); // 打开手电筒
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return dbvCustom.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        captureManager.onDestroy();
        super.onDestroy();
    }
}
