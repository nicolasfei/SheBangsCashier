package com.nicolas.shebangscashier.ui.cash.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class SettlementGroupMsg implements Parcelable {

    private SettlementGoodsMsg head;
    private SettlementGoodsMsg footer;
    private ArrayList<SettlementGoodsMsg> children;

    public SettlementGroupMsg(SettlementGoodsMsg head, SettlementGoodsMsg footer, ArrayList<SettlementGoodsMsg> children) {
        this.head = head;
        this.footer = footer;
        this.children = children;
    }

    protected SettlementGroupMsg(Parcel in) {
        head = in.readParcelable(SettlementGoodsMsg.class.getClassLoader());
        footer = in.readParcelable(SettlementGoodsMsg.class.getClassLoader());
        children = in.createTypedArrayList(SettlementGoodsMsg.CREATOR);
    }

    public static final Creator<SettlementGroupMsg> CREATOR = new Creator<SettlementGroupMsg>() {
        @Override
        public SettlementGroupMsg createFromParcel(Parcel in) {
            return new SettlementGroupMsg(in);
        }

        @Override
        public SettlementGroupMsg[] newArray(int size) {
            return new SettlementGroupMsg[size];
        }
    };

    public ArrayList<SettlementGoodsMsg> getChildren() {
        return children;
    }

    public SettlementGoodsMsg getFooter() {
        return footer;
    }

    public SettlementGoodsMsg getHead() {
        return head;
    }

    public void setChildren(ArrayList<SettlementGoodsMsg> children) {
        this.children = children;
    }

    public void setFooter(SettlementGoodsMsg footer) {
        this.footer = footer;
    }

    public void setHead(SettlementGoodsMsg head) {
        this.head = head;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(head, flags);
        dest.writeParcelable(footer, flags);
        dest.writeTypedList(children);
    }
}