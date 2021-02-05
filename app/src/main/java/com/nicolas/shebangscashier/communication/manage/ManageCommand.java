package com.nicolas.shebangscashier.communication.manage;

import com.nicolas.shebangscashier.communication.Command;
import com.nicolas.shebangscashier.communication.CommandTypeEnum;
import com.nicolas.shebangscashier.communication.CommandVo;

public class ManageCommand extends Command {
    @Override
    public String execute(CommandVo vo) {
        return super.firstNode.echo(vo);
    }

    @Override
    protected void buildDutyChain() {
        ManageInterface goodsOrder = new GoodsOrder();
        ManageInterface inStock = new InStock();

        goodsOrder.setNextHandler(inStock);
        inStock.setNextHandler(null);

        super.firstNode = goodsOrder;
    }

    @Override
    public CommandTypeEnum getCommandType() {
        return CommandTypeEnum.COMMAND_MANAGE;
    }
}
