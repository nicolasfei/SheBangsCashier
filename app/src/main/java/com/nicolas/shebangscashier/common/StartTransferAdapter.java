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

        String idValue = mContext.getString(R.string.goods_code) + mContext.getString(R.string.colon_zh) + goods.id;
        holder.textView0.setText(idValue);

        String goodsClassNameValue = mContext.getString(R.string.goods_type) + mContext.getString(R.string.colon_zh) + goods.goodsClassName;
        holder.textView1.setText(goodsClassNameValue);

        String fIdValue = mContext.getString(R.string.acceptanceShop) + mContext.getString(R.string.colon_zh) + goods.fId;
        holder.textView2.setText(fIdValue);

        String goodsIdValue = mContext.getString(R.string.goodsId) + mContext.getString(R.string.colon_zh) + goods.goodsId;
        holder.textView3.setText(goodsIdValue);

        String stateValue = mContext.getString(R.string.status) + mContext.getString(R.string.colon_zh) + goods.state;
        holder.textView4.setText(stateValue);

        String transferTimeValue = mContext.getString(R.string.transferTime) + mContext.getString(R.string.colon_zh) + goods.transferTime;
        holder.textView5.setText(transferTimeValue);

        return convertView;
    }

    private static class ViewHolder {
        private TextView textView0, textView1, textView2;
        private TextView textView3, textView4, textView5;

        private ViewHolder(View root) {
            this.textView0 = root.findViewById(R.id.textView0);
            this.textView1 = root.findViewById(R.id.textView1);
            this.textView2 = root.findViewById(R.id.textView2);

            this.textView3 = root.findViewById(R.id.textView3);
            this.textView4 = root.findViewById(R.id.textView4);
            this.textView5 = root.findViewById(R.id.textView5);
        }
    }
}
