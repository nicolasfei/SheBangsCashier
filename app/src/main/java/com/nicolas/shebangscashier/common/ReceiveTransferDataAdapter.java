package com.nicolas.shebangscashier.common;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.nicolas.shebangscashier.R;
import com.nicolas.toollibrary.imageload.ImageLoadClass;

import java.util.List;

public class ReceiveTransferDataAdapter extends BaseAdapter {

    private Context mContext;
    private List<ReceiveTransferData> data;
    private OnOperateListener listener;
    private boolean isBusy = false;                         //表示list view是否在快速滑动

    public ReceiveTransferDataAdapter(Context context, List<ReceiveTransferData> data, OnOperateListener listener) {
        this.mContext = context;
        this.data = data;
        this.listener = listener;
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
            convertView = LayoutInflater.from(this.mContext).inflate(R.layout.goods_transfer_item, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ReceiveTransferData s = data.get(position);
        //加载图片
        if (!isBusy) {
//            ImageLoadClass.getInstance().displayImage(s.img, holder.photo, false);
        } else {
            ImageLoadClass.getInstance().displayImage(s.img, holder.photo, true);
        }

        String shopValue = mContext.getString(R.string.shop) + mContext.getString(R.string.colon) + s.fIdFrom;
        holder.shop.setText(shopValue);
        holder.shop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onOperate2(position);
                }
            }
        });

        String sIdValue = mContext.getString(R.string.sId1) + mContext.getString(R.string.colon) + s.sId;
        holder.sId.setText(sIdValue);

        String transferTimeValue = mContext.getString(R.string.transferTime) + mContext.getString(R.string.colon) + s.transferTime;
        holder.transferTime.setText(transferTimeValue);

        String transferStatusValue = mContext.getString(R.string.transferStatus) + mContext.getString(R.string.colon) + s.state;
        holder.transferStatus.setText(transferStatusValue);

        String ConfirmationTimeValue = mContext.getString(R.string.confirmationTime) + mContext.getString(R.string.colon) + s.okTime;
        holder.ConfirmationTime.setText(ConfirmationTimeValue);

        String goodsIdValue = mContext.getString(R.string.goodsId) + mContext.getString(R.string.colon) + s.goodsId;
        holder.newGoods.setText(goodsIdValue);

        String codeValue = mContext.getString(R.string.code) + mContext.getString(R.string.colon) + s.b_b_BarCode_Id;
        holder.code.setText(codeValue);

        String goodsClassValue = mContext.getString(R.string.goodsClass) + mContext.getString(R.string.colon) + s.goodsClassName;
        holder.type.setText(goodsClassValue);

        holder.operate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onOperate(position);
                }
            }
        });

        holder.checkBox.setOnCheckedChangeListener(null);
        holder.checkBox.setChecked(s.isSelect);
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                s.isSelect = isChecked;
                if (listener != null) {
                    listener.onOperate3(position);
                }
            }
        });
        return convertView;
    }

    public interface OnOperateListener {
        void onOperate(int position);

        void onOperate2(int position);

        void onOperate3(int position);
    }

    private static class ViewHolder {
        private ImageView photo;
        private TextView shop, transferTime, transferStatus, ConfirmationTime;
        private TextView sId, code, newGoods, type;
        private Button operate;
        private CheckBox checkBox;

        private ViewHolder(View root) {
            this.photo = root.findViewById(R.id.photo);
            this.checkBox = root.findViewById(R.id.checkBox);

            this.shop = root.findViewById(R.id.shop);
            this.transferTime = root.findViewById(R.id.transferTime);
            this.transferStatus = root.findViewById(R.id.transferStatus);
            this.ConfirmationTime = root.findViewById(R.id.ConfirmationTime);
            this.sId = root.findViewById(R.id.sId);

            this.code = root.findViewById(R.id.code);
            this.newGoods = root.findViewById(R.id.newGoods);
            this.type = root.findViewById(R.id.type);
            this.operate = root.findViewById(R.id.operate);
        }
    }
}
