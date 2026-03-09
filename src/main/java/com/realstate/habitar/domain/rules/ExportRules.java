package com.realstate.habitar.domain.rules;

import com.realstate.habitar.domain.dtos.sales.LiquidationMonthReportRecord;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class ExportRules {
    public static void exportLiquidationReport(List<LiquidationMonthReportRecord> monthReportRecordList,String path) throws IOException {
        try(BufferedWriter writer= new BufferedWriter(new FileWriter(path))) {

            writer.write("name-user,hub-id,amount");
            writer.newLine();

            for (LiquidationMonthReportRecord record:monthReportRecordList){
                String line = String.format("%s,%s,%s",
                        record.nameUser(),
                        record.hubId(),
                        record.amount().toString()
                );
                writer.write(line);
                writer.newLine();
            }

        } catch (IOException e) {
            throw e;
        }
    }
}
