package com.nicolas.shebangscashier.communication.common;


import com.nicolas.shebangscashier.communication.AbstractInterface;

public abstract class CommonInterface extends AbstractInterface {
    //查询货物类别
    public final static String GoodsClass = AbstractInterface.COMMAND_URL + "Cashier/GoodsClass";

    //版本监测
    public final static String VersionCheck = "http://updatesupplier.scdawn.com/cv.json";
    //广播通知
    public final static String NoticeCheck = "http://updatesupplier.scdawn.com/cn.json";

}
