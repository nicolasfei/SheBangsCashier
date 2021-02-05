package com.nicolas.shebangscashier.ui.login;

import android.util.Patterns;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.nicolas.shebangscashier.R;
import com.nicolas.shebangscashier.app.MyApp;
import com.nicolas.shebangscashier.cashier.MyKeeper;
import com.nicolas.shebangscashier.common.OperateError;
import com.nicolas.shebangscashier.common.OperateInUserView;
import com.nicolas.shebangscashier.common.OperateResult;
import com.nicolas.shebangscashier.communication.CommandResponse;
import com.nicolas.shebangscashier.communication.CommandTypeEnum;
import com.nicolas.shebangscashier.communication.CommandVo;
import com.nicolas.shebangscashier.communication.Invoker;
import com.nicolas.shebangscashier.communication.login.LoginInterface;
import com.nicolas.toollibrary.HttpHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginViewModel extends ViewModel {
    private MutableLiveData<OperateResult> loginFormState;
    private MutableLiveData<OperateResult> loginResult;
    private MutableLiveData<OperateResult> warehouseStaffListResult;
    private MutableLiveData<OperateResult> employeesResult;


    public LoginViewModel() {
        loginFormState = new MutableLiveData<>();
        loginResult = new MutableLiveData<>();
        warehouseStaffListResult = new MutableLiveData<>();
        employeesResult = new MutableLiveData<>();
    }

    public LiveData<OperateResult> getLoginFormState() {
        return loginFormState;
    }

    public LiveData<OperateResult> getLoginResult() {
        return loginResult;
    }

    public LiveData<OperateResult> getWarehouseStaffListResult() {
        return warehouseStaffListResult;
    }

    public LiveData<OperateResult> getEmployeesResult() {
        return employeesResult;
    }

    /**
     * 登陆
     *
     * @param username 用户名
     * @param password 密码
     */
    public void login(String username, String password) {
        CommandVo vo = new CommandVo();
        vo.typeEnum = CommandTypeEnum.COMMAND_LOGIN;
        vo.url = LoginInterface.Login;
        vo.contentType = HttpHandler.ContentType_APP;
        vo.requestMode = HttpHandler.RequestMode_POST;
        Map<String, String> parameters = new HashMap<>();
        parameters.put("fId", username);
        parameters.put("password", password);
        vo.parameters = parameters;
        Invoker.getInstance().setOnEchoResultCallback(this.callback);
        Invoker.getInstance().exec(vo);
    }

    /**
     * 获取导购信息
     */
    public void queryEmployeesInformation() {
        CommandVo vo = new CommandVo();
        vo.typeEnum = CommandTypeEnum.COMMAND_LOGIN;
        vo.url = LoginInterface.QueryEmployees;
        vo.contentType = HttpHandler.ContentType_APP;
        vo.requestMode = HttpHandler.RequestMode_POST;
        Map<String, String> parameters = new HashMap<>();
        vo.parameters = parameters;
        Invoker.getInstance().setOnEchoResultCallback(this.callback);
        Invoker.getInstance().exec(vo);
    }

    /**
     * 响应
     */
    Invoker.OnExecResultCallback callback = new Invoker.OnExecResultCallback() {

        @Override
        public void execResult(CommandResponse result) {
            switch (result.url) {
                case LoginInterface.Login:              //登陆
                    if (!result.success) {
                        loginResult.setValue(new OperateResult(new OperateError(result.code, result.msg, null)));
                    } else {
                        try {
                            JSONObject token = new JSONObject(result.token);
                            MyKeeper.getInstance().setToken(token.getString("token"));        //设置token
                            MyKeeper.getInstance().setBranch(result.data);                           //设置分店信息
                            loginResult.setValue(new OperateResult(new OperateInUserView(null)));
                        } catch (JSONException e) {
                            e.printStackTrace();
                            loginResult.setValue(new OperateResult(new OperateError(-1, MyApp.getInstance().getString(R.string.errorData), null)));
                        }
                    }
                    break;
                case LoginInterface.QueryEmployees:     //获取导购信息
                    if (!result.success) {
                        employeesResult.setValue(new OperateResult(new OperateError(result.code, result.msg, null)));
                    } else {
                        try {
                            MyKeeper.getInstance().setEmployees(result.data);
                            employeesResult.setValue(new OperateResult(new OperateInUserView(null)));
                        } catch (JSONException e) {
                            e.printStackTrace();
                            employeesResult.setValue(new OperateResult(new OperateError(-1, MyApp.getInstance().getString(R.string.errorData), null)));
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    };

    public void loginDataChanged(String username, String password) {
        if (!isUserNameValid(username)) {
            loginFormState.setValue(new OperateResult(new OperateError(-1, MyApp.getInstance().getString(R.string.invalid_username), null)));
        } else if (!isPasswordValid(password)) {
            loginFormState.setValue(new OperateResult(new OperateError(-2, MyApp.getInstance().getString(R.string.invalid_password), null)));
        } else {
            loginFormState.setValue(new OperateResult(new OperateInUserView(null)));
        }
    }

    // A placeholder username validation check
    private boolean isUserNameValid(String username) {
        if (username == null) {
            return false;
        }
        if (username.contains("@")) {
            return Patterns.EMAIL_ADDRESS.matcher(username).matches();
        } else {
            return !username.trim().isEmpty();
        }
    }

    // A placeholder password validation check
    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 5;
    }
}
