package com.realstate.habitar.domain.rules;

import com.realstate.habitar.domain.dtos.funnelsDtos.sales.DetailPaymentFunnel;
import com.realstate.habitar.domain.dtos.funnelsDtos.sales.DetailResumeAbstract;
import com.realstate.habitar.domain.dtos.funnelsDtos.sales.DetailUserPayment;


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.function.BiFunction;



public class ExportRules {
    public static <T,R> void exportLiquidationReport(List<DetailResumeAbstract> detailResumeAbstract,
                                                     String[] header,
                                                     String path,
                                                     BiFunction<T,List<DetailResumeAbstract>,Void> function) throws IOException {
        try(BufferedWriter writer= new BufferedWriter(new FileWriter(path))) {
            String linesHeader = String.join(",",header);
            writer.write(linesHeader);
            writer.newLine();
            function.apply((T) writer,detailResumeAbstract);
        } catch (IOException e) {
            throw e;
        }
    }
}
