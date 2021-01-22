package com.nicolas.shebangscashier.ui.cash.data;

import android.os.Parcel;
import android.os.Parcelable;

public class SettlementGoodsMsg implements Parcelable {
    public String type;
    public String num;
    public String price;

    public SettlementGoodsMsg(String type, String num, String price) {
        this.type = type;
        this.num = num;
        this.price = price;
    }

    protected SettlementGoodsMsg(Parcel in) {
        type = in.readString();
        num = in.readString();
        price = in.readString();
    }

    public static final Creator<SettlementGoodsMsg> CREATOR = new Creator<SettlementGoodsMsg>() {
        @Override
        public SettlementGoodsMsg createFromParcel(Parcel in) {
            return new SettlementGoodsMsg(in);
        }

        @Override
        public SettlementGoodsMsg[] newArray(int size) {
            return new SettlementGoodsMsg[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(type);
        dest.writeString(num);
        dest.writeString(price);
    }
}
