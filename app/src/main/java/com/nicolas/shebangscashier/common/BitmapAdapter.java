package com.nicolas.shebangscashier.common;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nicolas.shebangscashier.R;

import java.util.List;

public class BitmapAdapter extends BaseAdapter {

    private Context mContext;
    private List<Bitmap> bitmaps;

    public BitmapAdapter(Context context, List<Bitmap> bitmaps) {
        this.mContext = context;
        this.bitmaps = bitmaps;
    }


    @Override
    public int getCount() {
        return bitmaps == null ? 0 : bitmaps.size();
    }

    @Override
    public Object getItem(int position) {
        return bitmaps == null ? null : bitmaps.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(this.mContext).inflate(R.layout.new_remittance_list_item, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Bitmap b = bitmaps.get(position);
        holder.imageView.setImageBitmap(b);
        return convertView;
    }

    private static class ViewHolder {
        private ImageView imageView;
        private TextView textView;

        private ViewHolder(View root) {
            this.imageView = root.findViewById(R.id.imageView);
            this.textView = root.findViewById(R.id.textView);
        }
    }
}
