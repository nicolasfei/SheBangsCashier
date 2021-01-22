package com.nicolas.shebangscashier.common;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.nicolas.shebangscashier.R;

import java.util.List;

public class DayStatisticsAdapter extends BaseAdapter {

    private Context mContext;
    private List<DayStatistics> statistics;

    public DayStatisticsAdapter(Context context, List<DayStatistics> statistics) {
        this.mContext = context;
        this.statistics = statistics;
    }

    @Override
    public int getCount() {
        return statistics == null ? 0 : statistics.size();
    }

    @Override
    public Object getItem(int position) {
        return statistics == null ? null : statistics.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(this.mContext).inflate(R.layout.sale_statistics_item, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        DayStatistics d = statistics.get(position);
        holder.item1.setText(d.goodsClassName);
        holder.item2.setText(String.valueOf(d.saleNum));
        holder.item3.setText(String.valueOf(d.saleTotalPrice));
        return convertView;
    }

    private static class ViewHolder {
        private TextView item1, item2, item3;

        private ViewHolder(View root) {
            this.item1 = root.findViewById(R.id.item1);
            this.item2 = root.findViewById(R.id.item2);
            this.item3 = root.findViewById(R.id.item3);
        }
    }
}
