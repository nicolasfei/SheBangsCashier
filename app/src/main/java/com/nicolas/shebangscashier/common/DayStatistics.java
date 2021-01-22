package com.nicolas.shebangscashier.common;

import org.json.JSONException;
import org.json.JSONObject;

public class DayStatistics {
    public String goodsClassName = "";//套头针织",
    public int saleNum = 0;
    public float saleTotalPrice = 0;

    public DayStatistics() {

    }

    public DayStatistics(String json) {
        try {
            JSONObject object = new JSONObject(json);
            if (object.has("goodsClassName")) {
                this.goodsClassName = object.getString("goodsClassName");
            }
            if (object.has("saleNum")) {
                this.saleNum = object.getInt("saleNum");
            }
            if (object.has("saleTotalPrice")) {
                this.saleTotalPrice = (float) object.getDouble("saleTotalPrice");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
