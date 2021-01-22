package com.nicolas.shebangscashier.ui.cash.data;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.nicolas.shebangscashier.R;

public class OldSaleDialog extends Dialog {

    private MultiAutoCompleteTextView completeTextView;
    private EditText num;
    private EditText price;
    private Button yes;
    private TextView titleTv;


    private String title;
    //监听接口
    private OnOldGoodsSaleDialogListener listener;

    public OldSaleDialog(@NonNull Context context) {
        super(context, R.style.CustomDialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_old_sale);
        //按空白处能取消动画
        setCanceledOnTouchOutside(true);
        //初始化界面控件
        initView();
        //初始化界面数据
        refreshView();
        //初始化界面控件的事件
        initEvent();
    }

    private void refreshView() {
        if (!TextUtils.isEmpty(title)){
            titleTv.setText(title);
        }
    }

    private void initEvent() {
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String goodsType = completeTextView.getText().toString();
                String numV = num.getText().toString();
                String priceV = price.getText().toString();

                if (goodsType.equals("") || numV.equals("") || priceV.equals("")) {
                    Toast.makeText(getContext(), getContext().getString(R.string.inputNull), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (listener != null) {
                    listener.saleOldGoods(completeTextView.getText().toString(), Integer.parseInt(num.getText().toString()), Float.parseFloat(price.getText().toString()));
                }
            }
        });
    }

    private void initView() {
        completeTextView = findViewById(R.id.multiAutoCompleteTextView8);
        num = findViewById(R.id.editText8);
        price = findViewById(R.id.editText9);
        yes = findViewById(R.id.button12);
        titleTv = findViewById(R.id.textView3);
    }

    public OldSaleDialog setTitle(String title) {
        this.title = title;
        return this ;
    }

    public void setOnOldGoodsSaleDialogListener(OnOldGoodsSaleDialogListener listener) {
        this.listener = listener;
    }

    public interface OnOldGoodsSaleDialogListener {
        void saleOldGoods(String goodsType, int num, float price);
    }
}
