package com.nicolas.shebangscashier.ui.cash.data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.nicolas.shebangscashier.R;

import java.text.DecimalFormat;
import java.util.List;

public class SaleGoodsInformationAdapter extends BaseAdapter {

    private Context context;
    private List<SaleGoodsInformation> goodsInformation;

    public SaleGoodsInformationAdapter(Context context, List<SaleGoodsInformation> goodsInformation) {
        this.context = context;
        this.goodsInformation = goodsInformation;
    }

    @Override
    public int getCount() {
        return this.goodsInformation.size();
    }

    @Override
    public Object getItem(int position) {
        return this.goodsInformation.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(this.context).inflate(R.layout.sale_list_item, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final SaleGoodsInformation msg = goodsInformation.get(position);

        //商品名字
        String name = msg.goodsClassName;
        viewHolder.name.setText(name);

        //商品编号
        String id = msg.id;
        viewHolder.id.setText(id);

        //商品价格
        String price = context.getString(R.string.money) + new DecimalFormat("###.00").format(msg.price);
        viewHolder.price.setText(price);

        //销售状态
        String status = msg.goodsType;
        viewHolder.status.setText(status);

        //库存
        String store = context.getString(R.string.stock) + "(" + msg.stockAmount + ")";
        viewHolder.store.setText(store);

        //商品数量
        String num = "x" + msg.amount;
        viewHolder.num.setText(num);
        return convertView;
    }

    /**
     * view
     */
    private static class ViewHolder {
        private TextView name, id, price, status, store, num;

        private ViewHolder(View root) {
            this.name = root.findViewById(R.id.name);
            this.id = root.findViewById(R.id.id);
            this.price = root.findViewById(R.id.price);
            this.status = root.findViewById(R.id.describe);
            this.store = root.findViewById(R.id.store);
            this.num = root.findViewById(R.id.num);
        }
    }
}
