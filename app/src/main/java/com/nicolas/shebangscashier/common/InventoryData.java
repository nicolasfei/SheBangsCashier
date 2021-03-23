package com.nicolas.shebangscashier.common;

import org.json.JSONException;
import org.json.JSONObject;

public class InventoryData {
    public String id = "";     //011060018887",
    public String b_g_GoodsOrder_Id = "";     //96231674461C",
    public String sId = "";     //9297",
    public String supplierId = "";     //null,
    public String supplierName = "";     //null,
    public String goodsClassId = "";     //170610-088761AC",
    public String goodsClassName = "";     //双面羊绒",
    public String seasonName = "";     //冬装",
    public String goodsType = "";     //普通",
    public String fId = "";     //L23",
    public String branchId = "";     //null,
    public String branchName = "";     //null,
    public String purchaseType = "";     //通下单",
    public String b_b_Stock_Id = "";     //null,
    public String b_g_GoodsId_Id = "";     //null,
    public String b_c_DearClass_Id = "";     //null,
    public String img = "";     //https://file.scdawn.com/cloud/goodsImg/9297/双面羊绒/01011AD/2010111403506E504.jpg",
    public String orderType = "";     //通下",
    public String isBack = "";     //允许",
    public String state = "";     //正常",
    public String goodsId = "";     //01011AD",
    public int inPrice = 0;//
    public int originalPrice = 0;//
    public int orderPrice = 0;//
    public float salePrice = 0;//
    public String getTime = "";     //2020-11-06 13:11",
    public String printTime = "";     //2020-11-06 13:11",
    public String backTime = "";     //",
    public String lockTime = "";     //",
    public String saleCode = "";     //null,
    public String backGoodsCode = "";     //null,
    public String transferGoodsCode = "";     //null,
    public String s_b_Account_Id = "";     //null,
    public String sendId = "";     //201105187",
    public String source = "";     //供货商",
    public String remark = "";     //",
    public String color = "";     //默认",
    public String size = "";     //均码",
    public int sort = 0;//100,
    public String valid = "";     //启用",
    public int count = 0;//
    public String pd = "";     //未盘点",
    public String pdTime = "";     //"

    public InventoryData(String json){
        try {
            JSONObject object = new JSONObject(json);
            if (object.has("id")){
                this.id = object.getString("id");
            }
            if (object.has("sId")){
                this.sId = object.getString("sId");
            }
            if (object.has("goodsClassName")){
                this.goodsClassName = object.getString("goodsClassName");
            }
            if (object.has("seasonName")){
                this.seasonName = object.getString("seasonName");
            }
            if (object.has("fId")){
                this.fId = object.getString("fId");
            }
            if (object.has("img")){
                this.img = object.getString("img");
            }
            if (object.has("state")){
                this.state = object.getString("state");
            }
            if (object.has("goodsId")){
                this.goodsId = object.getString("goodsId");
            }
            if (object.has("salePrice")){
                this.salePrice = (float) object.getDouble("salePrice");
            }
            if (object.has("getTime")){
                this.getTime = object.getString("getTime");
            }
            if (object.has("source")){
                this.source = object.getString("source");
            }
            if (object.has("pd")){
                this.pd = object.getString("pd");
            }
            if (object.has("pdTime")){
                this.pdTime = object.getString("pdTime");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
