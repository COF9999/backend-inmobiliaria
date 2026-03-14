package com.realstate.habitar.domain.dispactchers;

public class FunnelSalesType extends TypeMethodFunnel {
    private final String LIQUIDATEDEALS = "LIQUIDATE-DEALS";
    private final String LIQUIDATEONLYDEAL = "DEAL";
    private final String GENERATEREPORT = "GENERATE-REPORT";

    public FunnelSalesType(String targetValue){
        super(targetValue);
    }

    public String getLIQUIDATEDEALS() {
        return LIQUIDATEDEALS;
    }

    public String getLIQUIDATEONLYDEAL() {
        return LIQUIDATEONLYDEAL;
    }

    public String getGENERATEREPORT() {
        return GENERATEREPORT;
    }
}
