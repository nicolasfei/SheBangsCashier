package com.nicolas.shebangscashier.common;

import org.json.JSONException;
import org.json.JSONObject;

public class GoodsInventoryData {
    public String id = "";//2007271",
    public String goodsID = "";//005",
    public String branch = "";//6",
    public String area = "";//夏装",
    public String stock = "";//学生款",

    public GoodsInventoryData(String json) {
        try {
            JSONObject object = new JSONObject(json);
            if (object.has("id")) {
                this.id = object.getString("id");
            }
            if (object.has("goodsID")) {
                this.goodsID = object.getString("goodsID");
            }
            if (object.has("branch")) {
                this.branch = object.getString("branch");
            }
            if (object.has("area")) {
                this.area = object.getString("area");
            }
            if (object.has("stock")) {
                this.stock = object.getString("stock");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
