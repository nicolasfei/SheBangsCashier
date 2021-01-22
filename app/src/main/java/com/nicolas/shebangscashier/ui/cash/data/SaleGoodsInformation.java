package com.nicolas.shebangscashier.ui.cash.data;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 售卖商品信息
 */
public class SaleGoodsInformation implements Parcelable {
    public String id;               //"200803DFF61E",
    public String goodsClassId;     //"6",
    public String goodsClassName;   //"连衣裙",
    public String goodsUnit;        //"件",
    public String goodsType;        //"普通",
    public int amount;              //1,
    public float price;             //79,
    public float totalPrice;        //79,
    public int stockAmount;         //25


    public SaleGoodsInformation(String json) {
        try {
            JSONObject object = new JSONObject(json);
            this.id = object.getString("id");
            this.goodsClassId = object.getString("goodsClassId");
            this.goodsClassName = object.getString("goodsClassName");
            this.goodsUnit = object.getString("goodsUnit");
            this.goodsType = object.getString("goodsType");
            this.amount = object.getInt("amount");
            this.price = (float) object.getDouble("price");
            this.totalPrice = (float) object.getDouble("totalPrice");
            this.stockAmount = object.getInt("stockAmount");
        } catch (JSONException e) {
            e.printStackTrace();
            this.id = "";
            this.goodsClassId = "";
            this.goodsClassName = "";
            this.goodsUnit = "";
            this.goodsType = "";
            this.amount = 0;
            this.price = 0;
            this.totalPrice = 0;
            this.stockAmount = 0;
        }
    }

    protected SaleGoodsInformation(Parcel in) {
        id = in.readString();
        goodsClassId = in.readString();
        goodsClassName = in.readString();
        goodsUnit = in.readString();
        goodsType = in.readString();
        amount = in.readInt();
        price = in.readFloat();
        totalPrice = in.readFloat();
        stockAmount = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(goodsClassId);
        dest.writeString(goodsClassName);
        dest.writeString(goodsUnit);
        dest.writeString(goodsType);
        dest.writeInt(amount);
        dest.writeFloat(price);
        dest.writeFloat(totalPrice);
        dest.writeInt(stockAmount);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<SaleGoodsInformation> CREATOR = new Creator<SaleGoodsInformation>() {
        @Override
        public SaleGoodsInformation createFromParcel(Parcel in) {
            return new SaleGoodsInformation(in);
        }

        @Override
        public SaleGoodsInformation[] newArray(int size) {
            return new SaleGoodsInformation[size];
        }
    };
}
