package com.nicolas.shebangscashier.common;

import org.json.JSONException;
import org.json.JSONObject;

public class ReturnGoodsInformation {

    public String id = "";                  //20081618E908",
    public String goodsClassName = "";      //连衣裙",
    public String goodsClassId = "";        //6",
    public int amount = 0;                  //1,
    public float price = 0;                 //79,
    public float totalPrice = 0;            //79,
    public int deductionIntegral = 0;       //扣除积分
    public int balanceIntegral = 0;         //会员积分
    public String isIntegral = "";          //否",
    public String memberTel = "";           //",
    public String memberId = "";            //"

    public String json;

    public ReturnGoodsInformation() {

    }

    public ReturnGoodsInformation(String json) {
        this.json = json;
        try {
            JSONObject object = new JSONObject(json);
            if (object.has("id")) {
                this.id = object.getString("id");
            }
            if (object.has("goodsClassName")) {
                this.goodsClassName = object.getString("goodsClassName");
            }
            if (object.has("goodsClassId")) {
                this.goodsClassId = object.getString("goodsClassId");
            }
            if (object.has("amount")) {
                this.amount = object.getInt("amount");
            }
            if (object.has("price")) {
                this.price = (float) object.getDouble("price");
            }
            if (object.has("totalPrice")) {
                this.totalPrice = (float) object.getDouble("totalPrice");
            }
            if (object.has("deductionIntegral")) {
                this.deductionIntegral = object.getInt("deductionIntegral");
            }
            if (object.has("balanceIntegral")) {
                this.balanceIntegral = object.getInt("balanceIntegral");
            }
            if (object.has("isIntegral")) {
                this.isIntegral = object.getString("isIntegral");
            }
            if (object.has("memberTel")) {
                this.memberTel = object.getString("memberTel");
            }
            if (object.has("memberId")) {
                this.memberId = object.getString("memberId");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getJsonString() {
        return this.json;
    }
}
