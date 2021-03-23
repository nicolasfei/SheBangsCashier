package com.nicolas.shebangscashier.common;

import org.json.JSONException;
import org.json.JSONObject;

public class GoodsTransferData {
    public String id = "";     //2008175",
    public String sId = "";     //A005",
    public String fIdFrom = "";     //N04",
    public String fIdTo = "";     //K15",
    public String supplierId = "";     //,
    public String supplierName = "";     //,
    public String goodsClassId = "";     //6",
    public String goodsClassName = "";     //连衣裙",
    public String purchaseType = "";     //通下单",
    public String b_b_Stock_Id = "";     //,
    public String b_g_GoodsId_Id = "";     //,
    public String b_c_DearClass_Id = "";     //,
    public String img = "";     //https://file.scdawn.com/cloud/images/goods/2020072",
    public String oldGoodsId = "";     //DS23",
    public String goodsId = "";     //200727001",
    public String branchIdFrom = "";     //9",
    public String branchNameFrom = "";     //,
    public String branchIdTo = "";     //0224",
    public String branchNameTo = "";     //,
    public int transferNumber = 0;     //1,
    public float transferPrice = 0;     //50,
    public float transferTotalPrice = 0;     //50,
    public String transferTime = "";     //2020-08-17 10:37",
    public String state = "";     //已确认",
    public String okTime = "";     //2020-08-17 10",
    public String b_b_BarCode_Id = "";     //20081608289D",
    public String code = "";     //2008175",
    public String remark = "";     //",
    public String valid = "";     //启用"

    public GoodsTransferData(String json) {
        try {
            JSONObject object = new JSONObject(json);
            if (object.has("id")) {
                this.id = object.getString("id");
            }
            if (object.has("sId")) {
                this.sId = object.getString("sId");
            }
            if (object.has("fIdFrom")) {
                this.fIdFrom = object.getString("fIdFrom");
            }
            if (object.has("fIdTo")) {
                this.fIdTo = object.getString("fIdTo");
            }
            if (object.has("goodsClassName")) {
                this.goodsClassName = object.getString("goodsClassName");
            }
            if (object.has("img")) {
                this.img = object.getString("img");
            }
            if (object.has("goodsId")) {
                this.goodsId = object.getString("goodsId");
            }
            if (object.has("transferTime")) {
                this.transferTime = object.getString("transferTime");
            }
            if (object.has("state")) {
                this.state = object.getString("state");
            }
            if (object.has("okTime")) {
                this.okTime = object.getString("okTime");
            }
            if (object.has("b_b_BarCode_Id")) {
                this.b_b_BarCode_Id = object.getString("b_b_BarCode_Id");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
