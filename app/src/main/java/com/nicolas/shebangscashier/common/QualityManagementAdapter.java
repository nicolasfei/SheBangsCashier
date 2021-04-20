package com.nicolas.shebangscashier.common;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.nicolas.shebangscashier.R;
import com.nicolas.toollibrary.imageload.ImageLoadClass;

import java.util.List;

public class QualityManagementAdapter extends BaseAdapter {

    private Context mContext;
    private List<QualityManagementData> dataList;

    private OnOperateListener listener;
    private boolean isBusy = false;                         //表示list view是否在快速滑动

    public QualityManagementAdapter(Context context, List<QualityManagementData> dataList) {
        this.mContext = context;
        this.dataList = dataList;
    }

    public void setOnOperateListener(OnOperateListener listener) {
        this.listener = listener;
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
            convertView = LayoutInflater.from(this.mContext).inflate(R.layout.goods_transfer_item, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        QualityManagementData s = dataList.get(position);
        //加载图片
        if (!isBusy) {
//            ImageLoadClass.getInstance().displayImage(s.img, holder.photo, false);
        } else {
            ImageLoadClass.getInstance().displayImage(s.img, holder.photo, true);
        }
        holder.photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onOperate2(position);
                }
            }
        });


        String shopValue = mContext.getString(R.string.shop) + mContext.getString(R.string.colon) + "<font color=\"black\"><big>" + s.fId + "</big></font>";
        holder.shop.setText(Html.fromHtml(shopValue, Html.FROM_HTML_MODE_COMPACT));

        String seasonNameValue = mContext.getString(R.string.seasonName) + mContext.getString(R.string.colon) + "<font color=\"black\"><big>" + s.seasonName + "</big></font>";
        holder.seasonName.setText(Html.fromHtml(seasonNameValue, Html.FROM_HTML_MODE_COMPACT));

        String qualityClassNameValue = mContext.getString(R.string.qualityClassName) + mContext.getString(R.string.colon) + "<font color=\"black\"><big>" + s.qualityClassName + "</big></font>";
        holder.qualityClassName.setText(Html.fromHtml(qualityClassNameValue, Html.FROM_HTML_MODE_COMPACT));

        String remarkValue = mContext.getString(R.string.remark) + mContext.getString(R.string.colon) + "<font color=\"black\"><big>" + s.reason + "</big></font>";
        holder.remark.setText(Html.fromHtml(remarkValue, Html.FROM_HTML_MODE_COMPACT));

        String ConfirmationTimeValue = mContext.getString(R.string.confirmationTime) + mContext.getString(R.string.colon) + "<font color=\"black\"><big>" + s.createTime + "</big></font>";
        holder.ConfirmationTime.setText(Html.fromHtml(ConfirmationTimeValue, Html.FROM_HTML_MODE_COMPACT));

        String goodsIdValue = mContext.getString(R.string.goodsId) + mContext.getString(R.string.colon) + "<font color=\"black\"><big>" + s.goodsId + "</big></font>";
        holder.newGoods.setText(Html.fromHtml(goodsIdValue, Html.FROM_HTML_MODE_COMPACT));

        String codeValue = mContext.getString(R.string.code) + mContext.getString(R.string.colon) + "<font color=\"black\"><big>" + s.barCodeId + "</big></font>";
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

        void onOperate2(int position);

        void onOperate3(int position);
    }

    private static class ViewHolder {

        private ImageView photo;
        private TextView shop, code, type, ConfirmationTime;
        private TextView seasonName, qualityClassName, newGoods, remark;
        private Button operate;

        private ViewHolder(View root) {
            this.photo = root.findViewById(R.id.photo);

            this.shop = root.findViewById(R.id.shop);
            this.code = root.findViewById(R.id.code);
            this.type = root.findViewById(R.id.type);
            this.ConfirmationTime = root.findViewById(R.id.ConfirmationTime);

            this.seasonName = root.findViewById(R.id.seasonName);
            this.qualityClassName = root.findViewById(R.id.qualityClassName);
            this.newGoods = root.findViewById(R.id.newGoods);
            this.remark = root.findViewById(R.id.remark);

            this.operate = root.findViewById(R.id.operate);
        }
    }
}
