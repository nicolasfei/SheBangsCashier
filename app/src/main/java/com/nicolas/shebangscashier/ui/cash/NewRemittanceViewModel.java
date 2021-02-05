package com.nicolas.shebangscashier.ui.cash;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Message;
import android.text.TextUtils;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.nicolas.shebangscashier.R;
import com.nicolas.shebangscashier.app.MyApp;
import com.nicolas.shebangscashier.common.NewGoodsSaleStatistics;
import com.nicolas.shebangscashier.common.OperateError;
import com.nicolas.shebangscashier.common.OperateInUserView;
import com.nicolas.shebangscashier.common.OperateResult;
import com.nicolas.shebangscashier.communication.CommandResponse;
import com.nicolas.shebangscashier.communication.CommandTypeEnum;
import com.nicolas.shebangscashier.communication.CommandVo;
import com.nicolas.shebangscashier.communication.Invoker;
import com.nicolas.shebangscashier.communication.sale.SaleInterface;
import com.nicolas.toollibrary.HttpHandler;
import com.nicolas.toollibrary.Tool;
import com.nicolas.toollibrary.imageupload.ImageUpLoadClass;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewRemittanceViewModel extends ViewModel {

    private NewRemittance remittance;   //汇款登记
    private List<NewRemittance> queryRemittances;
    private MutableLiveData<OperateResult> updateRemittanceResult;      //汇款登记提交
    private MutableLiveData<OperateResult> queryRemittanceResult;       //汇款登记查询
    private MutableLiveData<OperateResult> querySingleRemittanceResult;       //汇款登记单个查询

    public NewRemittanceViewModel() {
        this.remittance = new NewRemittance();
        this.queryRemittances = new ArrayList<>();          //保存查询的汇款登记
        this.remittance.cashierTime = Tool.getTodayDate();     //默认当天

        this.updateRemittanceResult = new MutableLiveData<>();
        this.queryRemittanceResult = new MutableLiveData<>();
        this.querySingleRemittanceResult = new MutableLiveData<>();
    }

    /**
     * 获取vouchers
     *
     * @return vouchers
     */
    public List<Bitmap> getVouchers() {
        return remittance.vouchers;
    }

    /**
     * 获取登记内容类
     *
     * @return NewRemittance
     */
    public NewRemittance getRemittance() {
        return remittance;
    }

    /**
     * 添加图片
     *
     * @param filePath 图片文件地址
     */
    public void addPhoto(String filePath) {
        Bitmap bitmap = BitmapFactory.decodeFile(filePath, null);
        this.remittance.vouchers.add(bitmap);
    }

    /**
     * 删除照片
     *
     * @param pos 照片所在位置
     */
    public void deletePhoto(int pos) {
        if (this.remittance.vouchers.size() >= pos) {
            this.remittance.vouchers.remove(pos);
        }
    }

    /**
     * 更新或者上传汇款登记
     */
    public void updateRemittance() {
        this.uploadPhoto();
    }

    /**
     * 提交汇款登记
     */
    private void submitRemittance() {
        CommandVo vo = new CommandVo();
        vo.typeEnum = CommandTypeEnum.COMMAND_SALE;
        vo.url = SaleInterface.RemittanceEdit;
        vo.contentType = HttpHandler.ContentType_APP;
        vo.requestMode = HttpHandler.RequestMode_POST;
        Map<String, String> parameters = new HashMap<>();
        if (!TextUtils.isEmpty(remittance.cashierTime)) {
            parameters.put("cashierTime", remittance.cashierTime);
        }
        if (!TextUtils.isEmpty(remittance.id)) {
            parameters.put("id", remittance.id);
        }
        if (!TextUtils.isEmpty(remittance.electricalWaterCost)) {
            parameters.put("electricalWaterCost", remittance.electricalWaterCost);
        }
        if (!TextUtils.isEmpty(remittance.wageCost)) {
            parameters.put("wageCost", remittance.wageCost);
        }
        if (!TextUtils.isEmpty(remittance.expressCost)) {
            parameters.put("expressCost", remittance.expressCost);
        }
        if (!TextUtils.isEmpty(remittance.rentCost)) {
            parameters.put("rentCost", remittance.rentCost);
        }
        if (!TextUtils.isEmpty(remittance.clothesCost)) {
            parameters.put("clothesCost", remittance.clothesCost);
        }
        if (!TextUtils.isEmpty(remittance.discountCost)) {
            parameters.put("discountCost", remittance.discountCost);
        }
        if (!TextUtils.isEmpty(remittance.otherCost)) {
            parameters.put("otherCost", remittance.otherCost);
        }
        if (!TextUtils.isEmpty(remittance.remark)) {
            parameters.put("remark", remittance.remark);
        }
        if (!TextUtils.isEmpty(remittance.certificate)) {
            parameters.put("certificate", remittance.certificate);
        }

        vo.parameters = parameters;
        Invoker.getInstance().setOnEchoResultCallback(this.callback);
        Invoker.getInstance().exec(vo);
    }

    /**
     * 上传图片
     */
    private void uploadPhoto() {
        new ImageUpLoadClass("https://file.scdawn.com/CashierImg/upload", this.remittance.vouchers,
                new ImageUpLoadClass.OnImageUploadFinishCallBack() {
                    @Override
                    public void uploadFinish(List<String> uploadResult) {
                        StringBuilder builder = new StringBuilder();
                        for (String c : uploadResult) {
                            builder.append(c).append(",");
                        }
                        String certificate = builder.toString();
                        remittance.certificate = certificate.substring(0, certificate.length() - 1);
                        //提交汇款登记
                        submitRemittance();
                    }

                    @Override
                    public void uploadError(int pos) {
                        updateRemittanceResult.setValue(new OperateResult(new OperateError(-1, "上传图片失败", null)));
                    }
                }).upload();
    }

    /**
     * 查询结果返回接口
     */
    private Invoker.OnExecResultCallback callback = new Invoker.OnExecResultCallback() {
        @Override
        public void execResult(CommandResponse result) {
            switch (result.url) {
                case SaleInterface.RemittanceEdit:
                    if (result.success) {
                        updateRemittanceResult.setValue(new OperateResult(new OperateInUserView(null)));
                    } else {
                        updateRemittanceResult.setValue(new OperateResult(new OperateError(result.code, result.msg, null)));
                    }
                    break;
                case SaleInterface.Remittance:
                    if (result.success) {
//                        saleNum = result.saleNum;
//                        saleTotalPrice = result.saleTotalPrice;
                        queryRemittanceResult.setValue(new OperateResult(new OperateInUserView(null)));
                    } else {
                        queryRemittanceResult.setValue(new OperateResult(new OperateError(result.code, result.msg, null)));
                    }
                    break;
                case SaleInterface.RemittanceById:      //单个查询
                    break;
                default:
                    break;
            }
        }
    };

    public class NewRemittance {
        private String id = "";                 //汇款登记ID，修改时存在，新增是为空
        private String b_b_Branch_Id="";
        private String cashierTime = "";        //收银日期
        private String turnover = "";           //营业额
        private String rentCost = "";           //抵扣积分
        private String actualCollection = "";   //实收款

        private String expressCost = "";            //物流快递
        private String electricalWaterCost = "";    //水电气费
        private String clothesCost = "";            //衣服修补
        private String discountCost = "";           //折扣开支
        private String otherCost = "";              //其他开支
        private String wageCost = "";               //工资支出
        private String remark = "";             //备注
        private String valid = "";              //是否启用
        private String certificate = "";        //凭证


        private List<Bitmap> vouchers;          //支付凭证List

        private boolean isUpdate = false;

        public NewRemittance() {
            this.vouchers = new ArrayList<>();
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getCashierTime() {
            return cashierTime;
        }

        public void setCashDate(String cashierTime) {
            if (!this.cashierTime.equals(cashierTime)) {
                this.cashierTime = cashierTime;
                this.isUpdate = true;
            }
        }

        public void setRemarks(String remarks) {
            if (!this.remark.equals(remarks)) {
                this.remark = remarks;
                this.isUpdate = true;
            }
        }

        public String getRemark() {
            return remark;
        }

        public void setWageCost(String wages) {
            if (!this.wageCost.equals(wages)) {
                this.wageCost = wages;
                this.isUpdate = true;
            }
        }

        public String getWageCost() {
            return wageCost;
        }

        public void setOtherCost(String other) {
            if (!this.otherCost.equals(other)) {
                this.otherCost = other;
                this.isUpdate = true;
            }
        }

        public String getOtherCost() {
            return otherCost;
        }

        public void setDiscountCost(String discount) {
            if (!this.discountCost.equals(discount)) {
                this.discountCost = discount;
                this.isUpdate = true;
            }
        }

        public String getDiscountCost() {
            return discountCost;
        }

        public void setClothesCost(String repair) {
            if (!this.clothesCost.equals(repair)) {
                this.clothesCost = repair;
                this.isUpdate = true;
            }
        }

        public String getClothesCost() {
            return clothesCost;
        }

        public void setElectricalWaterCost(String waterAndElectricityCost) {
            if (!this.electricalWaterCost.equals(waterAndElectricityCost)) {
                this.electricalWaterCost = waterAndElectricityCost;
                this.isUpdate = true;
            }
        }

        public String getElectricalWaterCost() {
            return electricalWaterCost;
        }

        public void setExpressCost(String logistics) {
            if (!this.expressCost.equals(logistics)) {
                this.expressCost = logistics;
                this.isUpdate = true;
            }
        }

        public String getExpressCost() {
            return expressCost;
        }

        public void setTurnover(String turnover) {
            if (!this.turnover.equals(turnover)) {
                this.turnover = turnover;
                this.isUpdate = true;
            }
        }

        public String getTurnover() {
            return turnover;
        }

        public void setRentCost(String integralDeduction) {
            if (!this.rentCost.equals(integralDeduction)) {
                this.rentCost = integralDeduction;
                this.isUpdate = true;
            }
        }

        public String getRentCost() {
            return rentCost;
        }

        public void setActualCollection(String actualCollection) {
            if (!this.actualCollection.equals(actualCollection)) {
                this.actualCollection = actualCollection;
                this.isUpdate = true;
            }
        }

        public String getActualCollection() {
            return actualCollection;
        }

        public boolean isUpdate() {
            return isUpdate;
        }

        public void resetIsUpdate() {
            isUpdate = false;
        }
    }
}
