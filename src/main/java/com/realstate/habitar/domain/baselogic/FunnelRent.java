package com.realstate.habitar.domain.baselogic;
import com.realstate.habitar.domain.enums.FunnelRentEnum;
import com.realstate.habitar.domain.rules.CronoRules;

import java.time.LocalDateTime;
import java.util.Map;


public class FunnelRent{


    public record FunnelRentDecitions(String candidateMixId, int contractDuration, Map<String,String> map){}

    public static FunnelRentDecitions instanceFunnelRentDecitions(String candidateMixId, int contractDuration, Map<String,String> mapRent){
        return new FunnelRentDecitions(candidateMixId,contractDuration,mapRent);
    }
    public boolean isApportionment(LocalDateTime baseDate,int dayApportionment){
        return baseDate.getDayOfMonth() > dayApportionment;
    }
    public static boolean checkIsFullMonth(LocalDateTime start,LocalDateTime end) {
        // 1. Siempre debe empezar el día 1


        if (start.getDayOfMonth() != 1) return false;

        boolean endsAt30 = end.getDayOfMonth() == 30;
        boolean endsAt31 = end.getDayOfMonth() == 31;
        boolean isLastDayFeb = (start.getMonthValue() == 2 && end.plusDays(1).getMonthValue() == 3);

        return endsAt30 || endsAt31 || isLastDayFeb;
    }

    public static class Apportionment{
        public static boolean getAppointMentDecition(int iteration, FunnelRentEnum.TypeUsers.TypeAgentRecord typeAgentRecord){
            if (typeAgentRecord.isAppointment()){
                if (iteration<=typeAgentRecord.commission()){
                    return true;
                }
                return false;
            }else {
                if (iteration<typeAgentRecord.commission()){
                    return true;
                }
                return false;
            }
        }

        public static long restIsFullMonth(LocalDateTime baseDate,LocalDateTime endCurrentDate){
            long restDays;
            if (checkIsFullMonth(baseDate,endCurrentDate)){
                restDays = 30;
            }else {
                restDays = CronoRules.differenceBeetweenDates(baseDate,endCurrentDate);
            }
            return restDays;
        }
    }
}
