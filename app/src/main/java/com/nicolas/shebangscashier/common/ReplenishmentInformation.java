package com.nicolas.shebangscashier.common;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ReplenishmentInformation {
    public String id = "";      //2010091",
    public String fId = "";     //N40",
    public String storeRoomName = "";   //A座 N库房",
    public String sId = "";             //9011",
    public String goodsId = "";         //01009001",
    public String goodsClass = "";      //短款开衫",
    public String colorSize = "";       //黄色 M, 黄色 S, 蓝色 M, 蓝色 S",
    public String seasonName = "";      //夏装",
    public String customer = "";        //少女款",
    public String img = "";             //https://file.scdawn.com/cloud/images/goods/20201009/2010096af79ee9-bfa8-4827-b1d1-23a3ba18ec1f.jpg",
    public String purchaseType = "";    //通下单 A",
    public String collocation = "";     //123",
    public String remark = "";          //",
    public String goodsType = "";       //试卖",
    public String isStock = "";         //允许",
    public String accountNameCheck = "";//勇哥"
    public List<Property> properties;

    public ReplenishmentInformation(String json) {
        try {
            JSONObject object = new JSONObject(json);
            if (object.has("id")) {
                this.id = object.getString("id");
            }
            if (object.has("fId")) {
                this.fId = object.getString("fId");
            }
            if (object.has("storeRoomName")) {
                this.storeRoomName = object.getString("storeRoomName");
            }
            if (object.has("sId")) {
                this.sId = object.getString("sId");
            }
            if (object.has("goodsId")) {
                this.goodsId = object.getString("goodsId");
            }
            if (object.has("goodsClass")) {
                this.goodsClass = object.getString("goodsClass");
            }
            if (object.has("colorSize")) {
                this.colorSize = object.getString("colorSize");
            }
            if (object.has("customer")) {
                this.customer = object.getString("customer");
            }
            if (object.has("img")) {
                this.img = object.getString("img");
            }
            if (object.has("purchaseType")) {
                this.purchaseType = object.getString("purchaseType");
            }

            if (object.has("collocation")) {
                this.collocation = object.getString("collocation");
            }
            if (object.has("remark")) {
                this.remark = object.getString("remark");
            }
            if (object.has("goodsType")) {
                this.goodsType = object.getString("goodsType");
            }
            if (object.has("isStock")) {
                this.isStock = object.getString("isStock");
            }
            if (object.has("accountNameCheck")) {
                this.accountNameCheck = object.getString("accountNameCheck");
            }

            properties = new ArrayList<>();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setProperties(String json) {
        try {
            JSONArray array = new JSONArray(json);
            properties.clear();
            for (int i = 0; i < array.length(); i++) {
                properties.add(new Property(array.getString(i)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public class Property {
        public String id = "";          //2010091",
        public String branchId = "";    //190402N40",
        public String branchName = "";  //": null,
        public String gId = "";         //2010091",
        public String goodsOrderId = "";    //": null,
        public String color = "";       //黄色",
        public String size = "";        //M",
        public int val = 0;             //": null,
        public int orderVal = 0;        //": null,
        public String createTime = "";  //: null,
        public String isStock = "";     //允许",
        public String valid = "";       //启用",
        public String state = "";       //": null

        public Property(String json) {
            try {
                JSONObject object = new JSONObject(json);
                if (object.has("id")) {
                    this.id = object.getString("id");
                }
                if (object.has("branchId")) {
                    this.branchId = object.getString("branchId");
                }
                if (object.has("branchName")) {
                    this.branchName = object.getString("branchName");
                }
                if (object.has("gId")) {
                    this.gId = object.getString("gId");
                }
                if (object.has("goodsOrderId")) {
                    this.goodsOrderId = object.getString("goodsOrderId");
                }
                if (object.has("color")) {
                    this.color = object.getString("color");
                }
                if (object.has("size")) {
                    this.size = object.getString("size");
                }
                if (object.has("val")) {
                    this.val = object.optInt("val", 0);
                }
                if (object.has("orderVal")) {
                    this.orderVal = object.optInt("orderVal", 0);
                }
                if (object.has("createTime")) {
                    this.createTime = object.getString("createTime");
                }
                if (object.has("isStock")) {
                    this.isStock = object.getString("isStock");
                }
                if (object.has("valid")) {
                    this.valid = object.getString("valid");
                }
                if (object.has("state")) {
                    this.state = object.getString("state");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
