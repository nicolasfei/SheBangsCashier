package com.nicolas.shebangscashier;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.content.DialogInterface;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.nicolas.printerlibraryforufovo.PrinterManager;
import com.nicolas.shebangscashier.app.MyApp;
import com.nicolas.shebangscashier.app.LoginManager;
import com.nicolas.shebangscashier.cashier.MyKeeper;
import com.nicolas.shebangscashier.communication.CommandResponse;
import com.nicolas.shebangscashier.communication.CommandTypeEnum;
import com.nicolas.shebangscashier.communication.CommandVo;
import com.nicolas.shebangscashier.communication.Invoker;
import com.nicolas.shebangscashier.communication.login.LoginInterface;
import com.nicolas.toollibrary.AppActivityManager;
import com.nicolas.toollibrary.HttpHandler;
import com.nicolas.toollibrary.Utils;
import com.nicolas.toollibrary.VibratorUtil;
import com.nicolas.toollibrary.imageload.ImageLoadClass;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //全屏显示
//        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        AppActivityManager.getInstance().addActivity(this);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_sale,R.id.navigation_manage, R.id.navigation_set)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        //由于长时间被至于后台，系统回收了activity
        if (null == MyKeeper.getInstance().getBranch()) {
            LoginManager.getInstance().loginExpire(getString(R.string.loginTimeOut));
            finish();
            return;
        }

        //---------------------------------初始化全局类--------------------------------------//
        //开启打印机连接任务
        PrinterManager.getInstance().resetLinkDeviceModel(MyKeeper.getInstance().getBranch().fId);
        PrinterManager.getInstance().init(MyApp.getInstance());
        //开启SupplierKeeper定时查询任务
        MyKeeper.getInstance().startTimerTask();
        //开启语音，震动提示服务
        VibratorUtil.getInstance().init(MyApp.getInstance());
        //初始化url图片缓存
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2;
        ImageLoadClass.getInstance().init(BitmapFactory.decodeResource(getResources(), R.mipmap.ico_big_decolor, options));
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this).setTitle(R.string.logout)
                .setMessage(R.string.user_logout)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        userLogout();
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .create().show();
    }

    /**
     * 用户登出
     */
    private void userLogout() {
        CommandVo vo = new CommandVo();
        vo.typeEnum = CommandTypeEnum.COMMAND_LOGIN;
        vo.url = LoginInterface.Logout;
        vo.contentType = HttpHandler.ContentType_APP;
        vo.requestMode = HttpHandler.RequestMode_POST;
        Map<String, String> parameters = new HashMap<>();
        vo.parameters = parameters;
        Invoker.getInstance().setOnEchoResultCallback(this.callback);
        Invoker.getInstance().exec(vo);
    }

    Invoker.OnExecResultCallback callback = new Invoker.OnExecResultCallback() {

        @Override
        public void execResult(CommandResponse result) {
            switch (result.url) {
                case LoginInterface.Logout:        //登出
                    if (!result.success) {
                        Utils.toast(MainActivity.this, MainActivity.this.getString(R.string.logout_failed) + "," + result.msg);
                    } else {
                        Utils.toast(MainActivity.this, MainActivity.this.getString(R.string.logout_success));
                    }
                    MainActivity.this.finish();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onDestroy() {
        //打印机模块注销
        PrinterManager.getInstance().unManager();
        //关闭定时查询任务
        MyKeeper.getInstance().cancelTimerTask();
        //清除图片缓存
        ImageLoadClass.getInstance().release();
        //关闭语音，震动提示服务
        VibratorUtil.getInstance().shutdown();
        AppActivityManager.getInstance().removeActivity(this);
        super.onDestroy();
    }
}
