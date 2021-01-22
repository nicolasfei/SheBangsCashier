package com.nicolas.shebangscashier.communication.common;

import com.nicolas.shebangscashier.communication.Command;
import com.nicolas.shebangscashier.communication.CommandTypeEnum;
import com.nicolas.shebangscashier.communication.CommandVo;

public class CommonCommand extends Command {
    @Override
    public String execute(CommandVo vo) {
        return super.firstNode.handleMessage(vo);
    }

    @Override
    protected void buildDutyChain() {
        CommonInterface goodsClass = new GoodsClass();
        CommonInterface versionCheck = new VersionCheck();
        CommonInterface noticeCheck = new NoticeCheck();

        goodsClass.setNextHandler(versionCheck);
        versionCheck.setNextHandler(noticeCheck);
        noticeCheck.setNextHandler(null);

        super.firstNode = goodsClass;
    }

    @Override
    public CommandTypeEnum getCommandType() {
        return CommandTypeEnum.COMMAND_COMMON;
    }
}
