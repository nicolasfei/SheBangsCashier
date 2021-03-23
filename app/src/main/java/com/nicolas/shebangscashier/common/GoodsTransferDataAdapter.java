package com.nicolas.shebangscashier.common;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.nicolas.shebangscashier.R;
import com.nicolas.toollibrary.imageload.ImageLoadClass;

import java.util.List;

public class GoodsTransferDataAdapter extends BaseAdapter {

    private Context mContext;
    private List<GoodsTransferData> data;
    private OnOperateListener listener;
    private boolean isBusy = false;                         //表示list view是否在快速滑动

    public GoodsTransferDataAdapter(Context context, List<GoodsTransferData> data, OnOperateListener listener) {
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

        GoodsTransferData s = data.get(position);
        //加载图片
        if (!isBusy) {
//            ImageLoadClass.getInstance().displayImage(s.img, holder.photo, false);
        } else {
            ImageLoadClass.getInstance().displayImage(s.img, holder.photo, true);
        }

        String shopValue = mContext.getString(R.string.shop) + mContext.getString(R.string.colon) + "<font color=\"black\"><big>" + s.fIdFrom + "</big></font>";
        holder.shop.setText(Html.fromHtml(shopValue, Html.FROM_HTML_MODE_COMPACT));

        String sIdValue = mContext.getString(R.string.sId1) + mContext.getString(R.string.colon) + "<font color=\"black\"><big>" + s.sId + "</big></font>";
        holder.sId.setText(Html.fromHtml(sIdValue, Html.FROM_HTML_MODE_COMPACT));

        String transferTimeValue = mContext.getString(R.string.transferTime) + mContext.getString(R.string.colon) + "<font color=\"black\"><big>" + s.transferTime + "</big></font>";
        holder.transferTime.setText(Html.fromHtml(transferTimeValue, Html.FROM_HTML_MODE_COMPACT));

        String transferStatusValue = mContext.getString(R.string.transferStatus) + mContext.getString(R.string.colon) + "<font color=\"black\"><big>" + s.state + "</big></font>";
        holder.transferStatus.setText(Html.fromHtml(transferStatusValue, Html.FROM_HTML_MODE_COMPACT));

        String ConfirmationTimeValue = mContext.getString(R.string.confirmationTime) + mContext.getString(R.string.colon) + "<font color=\"black\"><big>" + s.okTime + "</big></font>";
        holder.ConfirmationTime.setText(Html.fromHtml(ConfirmationTimeValue, Html.FROM_HTML_MODE_COMPACT));

        String goodsIdValue = mContext.getString(R.string.goodsId) + mContext.getString(R.string.colon) + "<font color=\"black\"><big>" + s.goodsId + "</big></font>";
        holder.newGoods.setText(Html.fromHtml(goodsIdValue, Html.FROM_HTML_MODE_COMPACT));

        String codeValue = mContext.getString(R.string.code) + mContext.getString(R.string.colon) + "<font color=\"black\"><big>" + s.b_b_BarCode_Id + "</big></font>";
        holder.code.setText(Html.fromHtml(codeValue, Html.FROM_HTML_MODE_COMPACT));

        String goodsClassValue = mContext.getString(R.string.goodsClass) + mContext.getString(R.string.colon) + "<font color=\"black\"><big>" + s.goodsClassName + "</big></font>";
        holder.type.setText(Html.fromHtml(goodsClassValue, Html.FROM_HTML_MODE_COMPACT));

        holder.operate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onOperate(position);
                }
            }
        });

        return convertView;
    }

    public interface OnOperateListener {
        void onOperate(int position);
    }

    private static class ViewHolder {
        private ImageView photo;
        private TextView shop, transferTime, transferStatus, ConfirmationTime;
        private TextView sId, code, newGoods, type;
        private Button operate;

        private ViewHolder(View root) {
            this.photo = root.findViewById(R.id.photo);

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
