package com.realstate.habitar.application.components.helpers.sales;

import com.realstate.habitar.domain.baselogic.interfaces.VisitorExtractSales;
import com.realstate.habitar.domain.dtos.funnelsDtos.sales.DetailPaymentFunnel;
import com.realstate.habitar.domain.dtos.funnelsDtos.sales.DetailUserPayment;
import com.realstate.habitar.domain.dtos.funnelsDtos.sales.Resume;

public class SalesUtilMethods implements VisitorExtractSales<String> {
    @Override
    public String extract(DetailPaymentFunnel detailPaymentFunnel) {
        DetailUserPayment agentPayment= detailPaymentFunnel.getDetailAgentPayment();
        DetailUserPayment ownerPayment =detailPaymentFunnel.getDetailOwnerPayment();
        if (agentPayment==null){
            return printOwnerReport(ownerPayment);
        }else {
            String rowPart1 = printAgentReport(agentPayment);
            String rowPart2 = printOwnerReport(ownerPayment);
            return rowPart1 + rowPart2;
        }
    }

    @Override
    public String extract(Resume resume) {
        return printResumeReport(resume);
    }

    private String printOwnerReport(DetailUserPayment detailUserPayment){
        return templateToPrint(detailUserPayment);

    }

    private String printAgentReport(DetailUserPayment detailUserPayment){
        return templateToPrint(detailUserPayment);
    }

    private String printResumeReport(Resume resume){
        return  resume.getHubId()+resume.getTotalAmount();
    }

    private String templateToPrint(DetailUserPayment detailUserPayment){
        return
                detailUserPayment.nameUser()+","+
                        detailUserPayment.hubId()+","+
                        detailUserPayment.amountToPay().toString()+","+
                        detailUserPayment.commisionPercentage()+","+
                        detailUserPayment.operationPercentage()+",";
    }
}
