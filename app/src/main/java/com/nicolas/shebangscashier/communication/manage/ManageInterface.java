package com.nicolas.shebangscashier.communication.manage;

import com.nicolas.shebangscashier.communication.AbstractInterface;

public abstract class ManageInterface extends AbstractInterface {
    //订单查询(待收货和已收货)
    public final static String GoodsOrder = AbstractInterface.MANAGE_URL + "Goods/GoodsOrder";
    //分店收货
    public final static String InStock = AbstractInterface.MANAGE_URL + "Goods/InStock";
    //货号查询(搭配方案)
    public final static String GoodsIdById = AbstractInterface.MANAGE_URL + "Goods/GoodsIdById";
    //分店盘点--搜索
    public final static String PDBarCodeSearch = AbstractInterface.MANAGE_URL + "PD/BarCodeSearch";
    //分店盘点--查询
    public final static String PDInventoryById = AbstractInterface.MANAGE_URL + "PD/InventoryById";
    //分店盘点--提交
    public final static String PDInventoryUp = AbstractInterface.MANAGE_URL + "PD/InventoryUp";
    //分店盘点--统计
    public final static String PDBarCodeStatistics = AbstractInterface.MANAGE_URL + "PD/BarCodeStatistics";
    //分店盘点--清空
    public final static String PDInventoryClear = AbstractInterface.MANAGE_URL + "PD/InventoryClear";

    //调货查询（从）
    public final static String TransferGoodsSearchFrom = AbstractInterface.MANAGE_URL + "Goods/TransferGoodsSearchFrom";
    //调货--条码查询
    public final static String TransferGoodsBarCodeById = AbstractInterface.MANAGE_URL + "Goods/BarCodeById";
    //调货--调货确认
    public final static String TransferGoodsOK = AbstractInterface.MANAGE_URL + "Goods/TransferGoodsOK";
    //调货--调货取消
    public final static String TransferGoodsCancel = AbstractInterface.MANAGE_URL + "Goods/TransferGoodsCancel";
    //调货--调货确认（批量）
    public final static String TransferGoodsListOK = AbstractInterface.MANAGE_URL + "Goods/TransferGoodsListOK";
    //调货--发起调货
    public final static String TransferGoodsTo = AbstractInterface.MANAGE_URL + "Goods/TransferGoodsTo";
    //调货--调货查询(目标)
    public final static String TransferGoodsSearchTo = AbstractInterface.MANAGE_URL + "Goods/TransferGoodsSearchTo";

    //补货--查询
    public final static String GoodsByGoodsId = AbstractInterface.MANAGE_URL + "Goods/GoodsByGoodsId";
    //补货--下单
    public final static String OrderByBranch = AbstractInterface.MANAGE_URL + "Goods/OrderByBranch";

    //收货--打印条码
    public final static String BarCodeByOrderId = AbstractInterface.MANAGE_URL + "Goods/BarCodeByOrderId";

    //返货--打印条码--查询
    public final static String BackGoodsPrint = AbstractInterface.COMMAND_URL + "Cashier/BackGoodsPrint";
    //返货--打印条码--打印
    public final static String BackGoodsPrintOK = AbstractInterface.COMMAND_URL + "Cashier/BackGoodsPrintOK";

    //质检--质检类别
    public final static String QualityClass = AbstractInterface.MANAGE_URL + "Goods/QualityClass";
    //质检--质检提交
    public final static String QualityAdd = AbstractInterface.MANAGE_URL + "Goods/QualityAdd";
    //质检--质检列表查询
    public final static String Quality = AbstractInterface.MANAGE_URL + "Goods/Quality";
    //质检--质检记录获取
    public final static String QualityById = AbstractInterface.MANAGE_URL + "Goods/QualityById";
    //质检--质检记录删除
    public final static String QualityDel = AbstractInterface.MANAGE_URL + "Goods/QualityDel";
    //质检--条码查询
    public final static String QualityByBarCodeId = AbstractInterface.MANAGE_URL + "Goods/QualityByBarCodeId";
}
