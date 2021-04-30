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

public class GoodsInventoryAdapter extends BaseAdapter {
    private Context mContext;
    private List<GoodsInventoryData> dataList;

    public GoodsInventoryAdapter(Context context, List<GoodsInventoryData> list) {
        this.mContext = context;
        this.dataList = list;
    }

    @Override
    public int getCount() {
        return dataList == null ? 0 : dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataList == null ? null : dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(this.mContext).inflate(R.layout.goods_inventory_item, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        GoodsInventoryData s = dataList.get(position);

        String value1 = mContext.getString(R.string.goodsId) + mContext.getString(R.string.colon)  + s.goodsID ;
        holder.goodsId.setText(value1);

        String value2 = mContext.getString(R.string.branch) + mContext.getString(R.string.colon)  + s.branch ;
        holder.branch.setText(value2);

        String value3 = mContext.getString(R.string.area) + mContext.getString(R.string.colon)  + s.area ;
        holder.area.setText(value3);

        String value4 = mContext.getString(R.string.stock) + mContext.getString(R.string.colon)  + s.stock ;
        holder.stock.setText(value4);

        return convertView;
    }

    private static class ViewHolder {
        private TextView goodsId, branch;
        private TextView area, stock;

        private ViewHolder(View root) {
            this.goodsId = root.findViewById(R.id.goodsID);
            this.branch = root.findViewById(R.id.branch);
            this.area = root.findViewById(R.id.area);
            this.stock = root.findViewById(R.id.stock);
        }
    }
}
