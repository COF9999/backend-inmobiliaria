package com.realstate.habitar.domain;

import java.util.Arrays;
import java.util.List;


public enum PipelineType {
    FUNNEL_RENT("80031012",List.of("151455346")),
    FUNNELSALES("default",List.of("closedwon")),
    FUNNELRENTPLACEMENT("667146145",List.of("978989448"));
   // FUNNEL_TEST("800",List.of("1"));

    private final String pipelineKey;
    private final List<String> dealStages;

    PipelineType(String pipelineKey,List<String> dealStages){
        this.pipelineKey = pipelineKey;
        this.dealStages = dealStages;
    }

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
