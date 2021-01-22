package com.nicolas.shebangscashier.data.vip;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class Vip implements Parcelable {
    public String state = "";    //会员状态
    public String id = "";       //会员ID
    public String tel = "";      //会员电话
    public int integral = 0;     //会员积分
    public String name = "";     //会员名

    public Vip(String json) {
        try {
            JSONObject object = new JSONObject(json);
            this.state = object.getString("state");
            this.id = object.getString("id");
            this.tel = object.getString("tel");
            this.integral = object.getInt("integral");
            this.name = object.getString("name");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    protected Vip(Parcel in) {
        state = in.readString();
        id = in.readString();
        tel = in.readString();
        integral = in.readInt();
        name = in.readString();
    }

    public static final Creator<Vip> CREATOR = new Creator<Vip>() {
        @Override
        public Vip createFromParcel(Parcel in) {
            return new Vip(in);
        }

        @Override
        public Vip[] newArray(int size) {
            return new Vip[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(state);
        dest.writeString(id);
        dest.writeString(tel);
        dest.writeInt(integral);
        dest.writeString(name);
    }
}
