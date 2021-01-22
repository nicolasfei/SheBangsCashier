package com.nicolas.shebangscashier.communication;

import com.nicolas.shebangscashier.cashier.MyKeeper;

import java.util.Map;

public class CommandVo {
    public String url;
    public String token;
    public Map<String, String> parameters;
    public String requestMode;
    public String contentType;
    public CommandTypeEnum typeEnum;

    public CommandVo() {
        this.token = MyKeeper.getInstance().getToken();
    }
}
