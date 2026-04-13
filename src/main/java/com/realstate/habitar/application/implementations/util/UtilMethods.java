package com.realstate.habitar.application.implementations.util;

public class UtilMethods {
    public static String extractCorrectDealId(String anyMixId){
        int index = anyMixId.indexOf("-");
        return index!=-1? anyMixId.substring(0,index): anyMixId;
    }
}
