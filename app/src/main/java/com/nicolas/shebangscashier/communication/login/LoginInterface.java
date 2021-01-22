package com.nicolas.shebangscashier.communication.login;

import com.nicolas.shebangscashier.communication.AbstractInterface;

public abstract class LoginInterface extends AbstractInterface {
    //登陆接口
    public final static String Login = AbstractInterface.COMMAND_URL + "Cashier/Login";
    //登出接口
    public final static String Logout = AbstractInterface.COMMAND_URL + "Cashier/LoginOut";
    //导购查询接口
    public final static String QueryEmployees = AbstractInterface.COMMAND_URL + "Cashier/Employees";
}
