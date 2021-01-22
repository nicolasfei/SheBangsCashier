package com.nicolas.shebangscashier.cashier;

import org.json.JSONException;
import org.json.JSONObject;

public class Branch {
    public String id = "";//"0220",
    public String fId = "";//"ZYQ17",
    public String newName = "";//null,
    public String name = "";//"郫县3店",
    public String password = "";//null,
    public String cashierPWD = "";//null,
    public String supperPWD = "";//null,
    public String xsName = "";//"怦然心动",
    public String branchClass = "";//null,
    public String dearClassId = "";//null,
    public String dearClassName = "";//null,
    public String dzName = "";//null,
    public String dzTel = "";//null,
    public String province = "";//null,
    public String city = "";//null,
    public String district = "";//null,
    public String province_ = "";//null,
    public String city_ = "";//null,
    public String district_ = "";//null,
    public String address = "";//"郫县王府商业街54号",
    public String town = "";//null,
    public String storeRoomId = "";//null,
    public String storeRoomName = "";//null,
    public String areaId = "";//null,
    public String areaName = "";//null,
    public String areaTeId = "";//null,
    public String areaTeName = "";//null,
    public String complaintTel = "";//"18982201907",
    public String branchType = "";//"友情",
    public String purchaseTypeJson = "";//null,
    public String refuseId = "";//null,
    public String refuse = "";//null,
    public int priceLine = 0;//0,
    public String autoOrder = "";//null,
    public int multiple = 1;//1,
    public int balance = 0;//0,
    public int freezeMoney = 0;//0,
    public int availableMoney = 0;//0,
    public String weChat = "";//"",
    public String weMall = "";//"",
    public String state = "";//null,
    public String remark = "";//null,
    public int sort = 0;//0,
    public String valid = "";//null,
    public String lng = "";//null,
    public String lat = "";//null,
    public String createTime = "";//null

    public Branch(String json) {
        try {
            JSONObject object = new JSONObject(json);
            if (object.has("id")) {
                this.id = object.getString("id");
            }
            if (object.has("fId")) {
                this.fId = object.getString("fId");
            }
            if (object.has("newName")) {
                this.newName = object.getString("newName");
            }
            if (object.has("name")) {
                this.name = object.getString("name");
            }
            if (object.has("password")) {
                this.password = object.getString("password");
            }
            if (object.has("cashierPWD")) {
                this.cashierPWD = object.getString("cashierPWD");
            }
            if (object.has("supperPWD")) {
                this.supperPWD = object.getString("supperPWD");
            }

            if (object.has("xsName")) {
                this.xsName = object.getString("xsName");
            }
            if (object.has("branchClass")) {
                this.branchClass = object.getString("branchClass");
            }
            if (object.has("dearClassId")) {
                this.dearClassId = object.getString("dearClassId");
            }
            if (object.has("dearClassName")) {
                this.dearClassName = object.getString("dearClassName");
            }
            if (object.has("dzName")) {
                this.dzName = object.getString("dzName");
            }
            if (object.has("dzTel")) {
                this.dzTel = object.getString("dzTel");
            }
            if (object.has("province")) {
                this.province = object.getString("province");
            }
            if (object.has("city")) {
                this.city = object.getString("city");
            }
            if (object.has("district")) {
                this.district = object.getString("district");
            }
            if (object.has("province_")) {
                this.province_ = object.getString("province_");
            }

            if (object.has("city_")) {
                this.city_ = object.getString("city_");
            }
            if (object.has("district_")) {
                this.district_ = object.getString("district_");
            }
            if (object.has("address")) {
                this.address = object.getString("address");
            }
            if (object.has("town")) {
                this.town = object.getString("town");
            }
            if (object.has("storeRoomId")) {
                this.storeRoomId = object.getString("storeRoomId");
            }
            if (object.has("storeRoomName")) {
                this.storeRoomName = object.getString("storeRoomName");
            }
            if (object.has("areaId")) {
                this.areaId = object.getString("areaId");
            }
            if (object.has("areaName")) {
                this.areaName = object.getString("areaName");
            }
            if (object.has("areaTeId")) {
                this.areaTeId = object.getString("areaTeId");
            }
            if (object.has("areaTeName")) {
                this.areaTeName = object.getString("areaTeName");
            }

            if (object.has("complaintTel")) {
                this.complaintTel = object.getString("complaintTel");
            }
            if (object.has("branchType")) {
                this.branchType = object.getString("branchType");
            }
            if (object.has("purchaseTypeJson")) {
                this.purchaseTypeJson = object.getString("purchaseTypeJson");
            }
            if (object.has("refuseId")) {
                this.refuseId = object.getString("refuseId");
            }
            if (object.has("refuse")) {
                this.refuse = object.getString("refuse");
            }
            if (object.has("priceLine")) {
                this.priceLine = object.getInt("priceLine");
            }
            if (object.has("autoOrder")) {
                this.autoOrder = object.getString("autoOrder");
            }
            if (object.has("multiple")) {
                this.multiple = object.getInt("multiple");
            }
            if (object.has("balance")) {
                this.balance = object.getInt("balance");
            }
            if (object.has("freezeMoney")) {
                this.freezeMoney = object.getInt("freezeMoney");
            }

            if (object.has("availableMoney")) {
                this.availableMoney = object.getInt("availableMoney");
            }
            if (object.has("weChat")) {
                this.weChat = object.getString("weChat");
            }
            if (object.has("weMall")) {
                this.weMall = object.getString("weMall");
            }
            if (object.has("state")) {
                this.state = object.getString("state");
            }
            if (object.has("remark")) {
                this.remark = object.getString("remark");
            }
            if (object.has("sort")) {
                this.sort = object.getInt("sort");
            }
            if (object.has("valid")) {
                this.valid = object.getString("valid");
            }
            if (object.has("lng")) {
                this.lng = object.getString("lng");
            }
            if (object.has("lat")) {
                this.lat = object.getString("lat");
            }
            if (object.has("createTime")) {
                this.createTime = object.getString("createTime");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
