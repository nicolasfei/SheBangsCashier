package com.nicolas.shebangscashier.communication.sale;

import com.nicolas.shebangscashier.communication.AbstractInterface;

public abstract class SaleInterface extends AbstractInterface {
    //商品售卖--条码查询
    public final static String SaleQueryCode = AbstractInterface.COMMAND_URL + "Cashier/BarCode";
    //商品售卖--销售
    public final static String SaleGoodsSale = AbstractInterface.COMMAND_URL + "Cashier/BarCodeSale";
    //商品售卖--小票查询
    public final static String SaleReceipt = AbstractInterface.MANAGE_URL + "Goods/SaleByBarCode";

    //每日销售统计
    public final static String SaleByEverDay = AbstractInterface.MANAGE_URL + "Goods/SaleByEverDay";
    //销售查询
    public final static String SaleQuery = AbstractInterface.MANAGE_URL + "Goods/Sale";
    //销售统计
    public final static String SaleStatistics = AbstractInterface.MANAGE_URL + "Goods/SaleStatistics";

    //退货查询
    public final static String BarCodeBackById = AbstractInterface.COMMAND_URL + "Cashier/BarCodeBackById";
    //退货
    public final static String BarCodeBack = AbstractInterface.COMMAND_URL + "Cashier/BarCodeBack";
    //积分抵扣金额
    public final static String IntegralActivityBack = AbstractInterface.COMMAND_URL + "Cashier/IntegralActivityBack";

    //每日汇款登记
    public final static String RemittanceEdit = AbstractInterface.COMMAND_URL + "Cashier/RemittanceEdit";
    //每日汇款登记查询
    public final static String Remittance = AbstractInterface.COMMAND_URL + "Cashier/Remittance";
    //汇款登记单个查询
    public final static String RemittanceById = AbstractInterface.COMMAND_URL + "Cashier/RemittanceById";
}
