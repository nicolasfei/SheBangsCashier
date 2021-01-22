package com.nicolas.shebangscashier.ui.cash;

import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.nicolas.componentlibrary.multileveltree.TreeNode;
import com.nicolas.componentlibrary.multileveltree.TreeNodeViewDialog;
import com.nicolas.shebangscashier.BaseActivity;
import com.nicolas.shebangscashier.R;
import com.nicolas.shebangscashier.cashier.MyKeeper;

import java.util.List;

public class OldGoodsInputActivity extends BaseActivity implements View.OnClickListener {

    private TextView goodsClassType;
    private EditText goodsPriceType, goodsNum;
    private OldGoodsInputViewModel viewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_old_input);
        this.viewModel = new ViewModelProvider(this).get(OldGoodsInputViewModel.class);

        this.goodsClassType = findClickView(R.id.goodsClassType);
        this.goodsNum = findViewById(R.id.goodsNum);
        this.goodsPriceType = findViewById(R.id.goodsPriceType);

        findClickView(R.id.yes);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.goodsClassType:
                showGoodsClassTypeDialog();
                break;
            case R.id.yes:
                break;
            default:
                break;
        }
    }

    /**
     * 更新商品类型
     *
     * @param itemValue 商品类型
     */
    private void updateGoodsClassType(String itemValue) {
        String value = getString(R.string.goodsClassType) + "\u3000\u3000\u3000\u3000\u3000" + "<font color=\"black\">" + itemValue + "</font>";
        goodsClassType.setText(Html.fromHtml(value, Html.FROM_HTML_MODE_COMPACT));
    }

    /**
     * 商品类型对话框
     */
    private void showGoodsClassTypeDialog() {
        TreeNodeViewDialog.showTreeNodeViewDialog(this, getString(R.string.goodsClassChoice),
                MyKeeper.getInstance().getGoodsClassTree(), false, new TreeNodeViewDialog.OnTreeNodeViewDialogListener() {
                    @Override
                    public void onChoice(List<TreeNode> node) {
                        if (node != null && node.size() > 0) {
                            updateGoodsClassType((node.size() == 1 ? node.get(0).name : node.get(0).name + "..."));
                            for (TreeNode attr : node) {
//                                viewModel.getCondition().setGoodsClassType(attr.id);
                            }
                        }
                    }
                });
    }
}
