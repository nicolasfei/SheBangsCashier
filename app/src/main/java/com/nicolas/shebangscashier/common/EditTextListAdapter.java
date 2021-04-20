package com.nicolas.shebangscashier.common;

import android.content.Context;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
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
//        final ViewHolder holder;
//        if (convertView == null) {
//            convertView = LayoutInflater.from(this.mContext).inflate(R.layout.text_edit_item, parent, false);
//            holder = new ViewHolder(convertView);
//            convertView.setTag(holder);
//        } else {
//            holder = (ViewHolder) convertView.getTag();
//        }
        View newView = LayoutInflater.from(this.mContext).inflate(R.layout.text_edit_item, parent, false);
        final ViewHolder holder = new ViewHolder(newView);

        ReplenishmentInformation.Property s = properties.get(position);

        String colorValue = "<font color=\"black\">" + s.color + "</font>";
        holder.color.setText(Html.fromHtml(colorValue, Html.FROM_HTML_MODE_COMPACT));

        String sizeValue = "<font color=\"black\">" + s.size + "</font>";
        holder.size.setText(Html.fromHtml(sizeValue, Html.FROM_HTML_MODE_COMPACT));

//        holder.num.setOnEditorActionListener(null);
        holder.num.setText(s.val == 0 ? "" : String.valueOf(s.val));
//        holder.num.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                Log.d("onEditorAction", "onEditorAction: actionId is "+actionId);
//                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_NEXT) {
//                    String num = holder.num.getText().toString();
//                    if (!TextUtils.isEmpty(num)) {
//                        s.val = Integer.parseInt(num);
//                    }
//                }
//                return false;
//            }
//        });

        holder.num.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String num = holder.num.getText().toString();
                if (!TextUtils.isEmpty(num)) {
                    s.val = Integer.parseInt(num);
                }
            }
        });

        return newView;
    }

    private static class ViewHolder {
        private TextView color, size;
        private EditText num;

        private ViewHolder(View root) {
            this.color = root.findViewById(R.id.color);
            this.size = root.findViewById(R.id.size);
            this.num = root.findViewById(R.id.num);
        }
    }
}
