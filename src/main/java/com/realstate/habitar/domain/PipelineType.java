package com.realstate.habitar.domain;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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

    private static final Map<String, PipelineType> mapPipelineType = new HashMap<>();

    public String getPipelineKey(){
        return this.pipelineKey;
    }

    public List<String> getDealStages(){
        return this.dealStages;
    }

    public static PipelineType findInternalValuePipeline(String value){
        return Arrays.stream(values())
                .filter(k-> k.pipelineKey.equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(()->
                        new IllegalArgumentException("Pipeline not found")
                );
    }


}
