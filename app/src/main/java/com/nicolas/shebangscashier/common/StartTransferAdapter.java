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


public class StartTransferAdapter extends BaseAdapter {

    private List<StartTransferInformation> informations;
    private Context mContext;

    public StartTransferAdapter(Context context, List<StartTransferInformation> informations) {
        this.mContext = context;
        this.informations = informations;
    }

    @Override
    public int getCount() {
        return informations == null ? null : informations.size();
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
            convertView = LayoutInflater.from(this.mContext).inflate(R.layout.start_transfer_item, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final StartTransferInformation goods = informations.get(position);

        String idValue = mContext.getString(R.string.goods_code) + mContext.getString(R.string.colon_zh) + "<font color=\"black\"><big>" + goods.id + "</big></font>";
        holder.textView0.setText(Html.fromHtml(idValue, Html.FROM_HTML_MODE_COMPACT));

        String goodsClassNameValue = mContext.getString(R.string.goods_type) + mContext.getString(R.string.colon_zh) + "<font color=\"black\"><big>" + goods.goodsClassName + "</big></font>";
        holder.textView1.setText(Html.fromHtml(goodsClassNameValue, Html.FROM_HTML_MODE_COMPACT));

        String fIdValue = mContext.getString(R.string.acceptanceShop) + mContext.getString(R.string.colon_zh) + "<font color=\"black\"><big>" + goods.fId + "</big></font>";
        holder.textView2.setText(Html.fromHtml(fIdValue, Html.FROM_HTML_MODE_COMPACT));

        String goodsIdValue = mContext.getString(R.string.goodsId) + mContext.getString(R.string.colon_zh) + "<font color=\"black\"><big>" + goods.goodsId + "</big></font>";
        holder.textView3.setText(Html.fromHtml(goodsIdValue, Html.FROM_HTML_MODE_COMPACT));

        String stateValue = mContext.getString(R.string.status) + mContext.getString(R.string.colon_zh) + "<font color=\"black\"><big>" + goods.state + "</big></font>";
        holder.textView4.setText(Html.fromHtml(stateValue, Html.FROM_HTML_MODE_COMPACT));

        String transferTimeValue = mContext.getString(R.string.transferTime) + mContext.getString(R.string.colon_zh) + "<font color=\"black\"><big>" + goods.transferTime + "</big></font>";
        holder.textView5.setText(Html.fromHtml(transferTimeValue, Html.FROM_HTML_MODE_COMPACT));

        return convertView;
    }

    private static class ViewHolder {
        private TextView textView0, textView1, textView2;
        private TextView textView3, textView4, textView5;

        private ViewHolder(View root){
            this.textView0 = root.findViewById(R.id.textView0);
            this.textView1 = root.findViewById(R.id.textView1);
            this.textView2 = root.findViewById(R.id.textView2);

            this.textView3 = root.findViewById(R.id.textView3);
            this.textView4 = root.findViewById(R.id.textView4);
            this.textView5 = root.findViewById(R.id.textView5);
        }
    }
}
