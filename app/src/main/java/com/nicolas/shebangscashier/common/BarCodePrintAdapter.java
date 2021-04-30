package com.nicolas.shebangscashier.common;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.nicolas.shebangscashier.R;

import java.util.List;

public class BarCodePrintAdapter extends BaseAdapter {

    private Context mContext;
    private List<BarCodeInformation> information;
    private OnCheckStateChangedListener stateChangedListener;

    public BarCodePrintAdapter(Context context, List<BarCodeInformation> information) {
        this.mContext = context;
        this.information = information;
    }

    @Override
    public int getCount() {
        return information == null ? 0 : information.size();
    }

    @Override
    public Object getItem(int position) {
        return information == null ? null : information.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(this.mContext).inflate(R.layout.goods_transfer_item, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        BarCodeInformation info = information.get(position);

        String codeValue = mContext.getString(R.string.code) + mContext.getString(R.string.colon_zh)  + info.id ;
        holder.barCode.setText(codeValue);

        String colorValue = mContext.getString(R.string.code) + mContext.getString(R.string.colon_zh)  + info.color ;
        holder.color.setText(colorValue);

        String stateValue = mContext.getString(R.string.barCodeState) + mContext.getString(R.string.colon_zh)  + info.state ;
        holder.state.setText(stateValue);

        String sizeValue = mContext.getString(R.string.size) + mContext.getString(R.string.colon_zh)  + info.size ;
        holder.size.setText(sizeValue);

        String priceValue = mContext.getString(R.string.price) + mContext.getString(R.string.colon_zh)  + info.salePrice ;
        holder.price.setText(priceValue);

        String sourceValue = mContext.getString(R.string.source) + mContext.getString(R.string.colon_zh)  + info.source ;
        holder.source.setText(sourceValue);

        String inStoreTimeValue = mContext.getString(R.string.inStoreTime) + mContext.getString(R.string.colon_zh)  + info.getTime ;
        holder.inStoreTime.setText(inStoreTimeValue);

        String printTimeValue = mContext.getString(R.string.printTime) + mContext.getString(R.string.colon_zh)  + info.printTime ;
        holder.printTime.setText(printTimeValue);

        holder.choice.setOnCheckedChangeListener(null);
        holder.choice.setChecked(info.isSelect);
        holder.choice.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                info.isSelect = isChecked;
                if (stateChangedListener != null) {
                    stateChangedListener.CheckStateChanged(info);
                }
            }
        });

        return convertView;
    }

    public void setOnCheckStateChangedListener(OnCheckStateChangedListener listener) {
        this.stateChangedListener = listener;
    }

    public interface OnCheckStateChangedListener {
        void CheckStateChanged(BarCodeInformation info);
    }

    private static class ViewHolder {
        private TextView barCode, state;
        private TextView color, size;
        private TextView price, source;
        private TextView inStoreTime, printTime;
        private CheckBox choice;

        private ViewHolder(View root) {
            this.barCode = root.findViewById(R.id.barCode);
            this.state = root.findViewById(R.id.state);
            this.color = root.findViewById(R.id.color);
            this.size = root.findViewById(R.id.size);

            this.price = root.findViewById(R.id.price);
            this.source = root.findViewById(R.id.source);
            this.inStoreTime = root.findViewById(R.id.inStoreTime);
            this.printTime = root.findViewById(R.id.printTime);

            this.choice = root.findViewById(R.id.checkBox);
        }
    }
}
