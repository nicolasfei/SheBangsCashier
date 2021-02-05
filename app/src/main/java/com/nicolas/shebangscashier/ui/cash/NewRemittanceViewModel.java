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
import com.nicolas.shebangscashier.common.NewRemittance;
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
import org.json.JSONObject;

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
        this.queryRemittances = new ArrayList<>();                  //保存查询的汇款登记
        this.remittance.setCashierTime(Tool.getTodayDate());        //默认当天

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
        return remittance.getVouchers();
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
        this.remittance.getVouchers().add(bitmap);
    }

    /**
     * 删除照片
     *
     * @param pos 照片所在位置
     */
    public void deletePhoto(int pos) {
        if (this.remittance.getVouchers().size() >= pos) {
            this.remittance.getVouchers().remove(pos);
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
        if (!TextUtils.isEmpty(remittance.getCashierTime())) {
            parameters.put("cashierTime", remittance.getCashierTime());
        }
        if (!TextUtils.isEmpty(remittance.getId())) {
            parameters.put("id", remittance.getId());
        }
        if (!TextUtils.isEmpty(remittance.getElectricalWaterCost())) {
            parameters.put("electricalWaterCost", remittance.getElectricalWaterCost());
        }
        if (!TextUtils.isEmpty(remittance.getWageCost())) {
            parameters.put("wageCost", remittance.getWageCost());
        }
        if (!TextUtils.isEmpty(remittance.getExpressCost())) {
            parameters.put("expressCost", remittance.getExpressCost());
        }
        if (!TextUtils.isEmpty(remittance.getRentCost())) {
            parameters.put("rentCost", remittance.getRentCost());
        }
        if (!TextUtils.isEmpty(remittance.getClothesCost())) {
            parameters.put("clothesCost", remittance.getClothesCost());
        }
        if (!TextUtils.isEmpty(remittance.getDiscountCost())) {
            parameters.put("discountCost", remittance.getDiscountCost());
        }
        if (!TextUtils.isEmpty(remittance.getOtherCost())) {
            parameters.put("otherCost", remittance.getOtherCost());
        }
        if (!TextUtils.isEmpty(remittance.getRemark())) {
            parameters.put("remark", remittance.getRemark());
        }
        if (!TextUtils.isEmpty(remittance.getCertificate())) {
            parameters.put("certificate", remittance.getCertificate());
        }

        vo.parameters = parameters;
        Invoker.getInstance().setOnEchoResultCallback(this.callback);
        Invoker.getInstance().exec(vo);
    }

    /**
     * 上传图片
     */
    private void uploadPhoto() {
        new ImageUpLoadClass("https://file.scdawn.com/CashierImg/upload", this.remittance.getVouchers(),
                new ImageUpLoadClass.OnImageUploadFinishCallBack() {
                    @Override
                    public void uploadFinish(List<String> uploadResult) {
                        StringBuilder builder = new StringBuilder();
                        for (String c : uploadResult) {
                            builder.append(c).append(",");
                        }
                        String certificate = builder.toString();
                        remittance.setCertificate(certificate.substring(0, certificate.length() - 1));
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
}
