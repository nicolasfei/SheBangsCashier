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

public class ReturnGoodsAdapter extends BaseAdapter {

    public Context mContext;
    public List<ReturnGoodsInformation> informations;

    public ReturnGoodsAdapter(Context context, List<ReturnGoodsInformation> informations) {
        this.mContext = context;
        this.informations = informations;
    }

    @Override
    public int getCount() {
        return informations == null ? 0 : informations.size();
    }

    @Override
    public Object getItem(int position) {
        return informations == null ? null : informations.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(this.mContext).inflate(R.layout.return_goods_list_item, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final ReturnGoodsInformation goods = informations.get(position);

        String codeValue = mContext.getString(R.string.code) + mContext.getString(R.string.colon_zh) + goods.id ;
        holder.code.setText(codeValue);

        String typeValue = mContext.getString(R.string.goods_type) + mContext.getString(R.string.colon_zh) + goods.goodsClassName ;
        holder.type.setText(typeValue);

        String numValue = mContext.getString(R.string.num) + mContext.getString(R.string.colon_zh) + goods.amount ;
        holder.num.setText(numValue);

        String priceValue = mContext.getString(R.string.total_price) + mContext.getString(R.string.colon_zh) + goods.totalPrice ;
        holder.totalPrice.setText(priceValue);

        String dateValue = mContext.getString(R.string.price) + mContext.getString(R.string.colon_zh) + goods.price ;
        holder.price.setText(dateValue);

        String idValue = mContext.getString(R.string.deductionIntegral) + mContext.getString(R.string.colon_zh) + goods.deductionIntegral ;
        holder.deductionIntegral.setText(idValue);

        return convertView;
    }

    private static class ViewHolder {
        private TextView code;
        private TextView type;
        private TextView num;
        private TextView price;
        private TextView totalPrice;
        private TextView deductionIntegral;

        private ViewHolder(View view) {
            this.code = view.findViewById(R.id.code);
            this.type = view.findViewById(R.id.goodsClassType);
            this.num = view.findViewById(R.id.num);

            this.price = view.findViewById(R.id.price);
            this.totalPrice = view.findViewById(R.id.totalPrice);
            this.deductionIntegral = view.findViewById(R.id.deduction);
        }
    }
}
