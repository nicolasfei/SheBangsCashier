package com.nicolas.shebangscashier.common;

import org.json.JSONException;
import org.json.JSONObject;

public class NewGoodsSaleStatistics {
    public String id = "";       //"2005094B9A0E15B848",
    public String sId = "";       //"9001",
    public String supplierId = "";       //null,
    public String supplierName = "";       //null,
    public String goodsClassId = "";       //"13",
    public String goodsClassName = "";       //"T恤",
    public String seasonName = "";       //"",
    public String fId = "";       //"N04",
    public String branchId = "";       //null,
    public String branchName = "";       //null,
    public String purchaseType = "";        //"自选单",
    public String b_b_Stock_Id = "";        //null,
    public String b_g_GoodsId_Id = "";       //null,
    public String b_c_DearClass_Id = "";    //null,
    public String img = "";                 //"https://file.scdawn.com/cloud/goodsImg/9001/T恤/00430D4/200430154428E2088.jpg",
    public String goodsId = "";             //"00430D4",
    public float inPrice = 0;               //16.5,
    public float originalPrice = 0;         //16.5,
    public float orderPrice = 0;            //18,
    public int saleNum = 0;                 //1,
    public float salePrice = 0;             //26,
    public float saleTotalPrice = 0;        //26,
    public String saleTime = "";            //"2020-05-09 20:26:49",
    public String payType = "";             //"现金",
    public String memberId = "";            //null,
    public String memberName = "";          //"徐雅梅",
    public String memberTel = "";           //"173****335",
    public String employeesId = "";         //null,
    public String employeesName = "";       //null,
    public String isIntegral = "";          //"否",
    public String b_b_BarCode_Id = "";      //"2005046FCD4E",
    public String receiptId = "";           //"N04200509202649",
    public String code = "";                //"",
    public String remark = "";              //"",
    public String sort = "";                //0,
    public String valid = "";               //"启用",
    public String color = "";               //"默认",
    public String size = "";                //"均码",
    public String barCodeId = "";           //null
    public int amount = 0;                  //null     剩余库存

    public NewGoodsSaleStatistics(){

    }

    public NewGoodsSaleStatistics(String json) {
        try {
            JSONObject object = new JSONObject(json);
            if (object.has("img")){
                this.img = object.getString("img");
            }
            if (object.has("goodsId")){
                this.goodsId = object.getString("goodsId");
            }
            if (object.has("b_b_BarCode_Id")){
                this.b_b_BarCode_Id = object.getString("b_b_BarCode_Id");
            }

            if (object.has("sId")){
                this.sId = object.getString("sId");
            }
            if (object.has("goodsClassName")){
                this.goodsClassName = object.getString("goodsClassName");
            }
            if (object.has("saleNum")){
                this.saleNum = object.getInt("saleNum");
            }

            if (object.has("saleTotalPrice")){
                this.saleTotalPrice = (float) object.getDouble("saleTotalPrice");
            }
            if (object.has("seasonName")){
                this.seasonName = object.getString("seasonName");
            }
            if (object.has("amount")){
                this.amount = object.getInt("amount");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
