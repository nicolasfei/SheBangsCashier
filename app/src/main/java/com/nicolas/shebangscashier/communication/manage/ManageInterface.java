package com.nicolas.shebangscashier.communication.manage;

import com.nicolas.shebangscashier.communication.AbstractInterface;

public abstract class ManageInterface extends AbstractInterface {
    //订单查询(待收货和已收货)
    public final static String GoodsOrder = AbstractInterface.COMMAND_URL + "Cashier/GoodsOrder";
    //分店收货
    public final static String InStock = AbstractInterface.COMMAND_URL + "Cashier/InStock";
}
