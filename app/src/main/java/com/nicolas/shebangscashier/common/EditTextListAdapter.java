package com.nicolas.shebangscashier.common;

import android.content.Context;
import android.text.Html;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.nicolas.shebangscashier.R;
import com.nicolas.toollibrary.imageload.ImageLoadClass;

import java.util.List;

public class EditTextListAdapter extends BaseAdapter {

    private List<ReplenishmentInformation.Property> properties;
    private Context mContext;

    public EditTextListAdapter(Context context, List<ReplenishmentInformation.Property> properties) {
        this.mContext = context;
        this.properties = properties;
    }

    @Override
    public int getCount() {
        return properties == null ? 0 : properties.size();
    }

    @Override
    public Object getItem(int position) {
        return properties == null ? null : properties.get(position);
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

        ReplenishmentInformation.Property s = properties.get(position);

        String colorValue = "<font color=\"black\"><big>" + s.color + "," + s.size + "</big></font>";
        holder.textView.setText(Html.fromHtml(colorValue, Html.FROM_HTML_MODE_COMPACT));

        holder.editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT) {
                    String num = holder.editText.getText().toString();
                    if (!TextUtils.isEmpty(num)) {
                        s.val = Integer.parseInt(num);
                    }
                }
                return false;
            }
        });

        return convertView;
    }

    private static class ViewHolder {
        private TextView textView;
        private EditText editText;

        private ViewHolder(View root) {
            this.textView = root.findViewById(R.id.textView);
            this.editText = root.findViewById(R.id.editText);
        }
    }
}
