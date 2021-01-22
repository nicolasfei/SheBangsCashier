package com.nicolas.shebangscashier.communication.sale;

import com.nicolas.shebangscashier.communication.AbstractInterface;

public abstract class SaleInterface extends AbstractInterface {
    //商品售卖--条码查询
    public final static String SaleQueryCode = AbstractInterface.COMMAND_URL + "Cashier/BarCode";
    //商品售卖--销售
    public final static String SaleGoodsSale = AbstractInterface.COMMAND_URL + "Cashier/BarCodeSale";
    //商品售卖--小票查询
    public final static String SaleReceipt = AbstractInterface.COMMAND_URL + "Cashier/SaleByBarCode";

    //每日销售统计
    public final static String SaleByEverDay = AbstractInterface.COMMAND_URL + "Cashier/SaleByEverDay";
    //销售查询
    public final static String SaleQuery = AbstractInterface.COMMAND_URL + "Cashier/Sale";
    //销售统计
    public final static String SaleStatistics = AbstractInterface.COMMAND_URL + "Cashier/SaleStatistics";
}
