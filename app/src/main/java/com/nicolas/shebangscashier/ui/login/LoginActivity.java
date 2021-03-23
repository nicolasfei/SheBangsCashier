package com.nicolas.shebangscashier.ui.login;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.nicolas.shebangscashier.BaseActivity;
import com.nicolas.shebangscashier.MainActivity;
import com.nicolas.shebangscashier.R;
import com.nicolas.shebangscashier.app.MyApp;
import com.nicolas.shebangscashier.cashier.MyKeeper;
import com.nicolas.shebangscashier.common.OperateResult;
import com.nicolas.toollibrary.BruceDialog;
import com.nicolas.toollibrary.LoginAutoMatch;
import com.nicolas.toollibrary.Utils;

public class LoginActivity extends BaseActivity {

    private LoginViewModel loginViewModel;
    private boolean loginIng;                           //是否在登陆中

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //全屏显示
//        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //加载布局
        setContentView(R.layout.activity_login);
        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        //初始化界面
        AutoCompleteTextView usernameEditText = findViewById(R.id.username);
        EditText passwordEditText = findViewById(R.id.password);
        Button loginButton = findViewById(R.id.login);

        //添加自动匹配登陆用户账号信息
        LoginAutoMatch.getInstance().init(this);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, LoginAutoMatch.getInstance().getLoginUserName());
        usernameEditText.setAdapter(adapter);
        usernameEditText.setThreshold(1);   //设置输入几个字符后开始出现提示 默认是2
        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                loginViewModel.loginDataChanged(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        };
        usernameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    //用户登陆
                    userLogin(usernameEditText.getText().toString(), passwordEditText.getText().toString());
                }
                return false;
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //用户登陆
                userLogin(usernameEditText.getText().toString(), passwordEditText.getText().toString());
            }
        });

        //监听登陆信息输入
        loginViewModel.getLoginFormState().observe(this, new Observer<OperateResult>() {
            @Override
            public void onChanged(@Nullable OperateResult loginFormState) {
                if (loginFormState == null) {
                    return;
                }
                if (loginFormState.getSuccess() != null) {
                    loginButton.setEnabled(true);
                }
                if (loginFormState.getError() != null) {
                    switch (loginFormState.getError().getErrorCode()) {
                        case -1:
                            usernameEditText.setError(loginFormState.getError().getErrorMsg());
                            break;
                        case -2:
                            passwordEditText.setError(loginFormState.getError().getErrorMsg());
                            break;
                        default:
                            break;
                    }
                }
            }
        });

        //监听登陆结果
        loginViewModel.getLoginResult().observe(this, new Observer<OperateResult>() {
            @Override
            public void onChanged(OperateResult result) {
                if (result.getError() != null) {
                    dismissProgressDialog();
                    Utils.toast(LoginActivity.this, result.getError().getErrorMsg());
                    loginIng = false;
                }
                if (result.getSuccess() != null) {
                    showProgressDialog(getString(R.string.getting_employess));
                    //获取导购信息
                    loginViewModel.queryEmployeesInformation();
                }
            }
        });

        //监听获取导购信息
        loginViewModel.getEmployeesResult().observe(this, new Observer<OperateResult>() {
            @Override
            public void onChanged(OperateResult result) {
                dismissProgressDialog();
                if (result.getError() != null) {
                    Utils.toast(LoginActivity.this, result.getError().getErrorMsg());
                    loginIng = false;
                }
                if (result.getSuccess() != null) {
                    //选择导购
                    choiceEmployees();
                }
            }
        });

        //获取app当前版本
        String appCurrentVersion = "";
        PackageManager manager = MyApp.getInstance().getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(MyApp.getInstance().getPackageName(), 0);
            appCurrentVersion = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        String value = "版本号：v" + appCurrentVersion + "\n" +
                "系统名称：DAWN BUSINESS IT SYSTEM \n" +
                "版权信息：Copyright  2009-2020 By Si Chuan Province Dawn Business CO.,Ltd.All rights Reserved.";
        TextView about = findViewById(R.id.about);
        about.setText(value);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            loginIng = false;
        }
    }

    /**
     * 选择导购
     */
    private void choiceEmployees() {
        String[] employees = MyKeeper.getInstance().getEmployeesName();
        if (employees == null || employees.length == 0) {
            MyKeeper.getInstance().setStaff("");
            updateUiWithUser("");
            return;
        }
        BruceDialog.showSingleChoiceDialog(R.string.employees, LoginActivity.this, MyKeeper.getInstance().getEmployeesName(), new BruceDialog.OnChoiceItemListener() {
            @Override
            public void onChoiceItem(String itemName) {
                if (TextUtils.isEmpty(itemName)) {
                    choiceEmployees();
                } else {
                    MyKeeper.getInstance().setStaff(itemName);
                    updateUiWithUser(itemName);
                }
            }
        });
    }

    /**
     * 欢迎登陆
     *
     * @param name 用户名
     */
    private void updateUiWithUser(String name) {
        String welcome = getString(R.string.welcome) + name;
        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
        //添加登陆用户
        LoginAutoMatch.getInstance().addLoginUser(MyKeeper.getInstance().getLoginName(), MyKeeper.getInstance().getLoginPassword());
        //跳转到主页面
        Intent intent = new Intent(this, MainActivity.class);
        startActivityForResult(intent, 1);
    }

    /**
     * 用户登陆
     *
     * @param userName 用户名
     * @param password 密码
     */
    private void userLogin(String userName, String password) {
        if (!loginIng) {
            loginIng = true;
            showProgressDialog(getString(R.string.login_ing));
            MyKeeper.getInstance().setLogin(userName, password);
            loginViewModel.login(userName, password);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
