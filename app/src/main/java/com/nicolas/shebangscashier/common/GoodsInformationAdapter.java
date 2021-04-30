package com.nicolas.shebangscashier.common;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.nicolas.shebangscashier.R;

import java.util.List;

public class GoodsInformationAdapter extends BaseAdapter {

    public List<GoodsInformation> goodsInformation;
    public Context mContext;

    public GoodsInformationAdapter(Context context, List<GoodsInformation> list) {
        this.mContext = context;
        this.goodsInformation = list;
    }

    @Override
    public int getCount() {
        return goodsInformation == null ? 0 : goodsInformation.size();
    }

    @Override
    public Object getItem(int position) {
        return goodsInformation == null ? null : goodsInformation.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(this.mContext).inflate(R.layout.receipt_list_item, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final GoodsInformation goods = goodsInformation.get(position);

        String codeValue = mContext.getString(R.string.code) + mContext.getString(R.string.colon_zh) + goods.b_b_BarCode_Id;
        holder.code.setText(codeValue);

        String typeValue = mContext.getString(R.string.goods_type) + mContext.getString(R.string.colon_zh) + goods.goodsClassName;
        holder.type.setText(typeValue);

        String numValue = mContext.getString(R.string.num) + mContext.getString(R.string.colon_zh) + goods.saleNum;
        holder.num.setText(numValue);

        String priceValue = mContext.getString(R.string.total_price) + mContext.getString(R.string.colon_zh) + goods.saleTotalPrice;
        holder.price.setText(priceValue);

        String dateValue = mContext.getString(R.string.sale_date) + mContext.getString(R.string.colon_zh) + goods.saleTime;
        holder.date.setText(dateValue);

        String idValue = mContext.getString(R.string.receipt_id) + mContext.getString(R.string.colon_zh) + goods.receiptId;
        holder.id.setText(idValue);

        return convertView;
    }

    private static class ViewHolder {
        private TextView code;
        private TextView type;
        private TextView num;
        private TextView price;
        private TextView date;
        private TextView id;

        private ViewHolder(View view) {
            this.code = view.findViewById(R.id.code);
            this.type = view.findViewById(R.id.type);
            this.num = view.findViewById(R.id.num);

            this.price = view.findViewById(R.id.price);
            this.date = view.findViewById(R.id.date);
            this.id = view.findViewById(R.id.id);
        }
    }
}
