package com.nicolas.shebangscashier.ui.cash.data;

import android.content.Context;

import com.donkingliang.groupedadapter.adapter.GroupedRecyclerViewAdapter;
import com.donkingliang.groupedadapter.holder.BaseViewHolder;
import com.nicolas.shebangscashier.R;

import java.util.ArrayList;

public class SettlementGroupAdapter extends GroupedRecyclerViewAdapter {

    private Context mContext;
    private ArrayList<SettlementGroupMsg> mGroups;

    public SettlementGroupAdapter(Context context) {
        super(context);
        this.mContext = context;
    }

    public void setContent(ArrayList<SettlementGroupMsg> mGroups) {
        this.mGroups = mGroups;
    }

    @Override
    public int getGroupCount() {
        return mGroups == null ? 0 : mGroups.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        ArrayList<SettlementGoodsMsg> children = mGroups.get(groupPosition).getChildren();
        return children == null ? 0 : children.size();
    }

    @Override
    public boolean hasHeader(int groupPosition) {
        return true;
    }

    @Override
    public boolean hasFooter(int groupPosition) {
        return true;
    }

    @Override
    public int getHeaderLayout(int viewType) {
        return R.layout.settlement_list_head;
    }

    @Override
    public int getFooterLayout(int viewType) {
        return R.layout.settlement_list_footer;
    }

    @Override
    public int getChildLayout(int viewType) {
        return R.layout.settlement_list_item;
    }

    @Override
    public void onBindHeaderViewHolder(BaseViewHolder holder, int groupPosition) {
        SettlementGroupMsg msg = mGroups.get(groupPosition);
        SettlementGoodsMsg goodsMsg = msg.getHead();
        holder.setText(R.id.textView12, goodsMsg.type);
        holder.setText(R.id.textView13, goodsMsg.num);
        holder.setText(R.id.textView14, goodsMsg.price);
    }

    @Override
    public void onBindFooterViewHolder(BaseViewHolder holder, int groupPosition) {
        SettlementGroupMsg msg = mGroups.get(groupPosition);
        SettlementGoodsMsg goodsMsg = msg.getFooter();
        holder.setText(R.id.textView12, goodsMsg.type);
        holder.setText(R.id.textView13, "x" + goodsMsg.num);
        holder.setText(R.id.textView14, mContext.getString(R.string.money) + goodsMsg.price);
    }

    @Override
    public void onBindChildViewHolder(BaseViewHolder holder, int groupPosition, int childPosition) {
        SettlementGroupMsg msg = mGroups.get(groupPosition);
        SettlementGoodsMsg child = msg.getChildren().get(childPosition);
        holder.setText(R.id.textView12, child.type);
        holder.setText(R.id.textView13, "x" + child.num);
        holder.setText(R.id.textView14, mContext.getString(R.string.money) + child.price);
    }
}
