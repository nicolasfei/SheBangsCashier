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

public class NewGoodsSaleStatisticsAdapter extends BaseAdapter {
    public Context mContext;
    public List<NewGoodsSaleStatistics> saleStatistics;
    private boolean isBusy = false;                         //表示list view是否在快速滑动

    public NewGoodsSaleStatisticsAdapter(Context context, List<NewGoodsSaleStatistics> saleStatistics) {
        this.mContext = context;
        this.saleStatistics = saleStatistics;
    }

    @Override
    public int getCount() {
        return this.saleStatistics == null ? 0 : this.saleStatistics.size();
    }

    @Override
    public Object getItem(int position) {
        return this.saleStatistics == null ? null : this.saleStatistics.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(this.mContext).inflate(R.layout.new_goods_sale_statistics_item, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        NewGoodsSaleStatistics s = saleStatistics.get(position);
        //加载图片
        if (!isBusy) {
            ImageLoadClass.getInstance().displayImage(s.img, holder.photo, false);
        } else {
            ImageLoadClass.getInstance().displayImage(s.img, holder.photo, true);
        }

        String codeValue = mContext.getString(R.string.code) + mContext.getString(R.string.colon) + "<font color=\"black\"><big>" + s.code + "</big></font>";
        holder.code.setText(Html.fromHtml(codeValue, Html.FROM_HTML_MODE_COMPACT));

        String sIdValue = mContext.getString(R.string.sId) + mContext.getString(R.string.colon) + "<font color=\"black\"><big>" + s.sId + "</big></font>";
        holder.sId.setText(Html.fromHtml(sIdValue, Html.FROM_HTML_MODE_COMPACT));

        String amountValue = mContext.getString(R.string.amount) + mContext.getString(R.string.colon) + "<font color=\"black\"><big>" + s.amount + "</big></font>";
        holder.amount.setText(Html.fromHtml(amountValue, Html.FROM_HTML_MODE_COMPACT));

        String seasonNameValue = mContext.getString(R.string.seasonName) + mContext.getString(R.string.colon) + "<font color=\"black\"><big>" + s.seasonName + "</big></font>";
        holder.season.setText(Html.fromHtml(seasonNameValue, Html.FROM_HTML_MODE_COMPACT));

        String priceValue = mContext.getString(R.string.salePrice) + mContext.getString(R.string.colon) + "<font color=\"black\"><big>" + s.saleTotalPrice + "</big></font>";
        holder.price.setText(Html.fromHtml(priceValue, Html.FROM_HTML_MODE_COMPACT));

        String goodsIdValue = mContext.getString(R.string.goodsId) + mContext.getString(R.string.colon) + "<font color=\"black\"><big>" + s.goodsId + "</big></font>";
        holder.newGoods.setText(Html.fromHtml(goodsIdValue, Html.FROM_HTML_MODE_COMPACT));

        String saleNumValue = mContext.getString(R.string.saleNum) + mContext.getString(R.string.colon) + "<font color=\"black\"><big>" + s.saleNum + "</big></font>";
        holder.saleNum.setText(Html.fromHtml(saleNumValue, Html.FROM_HTML_MODE_COMPACT));

        String goodsClassValue = mContext.getString(R.string.goodsClass) + mContext.getString(R.string.colon) + "<font color=\"black\"><big>" + s.goodsClassName + "</big></font>";
        holder.type.setText(Html.fromHtml(goodsClassValue, Html.FROM_HTML_MODE_COMPACT));

        return convertView;
    }

    private static class ViewHolder {
        ImageView photo;
        TextView code, newGoods, sId, season, amount, type, price, saleNum;

        private ViewHolder(View root) {
            this.photo = root.findViewById(R.id.photo);

            this.newGoods = root.findViewById(R.id.newGoods);
            this.code = root.findViewById(R.id.code);
            this.sId = root.findViewById(R.id.sId);
            this.season = root.findViewById(R.id.season);

            this.amount = root.findViewById(R.id.amount);
            this.type = root.findViewById(R.id.type);
            this.price = root.findViewById(R.id.price);
            this.saleNum = root.findViewById(R.id.saleNum);
        }
    }
}
