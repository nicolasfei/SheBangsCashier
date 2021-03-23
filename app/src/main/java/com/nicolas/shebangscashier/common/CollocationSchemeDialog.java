package com.nicolas.shebangscashier.common;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.nicolas.shebangscashier.R;
import com.nicolas.toollibrary.imageload.ImageLoadClass;

public class CollocationSchemeDialog extends Dialog {

    private ImageView photo;
    private TextView scheme;
    private TextView collocation;
    private Button positiveBn, negativeBn;

    private String schemeValue;
    private String positiveValue, negativeValue;
    private String photoValue;
    private String collocationValue;

    public CollocationSchemeDialog(@NonNull Context context) {
        super(context);
    }

    public CollocationSchemeDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.collocation_scheme_dialog);
        setCanceledOnTouchOutside(false);       //按空白处不能取消动画
        //初始化界面控件
        initView();
        //初始化界面数据
        refreshView();
        //初始化界面控件的事件
        initEvent();
    }

    @Override
    public void show() {
        super.show();
        refreshView();
    }

    private void initView() {
        this.photo = findViewById(R.id.photo);
        this.collocation = findViewById(R.id.collocation);
        this.scheme = findViewById(R.id.scheme);
//        this.positiveBn = findViewById(R.id.positiveBn);
//        this.negativeBn = findViewById(R.id.negativeBn);
    }

    private void refreshView() {
        if (!TextUtils.isEmpty(collocationValue)) {
            this.collocation.setText(collocationValue);
            this.collocation.setVisibility(View.VISIBLE);
        } else {
            this.collocation.setVisibility(View.INVISIBLE);
        }

        if (!TextUtils.isEmpty(schemeValue)) {
            this.scheme.setText(schemeValue);
            this.scheme.setVisibility(View.VISIBLE);
        } else {
            this.scheme.setVisibility(View.INVISIBLE);
        }

        if (!TextUtils.isEmpty(positiveValue)) {
            this.positiveBn.setText(positiveValue);
            this.positiveBn.setVisibility(View.VISIBLE);
        } else {
            this.positiveBn.setVisibility(View.INVISIBLE);
        }

        if (!TextUtils.isEmpty(negativeValue)) {
            this.negativeBn.setText(negativeValue);
            this.negativeBn.setVisibility(View.VISIBLE);
        } else {
            this.negativeBn.setVisibility(View.INVISIBLE);
        }

        if (!TextUtils.isEmpty(photoValue)) {
            ImageLoadClass.getInstance().displayImage(photoValue, this.photo, false);
            this.photo.setVisibility(View.VISIBLE);
        } else {
            this.photo.setVisibility(View.INVISIBLE);
        }
    }

    private void initEvent() {
        //设置确定按钮被点击后，向外界提供监听
        positiveBn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClickBottomListener != null) {
                    onClickBottomListener.onPositiveClick();
                }
            }
        });
        //设置取消按钮被点击后，向外界提供监听
        negativeBn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClickBottomListener != null) {
                    onClickBottomListener.onNegativeClick();
                }
            }
        });
    }

    /**
     * 设置确定取消按钮的回调
     */
    public OnClickBottomListener onClickBottomListener;

    public CollocationSchemeDialog setOnClickBottomListener(OnClickBottomListener onClickBottomListener) {
        this.onClickBottomListener = onClickBottomListener;
        return this;
    }

    public interface OnClickBottomListener {
        /**
         * 点击确定按钮事件
         */
        void onPositiveClick();

        /**
         * 点击取消按钮事件
         */
        void onNegativeClick();
    }

    public CollocationSchemeDialog setSchemeValue(String schemeValue) {
        this.schemeValue = schemeValue;
        return this;
    }

    public CollocationSchemeDialog setNegativeValue(String negativeValue) {
        this.negativeValue = negativeValue;
        return this;
    }

    public CollocationSchemeDialog setPositiveValue(String positiveValue) {
        this.positiveValue = positiveValue;
        return this;
    }

    public CollocationSchemeDialog setPhotoValue(String photoValue) {
        this.photoValue = photoValue;
        return this;
    }

    public CollocationSchemeDialog setCollocationValue(String collocationValue) {
        this.collocationValue = collocationValue;
        return this;
    }
}
