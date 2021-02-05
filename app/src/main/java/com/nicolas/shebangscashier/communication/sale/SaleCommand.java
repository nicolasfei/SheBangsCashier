package com.nicolas.shebangscashier.communication.sale;

import com.nicolas.shebangscashier.communication.Command;
import com.nicolas.shebangscashier.communication.CommandTypeEnum;
import com.nicolas.shebangscashier.communication.CommandVo;

public class SaleCommand extends Command {
    @Override
    public String execute(CommandVo vo) {
        return super.firstNode.echo(vo);
    }

    @Override
    protected void buildDutyChain() {
        SaleInterface saleQueryCode = new SaleQueryCode();
        SaleInterface saleGoodsSale = new SaleGoodsSale();
        SaleInterface saleReceipt = new SaleReceipt();
        SaleInterface saleByEverDay = new SaleByEverDay();
        SaleInterface saleQuery = new SaleQuery();
        SaleInterface saleStatistics = new SaleStatistics();
        SaleInterface barCodeBack = new SaleStatistics();
        SaleInterface barCodeBackById = new SaleStatistics();
        SaleInterface integralActivityBack = new SaleStatistics();
        SaleInterface remittanceEdit = new RemittanceEdit();
        SaleInterface remittance = new Remittance();
        SaleInterface remittanceById = new RemittanceById();

        saleQueryCode.setNextHandler(saleGoodsSale);
        saleGoodsSale.setNextHandler(saleReceipt);
        saleReceipt.setNextHandler(saleByEverDay);
        saleByEverDay.setNextHandler(saleQuery);
        saleQuery.setNextHandler(saleStatistics);
        saleStatistics.setNextHandler(barCodeBack);
        barCodeBack.setNextHandler(barCodeBackById);
        barCodeBackById.setNextHandler(integralActivityBack);
        integralActivityBack.setNextHandler(remittanceEdit);
        remittanceEdit.setNextHandler(remittance);
        remittance.setNextHandler(remittanceById);
        remittanceById.setNextHandler(null);

        super.firstNode = saleQueryCode;
    }

    @Override
    public CommandTypeEnum getCommandType() {
        return CommandTypeEnum.COMMAND_SALE;
    }
}
