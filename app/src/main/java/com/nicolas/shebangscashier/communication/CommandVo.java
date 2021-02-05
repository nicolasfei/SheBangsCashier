package com.nicolas.shebangscashier.communication;

import android.text.TextUtils;

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
        if (TextUtils.isEmpty(MyKeeper.getInstance().getToken())) {
            this.token = "";
        } else {
            this.token = "Bearer " + MyKeeper.getInstance().getToken();
        }
    }
}
