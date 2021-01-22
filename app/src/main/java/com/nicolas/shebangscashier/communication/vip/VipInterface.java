package com.nicolas.shebangscashier.communication.vip;

import com.nicolas.shebangscashier.communication.AbstractInterface;

public abstract class VipInterface extends AbstractInterface {
    //会员信息查询
    public final static String VipInformationQuery = AbstractInterface.COMMAND_URL + "Cashier/Member";
    //积分活动查询
    public final static String IntegralActivity = AbstractInterface.COMMAND_URL + "Cashier/IntegralActivity";
    //积分规则
    public final static String IntegralCheck = AbstractInterface.COMMAND_URL + "Cashier/IntegralCheck";
    //积分抵扣金额
    public final static String IntegralActivityBack = AbstractInterface.COMMAND_URL + "Cashier/IntegralActivityBack";
}
