package com.realstate.habitar.application.components.helpers.sales;

import com.realstate.habitar.domain.dtos.funnelsDtos.sales.DetailResumeAbstract;
import com.realstate.habitar.domain.dtos.funnelsDtos.sales.Resume;
import com.realstate.habitar.domain.rules.ExportRules;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;
import java.util.function.BiFunction;

@Service
public class SalesAsync {

    @Async
    public void WriteInFile(String pathToExport,List<DetailResumeAbstract> detailResumeAbstractList) throws IOException {

        BiFunction<BufferedWriter, List<DetailResumeAbstract>,Void> function = (writer, listDetailPayment)->{
            SalesUtilMethods salesUtilMethods = new SalesUtilMethods();
            try {
                for (DetailResumeAbstract record:listDetailPayment){

                    if (record instanceof Resume resume){
                        writer.write(String.format("%s",
                                "Nombre-Agente","Hub-id","total-recaudo"
                        ));
                        writer.write(record.report(salesUtilMethods));
                        writer.newLine();
                    }else {
                        String row = String.format("%s",
                                record.report(salesUtilMethods)
                        );
                        writer.write(row);
                        writer.newLine();
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            return null;
        };

        String[] header = {"Dueño-nombre","Hub-id","Monto","A1","A2","Agente-Nombre","Hub-id","Monto","A1","A2"};
        ExportRules.exportLiquidationReport(detailResumeAbstractList,
                header
                ,pathToExport
                ,function);
    }
}
