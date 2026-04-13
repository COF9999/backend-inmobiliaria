package com.realstate.habitar.domain.dispactchers;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public enum PipelineType {
    FUNNEL_RENT("80031012","Funnel Rent",List.of("151455346")),
    FUNNELSALES("default","Funnel Sales",List.of("closedwon")),
    FUNNELRENTPLACEMENT("667146145","Funnel Rent Placement",List.of("978989448"));
   // FUNNEL_TEST("800",List.of("1"));

    private final String pipelineKey;
    private final String fieldName;
    private final List<String> dealStages;

    PipelineType(String pipelineKey,String fieldName,List<String> dealStages){
        this.pipelineKey = pipelineKey;
        this.fieldName = fieldName;
        this.dealStages = dealStages;
    }

    private static final Map<String, String> LOOKUP_MAP;

    private static final Map<String,String> INVERSE_LOOKUP;

    private static final Map<String,PipelineType> pipelineTypeMap;

    static {
        LOOKUP_MAP = Arrays.stream(values())
                .collect(Collectors.toMap(PipelineType::getPipelineKey, PipelineType::getFieldName));

        INVERSE_LOOKUP = Arrays.stream(values())
                .collect(Collectors.toMap(PipelineType::getFieldName, PipelineType::getPipelineKey));

        pipelineTypeMap = Arrays.stream(values())
                .collect(Collectors.toMap(PipelineType::getFieldName, p->p));
    }


    public static String getFieldNameByMap(String pipelineKey){
        return LOOKUP_MAP.get(pipelineKey);
    }

    public static String pipelineKeyByFieldName(String fieldName){
        return INVERSE_LOOKUP.get(fieldName);
    }

    public String getPipelineKey(){
        return this.pipelineKey;
    }

    public String getFieldName(){
        return this.fieldName;
    }

    public List<String> getDealStages(){
        return this.dealStages;
    }

    public static PipelineType findInternalValuePipeline(String value){
        return pipelineTypeMap.get(value);
    }


}
