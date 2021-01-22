package com.nicolas.shebangscashier.communication.vip;

import com.nicolas.shebangscashier.communication.Command;
import com.nicolas.shebangscashier.communication.CommandTypeEnum;
import com.nicolas.shebangscashier.communication.CommandVo;

public class VipCommand extends Command {
    @Override
    public String execute(CommandVo vo) {
        return super.firstNode.echo(vo);
    }

    @Override
    protected void buildDutyChain() {
        VipInterface vipInformationQuery = new VipInformationQuery();
        VipInterface integralActivity = new IntegralActivity();
        VipInterface integralActivityBack = new IntegralActivityBack();
        VipInterface integralCheck = new IntegralCheck();

        vipInformationQuery.setNextHandler(integralActivity);
        integralActivity.setNextHandler(integralActivityBack);
        integralActivityBack.setNextHandler(integralCheck);
        integralCheck.setNextHandler(null);

        super.firstNode = vipInformationQuery;
    }

    @Override
    public CommandTypeEnum getCommandType() {
        return CommandTypeEnum.COMMAND_VIP;
    }
}
