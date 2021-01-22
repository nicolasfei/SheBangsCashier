package com.nicolas.shebangscashier.common;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 商品信息
 */
public class GoodsInformation implements Parcelable {
    public String id = "";               //"200919625252297DB1",
    public String sId = "";              //"9064",
    public String supplierId = "";       //null,
    public String supplierName = "";     //null,
    public String goodsClassId = "";     //"170817F859A758E5",
    public String goodsClassName = "";   //"长裤",
    public String seasonName = "";       //"",
    public String fId = "";              //"N04",
    public String branchId = "";         //null,
    public String branchName = "";       //null,
    public String purchaseType = "";     //"通下单",
    public String b_b_Stock_Id = "";     //null,
    public String b_g_GoodsId_Id = "";   //null,
    public String b_c_DearClass_Id = ""; //null,
    public String img = "";              //"https://file.scdawn.com/cloud/goodsImg/9064/长裤/00902AC/2009021528503F94A.jpg",
    public String goodsId = "";          //"00902AC",
    public float inPrice = 0;           //51,
    public float originalPrice = 0;     //51,
    public float orderPrice = 0;        //56,
    public int saleNum = 0;             //1,
    public float salePrice = 0;         //79,
    public float saleTotalPrice = 0;    //79,
    public String saleTime = "";          //"2020-09-19 21:07:43",
    public String payType = "";           //"现金",
    public String memberId = "";          //null,
    public String memberName = "";        //"18381176821",
    public String memberTel = "";         //"183****821",
    public String employeesId = "";       //null,
    public String employeesName = "";     //null,
    public String isIntegral = "";        //"否",
    public String b_b_BarCode_Id = "";    //"2009141BA6CA",
    public String receiptId = "";         //"N04200919210742",
    public String code = "";              //"",
    public String remark = "";            //"",
    public String sort = "";              //0,
    public String valid = "";             //"启用",
    public String color = "";             //"默认",
    public String size = "";              //"均码",
    public String barCodeId = "";         //null

