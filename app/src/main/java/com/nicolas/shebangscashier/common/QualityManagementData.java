package com.nicolas.shebangscashier.common;

import org.json.JSONException;
import org.json.JSONObject;

public class QualityManagementData {
    public String id = "";//2101051",
    public String barCodeId = "";//101050021728",
    public String color = "";//默认",
    public String size = "";//默认",
    public String supplierId = "";//,
    public String sId = "";//9072",
    public String supplierName = "";//,
    public String goodsClassLv2Id = "";//31",
    public String goodsClassLv2Name = "";//羽绒服",
    public String goodsClassId = "";//MLHB-ZC-YRF",
    public String goodsClassName = "";//中长款羽绒服",
    public String branchId = "";//,
    public String branchName = "";//,
    public String fId = "";//L23",
    public String qualityClassId = "";//,
    public String qualityClassName = "";//,
    public String stockId = "";//,
    public String gId = "";//,
    public String seasonName = "";//冬装",
    public String goodsType = "";//代卖",
    public String img = "";//https://file.scdawn.com/cloud/images/goods/20201227/20122756af8c83-36f7-4c74-b185-3a5cba92288f.jpg",
    public String orderType = "";//首单",
    public String goodsId = "";//01227025",
    public float inPrice = 0;//": 459,
    public float originalPrice = 0;//": 459,
    public float orderPrice = 0;//": 501,
    public float salePrice = 0;//": 599,
    public String certificate = "";//https://file.scdawn.com/cloud/branch/idcard/20210104/210104cbddd0d4-49b7-4a88-ab9a-6cbff97fda13-2.jpg",
    public String reason = "";//原因备注",
    public String createTime = "";//2021-01-05 17:04:23",
    public String valid = "";//启用"

    public QualityManagementData(String json) {
        try {
            JSONObject object = new JSONObject(json);
            if (object.has("id")) {
                this.id = object.getString("id");
            }
            if (object.has("fId")) {
                this.fId = object.getString("fId");
            }
            if (object.has("barCodeId")) {
                this.barCodeId = object.getString("barCodeId");
            }
            if (object.has("img")) {
                this.img = object.getString("img");
            }
            if (object.has("goodsId")) {
                this.goodsId = object.getString("goodsId");
            }
            if (object.has("goodsClassName")) {
                this.goodsClassName = object.getString("goodsClassName");
            }
            if (object.has("seasonName")) {
                this.seasonName = object.getString("seasonName");
            }
            if (object.has("qualityClassName")) {
                this.qualityClassName = object.getString("qualityClassName");
            }
            if (object.has("reason")) {
                this.reason = object.getString("reason");
            }
            if (object.has("createTime")) {
                this.createTime = object.getString("createTime");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
