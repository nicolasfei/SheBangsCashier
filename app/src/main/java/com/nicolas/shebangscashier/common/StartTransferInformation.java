package com.nicolas.shebangscashier.common;

import org.json.JSONException;
import org.json.JSONObject;

public class StartTransferInformation {

    public String id = "";//20081608289D",
    public String goodsId = "";//200727001",
    public String goodsClassName = "";//连衣裙",
    public String fId = "";//K15",
    public String transferTime = "";//2020-08-17 11:39:52",
    public String state = "";//正常"

    public StartTransferInformation(String json){
        try {
            JSONObject object = new JSONObject(json);
            if (object.has("id")){
                this.id = object.getString("id");
            }
            if (object.has("goodsId")){
                this.goodsId = object.getString("goodsId");
            }
            if (object.has("goodsClassName")){
                this.goodsClassName = object.getString("goodsClassName");
            }
            if (object.has("fId")){
                this.fId = object.getString("fId");
            }
            if (object.has("transferTime")){
                this.transferTime = object.getString("transferTime");
            }
            if (object.has("state")){
                this.state = object.getString("state");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
