package com.nicolas.shebangscashier.communication;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class CommandResponse {
    private static final String TAG = "CommandResponse";
    public float saleTotalPrice;
    public int saleNum;
    public boolean success = false;
    public String msg = "";
    public int code = 0;
    public String data;
    public String token;
    public String jsonData;
    public int total;
    public String url;

    public int integral;
    public int integralMoney;

    /**
     * 请求返回，组建为响应类
     *
     * @param response   响应字符串
     * @param requestUrl 请求url
     */
    public CommandResponse(String response, String requestUrl) {
        if (response != null) {
            try {
                Log.i(TAG, "CommandResponse: " + response + " requestUrl is " + requestUrl);
                JSONObject rep = new JSONObject(response);
                this.success = rep.getBoolean("success");
                if (rep.has("msg")) {
                    this.msg = rep.getString("msg");
                }
                if (rep.has("data")) {
                    this.data = rep.getString("data").replace("null", "\"\"");
                }
                if (rep.has("token")) {
                    JSONObject token = new JSONObject();
                    token.put("token", rep.getString("token"));
                    this.token = token.toString();
                }
                if (rep.has("jsonData")) {
                    this.jsonData = rep.getString("jsonData");
                }
                if (rep.has("total")) {
                    this.total = rep.getInt("total");
                }
                if (rep.has("integralMoney")) {
                    this.integralMoney = rep.getInt("integralMoney");
                }
                if (rep.has("integral")) {
                    this.integral = rep.getInt("integral");
                }
                if (rep.has("saleNum")) {
                    this.saleNum = rep.getInt("integral");
                }
                if (rep.has("saleTotalPrice")) {
                    this.saleTotalPrice = (float) rep.getDouble("saleTotalPrice");
                }
            } catch (JSONException e) {
                e.printStackTrace();
                if (response.contains("error")) {
                    this.msg = response.substring("error".length());
                } else {     //这个是app版本号
                    this.success = true;
                    this.data = response;
                }
            }
        }
        this.url = requestUrl;
    }
}
