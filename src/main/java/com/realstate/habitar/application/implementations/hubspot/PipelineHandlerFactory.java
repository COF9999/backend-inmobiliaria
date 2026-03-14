package com.realstate.habitar.application.implementations.hubspot;
import com.realstate.habitar.domain.dispactchers.PipelineHandler;
import com.realstate.habitar.domain.dispactchers.PipelineType;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class PipelineHandlerFactory {

    private final Map<PipelineType, PipelineHandler> handlers;

    public PipelineHandlerFactory(List<PipelineHandler> handlerList){
        this.handlers = handlerList
                .stream()
                .collect(Collectors.toMap(
                        handler-> handler.pipelineKey(),
                       handler -> handler
                ));
    }

    public PipelineHandler getHandler(String pipelineId){
        PipelineType type = PipelineType.findInternalValuePipeline(pipelineId);
        return handlers.get(type);
    }
}
