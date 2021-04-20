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
        ManageInterface goodsIdById = new GoodsIdById();

        //盘点
        ManageInterface barCodeSearch = new PDBarCodeSearch();
        ManageInterface barCodeStatistics = new PDBarCodeStatistics();
        ManageInterface inventoryById = new PDInventoryById();
        ManageInterface inventoryClear = new PDInventoryClear();
        ManageInterface inventoryUp = new PDInventoryUp();

        //调货
        ManageInterface transferGoodsSearchFrom = new TransferGoodsSearchFrom();
        ManageInterface transferGoodsBarCodeById = new TransferGoodsBarCodeById();
        ManageInterface transferGoodsOK = new TransferGoodsOK();
        ManageInterface transferGoodsCancel = new TransferGoodsCancel();
        ManageInterface transferGoodsListOK = new TransferGoodsListOK();
        ManageInterface transferGoodsTo = new TransferGoodsTo();
        ManageInterface transferGoodsSearchTo = new TransferGoodsSearchTo();

        //补货
        ManageInterface goodsByGoodsId = new GoodsByGoodsId();
        ManageInterface orderByBranch = new OrderByBranch();
        //收货打印条码
        ManageInterface barCodeByOrderId = new BarCodeByOrderId();
        //返货打印条码--查询
        ManageInterface backGoodsPrint = new BackGoodsPrint();
        //返货打印条码--查询
        ManageInterface backGoodsPrintOK = new BackGoodsPrintOK();

        //质检
        ManageInterface qualityClass = new QualityClass();
        ManageInterface qualityAdd = new QualityAdd();
        ManageInterface quality = new Quality();
        ManageInterface qualityById = new QualityById();
        ManageInterface qualityDel = new QualityDel();
        ManageInterface qualityByBarCodeId = new QualityByBarCodeId();

        goodsOrder.setNextHandler(inStock);
        inStock.setNextHandler(barCodeSearch);
        //盘点
        barCodeSearch.setNextHandler(barCodeStatistics);
        barCodeStatistics.setNextHandler(inventoryById);
        inventoryById.setNextHandler(inventoryClear);
        inventoryClear.setNextHandler(inventoryUp);
        inventoryUp.setNextHandler(transferGoodsSearchFrom);
        //调货
        transferGoodsSearchFrom.setNextHandler(transferGoodsBarCodeById);
        transferGoodsBarCodeById.setNextHandler(transferGoodsOK);
        transferGoodsOK.setNextHandler(transferGoodsCancel);
        transferGoodsCancel.setNextHandler(transferGoodsListOK);
        transferGoodsListOK.setNextHandler(transferGoodsTo);
        transferGoodsTo.setNextHandler(transferGoodsSearchTo);
        transferGoodsTo.setNextHandler(goodsIdById);
        goodsIdById.setNextHandler(goodsByGoodsId);
        //补货
        goodsByGoodsId.setNextHandler(orderByBranch);
        orderByBranch.setNextHandler(barCodeByOrderId);
        //收货打条码
        barCodeByOrderId.setNextHandler(backGoodsPrint);
        //返货打条码
        backGoodsPrint.setNextHandler(backGoodsPrintOK);
        backGoodsPrintOK.setNextHandler(qualityClass);

        //质检
        qualityClass.setNextHandler(qualityAdd);
        qualityAdd.setNextHandler(quality);
        quality.setNextHandler(qualityById);
        qualityById.setNextHandler(qualityDel);
        qualityDel.setNextHandler(qualityByBarCodeId);
        qualityByBarCodeId.setNextHandler(null);

        super.firstNode = goodsOrder;
    }

    @Override
    public CommandTypeEnum getCommandType() {
        return CommandTypeEnum.COMMAND_MANAGE;
    }
}