    public GoodsInformation(String json) {
        try {
            JSONObject object = new JSONObject(json);
            if (object.has("id")) {
                this.id = object.getString("id");
            }
            if (object.has("sId")) {
                this.sId = object.getString("sId");
            }
            if (object.has("supplierId")) {
                this.supplierId = object.getString("supplierId");
            }
            if (object.has("supplierName")) {
                this.supplierName = object.getString("supplierName");
            }
            if (object.has("goodsClassId")) {
                this.goodsClassId = object.getString("goodsClassId");
            }

            if (object.has("goodsClassName")) {
                this.goodsClassName = object.getString("goodsClassName");
            }
            if (object.has("seasonName")) {
                this.seasonName = object.getString("seasonName");
            }
            if (object.has("fId")) {
                this.fId = object.getString("fId");
            }
            if (object.has("branchId")) {
                this.branchId = object.getString("branchId");
            }
            if (object.has("branchName")) {
                this.branchName = object.getString("branchName");
            }

            if (object.has("purchaseType")) {
                this.purchaseType = object.getString("purchaseType");
            }
            if (object.has("img")) {
                this.img = object.getString("img");
            }
            if (object.has("goodsId")) {
                this.goodsId = object.getString("goodsId");
            }
            if (object.has("inPrice")) {
                this.inPrice = (float) object.getDouble("inPrice");
            }
            if (object.has("originalPrice")) {
                this.originalPrice = (float) object.getDouble("originalPrice");
            }

            if (object.has("orderPrice")) {
                this.orderPrice = (float) object.getDouble("orderPrice");
            }
            if (object.has("saleNum")) {
                this.saleNum = object.getInt("saleNum");
            }
            if (object.has("salePrice")) {
                this.salePrice = (float) object.getDouble("salePrice");
            }
            if (object.has("saleTotalPrice")) {
                this.saleTotalPrice = (float) object.getDouble("saleTotalPrice");
            }
            if (object.has("saleTime")) {
                this.saleTime = object.getString("saleTime");
            }

            if (object.has("payType")) {
                this.payType = object.getString("payType");
            }
            if (object.has("memberId")) {
                this.memberId = object.getString("memberId");
            }
            if (object.has("memberName")) {
                this.memberName = object.getString("memberName");
            }
            if (object.has("memberTel")) {
                this.memberTel = object.getString("memberTel");
            }
            if (object.has("isIntegral")) {
                this.isIntegral = object.getString("isIntegral");
            }

            if (object.has("b_b_BarCode_Id")) {
                this.b_b_BarCode_Id = object.getString("b_b_BarCode_Id");
            }
            if (object.has("receiptId")) {
                this.receiptId = object.getString("receiptId");
            }
            if (object.has("code")) {
                this.code = object.getString("code");
            }
            if (object.has("remark")) {
                this.remark = object.getString("remark");
            }
            if (object.has("isIntegral")) {
                this.isIntegral = object.getString("isIntegral");
            }

            if (object.has("sort")) {
                this.sort = object.getString("sort");
            }
            if (object.has("valid")) {
                this.valid = object.getString("valid");
            }
            if (object.has("color")) {
                this.color = object.getString("color");
            }
            if (object.has("size")) {
                this.size = object.getString("size");
            }
            if (object.has("barCodeId")) {
                this.barCodeId = object.getString("barCodeId");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    protected GoodsInformation(Parcel in) {
        id = in.readString();
        sId = in.readString();
        supplierId = in.readString();
        supplierName = in.readString();
        goodsClassId = in.readString();
        goodsClassName = in.readString();
        seasonName = in.readString();
        fId = in.readString();
        branchId = in.readString();
        branchName = in.readString();
        purchaseType = in.readString();
        b_b_Stock_Id = in.readString();
        b_g_GoodsId_Id = in.readString();
        b_c_DearClass_Id = in.readString();
        img = in.readString();
        goodsId = in.readString();
        inPrice = in.readFloat();
        originalPrice = in.readFloat();
        orderPrice = in.readFloat();
        saleNum = in.readInt();
        salePrice = in.readFloat();
        saleTotalPrice = in.readFloat();
        saleTime = in.readString();
        payType = in.readString();
        memberId = in.readString();
        memberName = in.readString();
        memberTel = in.readString();
        employeesId = in.readString();
        employeesName = in.readString();
        isIntegral = in.readString();
        b_b_BarCode_Id = in.readString();
        receiptId = in.readString();
        code = in.readString();
        remark = in.readString();
        sort = in.readString();
        valid = in.readString();
        color = in.readString();
        size = in.readString();
        barCodeId = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(sId);
        dest.writeString(supplierId);
        dest.writeString(supplierName);
        dest.writeString(goodsClassId);
        dest.writeString(goodsClassName);
        dest.writeString(seasonName);
        dest.writeString(fId);
        dest.writeString(branchId);
        dest.writeString(branchName);
        dest.writeString(purchaseType);
        dest.writeString(b_b_Stock_Id);
        dest.writeString(b_g_GoodsId_Id);
        dest.writeString(b_c_DearClass_Id);
        dest.writeString(img);
        dest.writeString(goodsId);
        dest.writeFloat(inPrice);
        dest.writeFloat(originalPrice);
        dest.writeFloat(orderPrice);
        dest.writeInt(saleNum);
        dest.writeFloat(salePrice);
        dest.writeFloat(saleTotalPrice);
        dest.writeString(saleTime);
        dest.writeString(payType);
        dest.writeString(memberId);
        dest.writeString(memberName);
        dest.writeString(memberTel);
        dest.writeString(employeesId);
        dest.writeString(employeesName);
        dest.writeString(isIntegral);
        dest.writeString(b_b_BarCode_Id);
        dest.writeString(receiptId);
        dest.writeString(code);
        dest.writeString(remark);
        dest.writeString(sort);
        dest.writeString(valid);
        dest.writeString(color);
        dest.writeString(size);
        dest.writeString(barCodeId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<GoodsInformation> CREATOR = new Creator<GoodsInformation>() {
        @Override
        public GoodsInformation createFromParcel(Parcel in) {
            return new GoodsInformation(in);
        }

        @Override
        public GoodsInformation[] newArray(int size) {
            return new GoodsInformation[size];
        }
    };
}
