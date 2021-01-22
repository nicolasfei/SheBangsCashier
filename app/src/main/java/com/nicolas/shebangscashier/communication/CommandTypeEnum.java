package com.nicolas.shebangscashier.communication;

/**
 * 请求分类--大类
 */
public enum CommandTypeEnum {
    COMMAND_LOGIN,                  //登陆
    COMMAND_SALE,                   //售卖
    COMMAND_VIP,                    //VIP
    COMMAND_REFUNDS,
    COMMAND_COMMON,               //通用功能
    ;                //退货
}
