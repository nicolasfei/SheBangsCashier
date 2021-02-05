package com.nicolas.shebangscashier.common;

import android.graphics.Bitmap;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class NewRemittance {
    private String id = "";                 //汇款登记ID，修改时存在，新增是为空
    private String fid = "";                 //fid
    private String b_b_Branch_Id = "";
    private String branchId = "";
    private String branchName = "";
    private String cashierTime = "";        //收银日期
    private String turnover = "";           //营业额
    private String rentCost = "";           //抵扣积分
    private String actualCollection = "";   //实收款

    private String expressCost = "";            //物流快递
    private String electricalWaterCost = "";    //水电气费
    private String clothesCost = "";            //衣服修补
    private String discountCost = "";           //折扣开支
    private String otherCost = "";              //其他开支
    private String wageCost = "";               //工资支出
    private String remark = "";                 //备注
    private String createTime = "";
    private String valid = "";              //是否启用
    private String certificate = "";        //凭证


    private List<Bitmap> vouchers;          //支付凭证List

    public NewRemittance(String json){
        try {
            JSONObject object = new JSONObject(json);
            if (object.has("id")){
                this.id = object.getString("id");
            }
            if (object.has("fid")){
                this.fid = object.getString("fid");
            }
            if (object.has("b_b_Branch_Id")){
                this.b_b_Branch_Id = object.getString("b_b_Branch_Id");
            }
            if (object.has("branchId")){
                this.branchId = object.getString("branchId");
            }
            if (object.has("branchName")){
                this.branchName = object.getString("branchName");
            }
            if (object.has("cashierTime")){
                this.cashierTime = object.getString("cashierTime");
            }
            if (object.has("turnover")){
                this.turnover = object.getString("turnover");
            }
            if (object.has("rentCost")){
                this.rentCost = object.getString("rentCost");
            }
            if (object.has("actualCollection")){
                this.actualCollection = object.getString("actualCollection");
            }
            if (object.has("expressCost")){
                this.expressCost = object.getString("expressCost");
            }

            if (object.has("electricalWaterCost")){
                this.electricalWaterCost = object.getString("electricalWaterCost");
            }
            if (object.has("clothesCost")){
                this.clothesCost = object.getString("clothesCost");
            }
            if (object.has("discountCost")){
                this.discountCost = object.getString("discountCost");
            }
            if (object.has("otherCost")){
                this.otherCost = object.getString("otherCost");
            }
            if (object.has("wageCost")){
                this.wageCost = object.getString("wageCost");
            }
            if (object.has("remark")){
                this.remark = object.getString("remark");
            }
            if (object.has("createTime")){
                this.createTime = object.getString("createTime");
            }
            if (object.has("valid")){
                this.valid = object.getString("valid");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        this.vouchers = new ArrayList<>();
    }

    private boolean isUpdate = false;

    public NewRemittance() {
        this.vouchers = new ArrayList<>();
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getCertificate() {
        return certificate;
    }

    public void setCertificate(String certificate) {
        this.certificate = certificate;
    }

    public String getCashierTime() {
        return cashierTime;
    }

    public List<Bitmap> getVouchers() {
        return vouchers;
    }

    public void setCashierTime(String cashierTime) {
        if (!this.cashierTime.equals(cashierTime)) {
            this.cashierTime = cashierTime;
            this.isUpdate = true;
        }
    }

    public void setRemark(String remarks) {
        if (!this.remark.equals(remarks)) {
            this.remark = remarks;
            this.isUpdate = true;
        }
    }

    public String getRemark() {
        return remark;
    }

    public void setWageCost(String wages) {
        if (!this.wageCost.equals(wages)) {
            this.wageCost = wages;
            this.isUpdate = true;
        }
    }

    public String getWageCost() {
        return wageCost;
    }

    public void setOtherCost(String other) {
        if (!this.otherCost.equals(other)) {
            this.otherCost = other;
            this.isUpdate = true;
        }
    }

    public String getOtherCost() {
        return otherCost;
    }

    public void setDiscountCost(String discount) {
        if (!this.discountCost.equals(discount)) {
            this.discountCost = discount;
            this.isUpdate = true;
        }
    }

    public String getDiscountCost() {
        return discountCost;
    }

    public void setClothesCost(String repair) {
        if (!this.clothesCost.equals(repair)) {
            this.clothesCost = repair;
            this.isUpdate = true;
        }
    }

    public String getClothesCost() {
        return clothesCost;
    }

    public void setElectricalWaterCost(String waterAndElectricityCost) {
        if (!this.electricalWaterCost.equals(waterAndElectricityCost)) {
            this.electricalWaterCost = waterAndElectricityCost;
            this.isUpdate = true;
        }
    }

    public String getElectricalWaterCost() {
        return electricalWaterCost;
    }

    public void setExpressCost(String logistics) {
        if (!this.expressCost.equals(logistics)) {
            this.expressCost = logistics;
            this.isUpdate = true;
        }
    }

    public String getExpressCost() {
        return expressCost;
    }

    public void setTurnover(String turnover) {
        if (!this.turnover.equals(turnover)) {
            this.turnover = turnover;
            this.isUpdate = true;
        }
    }

    public String getTurnover() {
        return turnover;
    }

    public void setRentCost(String integralDeduction) {
        if (!this.rentCost.equals(integralDeduction)) {
            this.rentCost = integralDeduction;
            this.isUpdate = true;
        }
    }

    public String getRentCost() {
        return rentCost;
    }

    public void setActualCollection(String actualCollection) {
        if (!this.actualCollection.equals(actualCollection)) {
            this.actualCollection = actualCollection;
            this.isUpdate = true;
        }
    }

    public String getActualCollection() {
        return actualCollection;
    }

    public boolean isUpdate() {
        return isUpdate;
    }

    public void resetIsUpdate() {
        isUpdate = false;
    }
}
