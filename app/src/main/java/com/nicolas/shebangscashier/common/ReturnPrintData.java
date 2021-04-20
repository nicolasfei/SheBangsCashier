package com.nicolas.shebangscashier.common;

import org.json.JSONException;
import org.json.JSONObject;

public class ReturnPrintData {
    public String id = "";//2010120389DE",
    public String fId = "";//L01",
    public float salePrice = 0;//": 69,
    public String sId = "";//9002",
    public String goodsId = "";//正常",
    public String goodsClassName = "";//长裤",
    public String state = "";//正常",
    public String area = "";//2"

    public ReturnPrintData(String json){
        try {
            JSONObject object = new JSONObject(json);
            if (object.has("id")){
                this.id = object.getString("id");
            }
            if (object.has("fId")){
                this.fId = object.getString("fId");
            }
            if (object.has("salePrice")){
                this.salePrice = (float) object.getDouble("salePrice");
            }
            if (object.has("sId")){
                this.sId = object.getString("sId");
            }
            if (object.has("goodsId")){
                this.goodsId = object.getString("goodsId");
            }
            if (object.has("goodsClassName")){
                this.goodsClassName = object.getString("goodsClassName");
            }
            if (object.has("state")){
                this.state = object.getString("state");
            }
            if (object.has("area")){
                this.area = object.getString("area");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
