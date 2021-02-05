package com.nicolas.shebangscashier.ui.cash;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.nicolas.componentlibrary.datetimepicker.DateTimePickerDialog;
import com.nicolas.shebangscashier.BaseActivity;
import com.nicolas.shebangscashier.R;
import com.nicolas.shebangscashier.cashier.MyKeeper;
import com.nicolas.shebangscashier.common.BitmapAdapter;
import com.nicolas.toollibrary.Tool;

public class NewRemittanceActivity extends BaseActivity implements View.OnClickListener {

    private NewRemittanceViewModel viewModel;

    private TextView cashTime;          //收银日期
    private EditText turnover;          //营业额
    private EditText integralDeduction; //抵扣积分
    private EditText actualCollection;  //实收款

    private EditText logistics;         //物流快递
    private EditText waterAndElectricityCost;   //水电气费
    private EditText repair;            //衣服修补
    private EditText discount;          //折扣开支
    private EditText other;             //其他开支
    private EditText wages;             //工资支出
    private EditText remarks;           //备注

    private BitmapAdapter vouchers;     //支付凭证

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_remittance);

        this.viewModel = new ViewModelProvider(this).get(NewRemittanceViewModel.class);
        //分店ID
        TextView store = findViewById(R.id.store);
        store.setText(MyKeeper.getInstance().getBranch().fId);

        //收银日期
        this.cashTime = findClickView(R.id.cashTime);
        //营业额
        this.turnover = findViewById(R.id.turnover);
        //抵扣积分
        this.integralDeduction = findViewById(R.id.integralDeduction);
        //实收款
        this.actualCollection = findViewById(R.id.actualCollection);

        //物流快递
        this.logistics = findViewById(R.id.logistics);
        //水电气费
        this.waterAndElectricityCost = findViewById(R.id.waterAndElectricityCost);
        //衣服修补
        this.repair = findViewById(R.id.repair);
        //折扣开支
        this.discount = findViewById(R.id.discount);
        //其他开支
        this.other = findViewById(R.id.other);
        //工资支出
        this.wages = findViewById(R.id.wages);
        //备注
        this.remarks = findViewById(R.id.remarks);

        //支付凭证List
        GridView list = findViewById(R.id.uploadList);
        this.vouchers = new BitmapAdapter(this, viewModel.getVouchers());
        list.setAdapter(this.vouchers);

        //上传图片
        findClickView(R.id.upload);
        //保存
        findClickView(R.id.save);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case RC_CHOOSE_PHOTO:
                Uri uri = data.getData();
                String filePath = Tool.getFilePathByUri(this, uri);     //图片文件路径

                if (!TextUtils.isEmpty(filePath)) {
                    viewModel.addPhoto(filePath);
                }
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cashTime:
                cashTimeChoice(viewModel.getRemittance().getCashDate());
                break;
            case R.id.upload:
                choosePhoto();
                break;
            case R.id.save:
                updateRemittance();
                break;
            default:
                break;
        }
    }

    /**
     * 更新或者添加汇款登记
     */
    private void updateRemittance() {
        updateRemittanceItem();
        if (!viewModel.getRemittance().isUpdate()){
            Toast.makeText(this, "有未填项!",Toast.LENGTH_SHORT).show();
            return;
        }
        super.showProgressDialog("数据提交中...");
        viewModel.updateRemittance();
    }

    /**
     * 更新汇款登记项
     */
    private void updateRemittanceItem() {
        String turnoverV = this.turnover.getText().toString();                      //营业额
        if (!TextUtils.isEmpty(turnoverV) && !turnoverV.equals(viewModel.getRemittance().getTurnover())) {
            viewModel.getRemittance().setTurnover(turnoverV);
        }
        String integralDeductionV = this.integralDeduction.getText().toString();    //抵扣积分
        if (!TextUtils.isEmpty(integralDeductionV) && !integralDeductionV.equals(viewModel.getRemittance().getIntegralDeduction())) {
            viewModel.getRemittance().setIntegralDeduction(integralDeductionV);
        }
        String actualCollectionV = this.actualCollection.getText().toString();      //实收款
        if (!TextUtils.isEmpty(actualCollectionV) && !actualCollectionV.equals(viewModel.getRemittance().getActualCollection())) {
            viewModel.getRemittance().setActualCollection(actualCollectionV);
        }

        String logisticsV = this.logistics.getText().toString();                    //物流快递
        if (!TextUtils.isEmpty(logisticsV) && !logisticsV.equals(viewModel.getRemittance().getLogistics())) {
            viewModel.getRemittance().setLogistics(logisticsV);
        }
        String waterAndElectricityCostV = this.waterAndElectricityCost.getText().toString();   //水电气费
        if (!TextUtils.isEmpty(waterAndElectricityCostV) && !waterAndElectricityCostV.equals(viewModel.getRemittance().getWaterAndElectricityCost())) {
            viewModel.getRemittance().setWaterAndElectricityCost(waterAndElectricityCostV);
        }
        String repairV = this.repair.getText().toString();                          //衣服修补
        if (!TextUtils.isEmpty(repairV) && !repairV.equals(viewModel.getRemittance().getRepair())) {
            viewModel.getRemittance().setRepair(repairV);
        }
        String discountV = this.discount.getText().toString();                      //折扣开支
        if (!TextUtils.isEmpty(discountV) && !discountV.equals(viewModel.getRemittance().getDiscount())) {
            viewModel.getRemittance().setDiscount(discountV);
        }
        String otherV = this.other.getText().toString();                            //其他开支
        if (!TextUtils.isEmpty(otherV) && !otherV.equals(viewModel.getRemittance().getOther())) {
            viewModel.getRemittance().setOther(otherV);
        }
        String wagesV = this.wages.getText().toString();                            //工资支出
        if (!TextUtils.isEmpty(wagesV) && !wagesV.equals(viewModel.getRemittance().getWages())) {
            viewModel.getRemittance().setWages(wagesV);
        }
        String remarksV = this.remarks.getText().toString();                        //备注
        if (!TextUtils.isEmpty(remarksV) && !remarksV.equals(viewModel.getRemittance().getRemarks())) {
            viewModel.getRemittance().setRemarks(remarksV);
        }
    }

    public static final int RC_CHOOSE_PHOTO = 2;

    /**
     * 跳转系统相册获取图片
     */
    private void choosePhoto() {
        Intent intentToPickPic = new Intent(Intent.ACTION_PICK, null);
        intentToPickPic.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intentToPickPic, RC_CHOOSE_PHOTO);
    }

    /**
     * 收银日期选择对话框
     *
     * @param itemValue 已选择的收银日期
     */
    private void cashTimeChoice(String itemValue) {
        DateTimePickerDialog.showDatePickerDialog(NewRemittanceActivity.this, itemValue, new DateTimePickerDialog.OnDateTimePickListener() {

            @Override
            public void OnDateTimePick(String date) {
                viewModel.getRemittance().setCashDate(date);
                updateCashTime(date);
            }
        });
    }

    /**
     * 更新收银日期
     *
     * @param itemValue 新的收银日期
     */
    private void updateCashTime(String itemValue) {
        cashTime.setText(itemValue);
    }

}
