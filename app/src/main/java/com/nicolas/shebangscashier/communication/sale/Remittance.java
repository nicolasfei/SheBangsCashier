package com.nicolas.shebangscashier.communication.sale;

public class Remittance extends SaleInterface {
    @Override
    public String getUrlParam() {
        return SaleInterface.Remittance;
    }
}
