package com.nicolas.shebangscashier.common;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nicolas.shebangscashier.R;
import com.nicolas.toollibrary.imageload.ImageLoadClass;

import java.util.List;

public class InventoryDataAdapter extends BaseAdapter {

    private Context mContext;
    private List<InventoryData> data;
    private boolean isBusy = false;                         //表示list view是否在快速滑动

    public InventoryDataAdapter(Context context, List<InventoryData> data) {
        this.mContext = context;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data == null ? 0 : data.size();
    }

    @Override
    public Object getItem(int position) {
        return data == null ? null : data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(this.mContext).inflate(R.layout.inventory_item, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        InventoryData s = data.get(position);
        //加载图片
        if (!isBusy) {
//            ImageLoadClass.getInstance().displayImage(s.img, holder.photo, false);
        } else {
            ImageLoadClass.getInstance().displayImage(s.img, holder.photo, true);
        }

        String shopValue = mContext.getString(R.string.shop) + mContext.getString(R.string.colon)  + s.fId ;
        holder.shop.setText(shopValue);

        String sIdValue = mContext.getString(R.string.sId1) + mContext.getString(R.string.colon)  + s.sId ;
        holder.sId.setText(sIdValue);

        String getTimeValue = mContext.getString(R.string.getTime) + mContext.getString(R.string.colon)  + s.getTime ;
        holder.getTime.setText(getTimeValue);

        String seasonNameValue = mContext.getString(R.string.seasonName) + mContext.getString(R.string.colon)  + s.seasonName ;
        holder.season.setText(seasonNameValue);

        String priceValue = mContext.getString(R.string.salePrice) + mContext.getString(R.string.colon)  + s.salePrice ;
        holder.price.setText(priceValue);

        String goodsIdValue = mContext.getString(R.string.goodsId) + mContext.getString(R.string.colon)  + s.goodsId ;
        holder.newGoods.setText(goodsIdValue);

        String isInventoryValue = mContext.getString(R.string.isInventory) + mContext.getString(R.string.colon)  + s.pd ;
        holder.isInventory.setText(isInventoryValue);

        String goodsClassValue = mContext.getString(R.string.goodsClass) + mContext.getString(R.string.colon)  + s.goodsClassName ;
        holder.type.setText(goodsClassValue);

        String pdTimeValue = mContext.getString(R.string.pdTime) + mContext.getString(R.string.colon)  + s.pdTime ;
        holder.pdTime.setText(pdTimeValue);

        String sourceValue = mContext.getString(R.string.source) + mContext.getString(R.string.colon)  + s.source ;
        holder.source.setText(sourceValue);

        String codeStatusValue = mContext.getString(R.string.codeStatus) + mContext.getString(R.string.colon)  + s.state ;
        holder.codeStatus.setText(codeStatusValue);

        return convertView;
    }

    private static class ViewHolder {
        private ImageView photo;
        private TextView shop, getTime, isInventory, pdTime;
        private TextView sId, season, newGoods, type, price, source, codeStatus;

        private ViewHolder(View root) {
            this.photo = root.findViewById(R.id.photo);

            this.shop = root.findViewById(R.id.shop);
            this.getTime = root.findViewById(R.id.getTime);
            this.isInventory = root.findViewById(R.id.isInventory);
            this.pdTime = root.findViewById(R.id.pdTime);
            this.sId = root.findViewById(R.id.sId);

            this.season = root.findViewById(R.id.season);
            this.newGoods = root.findViewById(R.id.newGoods);
            this.type = root.findViewById(R.id.type);
            this.price = root.findViewById(R.id.price);
            this.source = root.findViewById(R.id.source);
            this.codeStatus = root.findViewById(R.id.codeStatus);
        }
    }
}
