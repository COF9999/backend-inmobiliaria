package com.realstate.habitar.application.implementations.hubspot;
import com.realstate.habitar.domain.PipelineHandler;
import com.realstate.habitar.domain.PipelineType;
import com.realstate.habitar.domain.dtos.hubspot.HubspotDealDtoApp;
import com.realstate.habitar.domain.dtos.sales.LiquidationTimeRecord;
import com.realstate.habitar.domain.hubspot.ObjectHubSpot;
import com.realstate.habitar.domain.hubspot.Operators;
import com.realstate.habitar.domain.rules.CronoRules;
import com.realstate.habitar.infraestructure.adapters.interfaces.HspotClientRepository;
import com.realstate.habitar.infraestructure.adapters.interfaces.ProcessedDealRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class HspotDealService {
    private final PipelineHandlerFactory handlerFactory;

    private final HspotClientRepository hubspotClientRepository;

    private final ProcessedDealRepository processedDealRepository;

    public HspotDealService(PipelineHandlerFactory handlerFactory,
                            HspotClientRepository hubspotClientRepository,
                            ProcessedDealRepository processedDealRepository){
        this.handlerFactory = handlerFactory;
        this.hubspotClientRepository = hubspotClientRepository;
        this.processedDealRepository = processedDealRepository;
    }


    @Transactional
    public void liquidateSelectDeals(List<HubspotDealDtoApp> listDeals){
        Map<String,Map<String,List<HubspotDealDtoApp>>> grouped = grouped(listDeals);

        grouped.forEach((pipeline, listDealsByOwner) -> {
            processCloseDeal(pipeline,listDealsByOwner);
        });
    }


    @Transactional
    public void processDealTest(List<HubspotDealDtoApp> listDeals){
        Map<String,Map<String,List<HubspotDealDtoApp>>> grouped = grouped(listDeals);

        grouped.forEach((pipeline, listDealsByOwner) -> {
            processCloseDeal(pipeline,listDealsByOwner);
        });
    }

    @Transactional
    public void liquidationReportByMonth(LiquidationTimeRecord liquidationMonthRecord){

        LiquidationTimeRecord timeRecord = CronoRules.getMileSecondsTime(liquidationMonthRecord);

        List<HubspotDealDtoApp> listDeals= getDealsHubspot(timeRecord);

        Map<String,Map<String,List<HubspotDealDtoApp>>> grouped = grouped(listDeals);

        grouped.forEach((pipeline, listDealsByOwner) -> {
            callGenerateReport(pipeline,listDealsByOwner);
        });
    }

    @Transactional
    public List<HubspotDealDtoApp> getDealsHubspot(LiquidationTimeRecord liquidationTimeRecord){

        return Arrays
                .stream(PipelineType.values())
                .map(p-> getDataFromHubSpot(p,liquidationTimeRecord))
                .flatMap(List::stream)
                .filter(dealByPipeline-> findUniqueKey(dealByPipeline.id()+"-"+dealByPipeline.ownerId()))
                .toList();
    }

    public List<HubspotDealDtoApp> filterHubSpotFromUser(List<ObjectHubSpot> objectHubSpot){
        try {
            List<Map<String,Object>> filters =  buildStuctureConsultHubSpot(false,null,null,objectHubSpot);
            return hubspotClientRepository.findDeal(filters);
        }catch (Exception e){
            throw new UnsupportedOperationException(e.getMessage());
        }
    }

    private List<HubspotDealDtoApp> getDataFromHubSpot(PipelineType pipelineType, LiquidationTimeRecord liquidationTimeRecord){

        System.out.println(liquidationTimeRecord);
        try {
            LiquidationTimeRecord timeRecord = CronoRules.getMileSecondsTime(liquidationTimeRecord);
            System.out.println("DESPUES");
            List<Map<String,Object>> filters = buildStuctureConsultHubSpot(true,pipelineType,timeRecord,null);
            return hubspotClientRepository.findDeal(filters);
        }catch (Exception e){
            throw new UnsupportedOperationException(e.getMessage());
        }
    }


    private List<Map<String,Object>> buildStuctureConsultHubSpot(boolean isDefault,
                                                                 PipelineType pipelineType,
                                                                 LiquidationTimeRecord liquidationTimeRecord,
                                                                 List<ObjectHubSpot> objectHubSpotList){
        if (isDefault){
            return List.of(
                    Map.of(
                            "propertyName", "pipeline",
                            "operator", "EQ",
                            "value", pipelineType.getPipelineKey()
                    ),
                    Map.of(
                            "propertyName", "dealstage",
                            "operator", "IN",
                            "values", pipelineType.getDealStages()
                    ),
                    Map.of(
                            "propertyName", "closedate",
                            "operator", "BETWEEN",
                            "value", String.valueOf(liquidationTimeRecord.milesecondsStartDay()),
                            "highValue", String.valueOf(liquidationTimeRecord.milesecondsEndDay())
                    )
            );
        }else {
            List<Map<String,Object>> mapListQuerys = new ArrayList<>();
            for (ObjectHubSpot objHub:objectHubSpotList){
                String propertyName = objHub.getMapProperties().get("propertyName");
                String operator = objHub.getMapProperties().get("operator");
                String value = objHub.getMapProperties().get("value");
                String type = objHub.getMapProperties().get("type");
                Map<String,Object> singleQuery = Operators.build(propertyName,operator,value);
                mapListQuerys.add(singleQuery);
            }
            return mapListQuerys;
        }
    }

    private Map<String,Map<String,List<HubspotDealDtoApp>>> grouped(List<HubspotDealDtoApp> listDeals){
       return listDeals.stream()
                .collect(Collectors.groupingBy(
                        HubspotDealDtoApp::pipelineType,
                        Collectors.groupingBy(HubspotDealDtoApp::ownerId)
                ));
    }

    private void callGenerateReport(String pipelineType,Map<String,List<HubspotDealDtoApp>> dealsByOwner){
        PipelineHandler handler = getPipelineHandler(pipelineType);
        handler.generateReport(dealsByOwner);
    }

    private void processCloseDeal(String pipelineType,Map<String,List<HubspotDealDtoApp>> dealsByOwner ){
        PipelineHandler handler = getPipelineHandler(pipelineType);
        handler.handle(dealsByOwner);
    }

    private PipelineHandler getPipelineHandler(String pipelineKey){
        return handlerFactory.getHandler(pipelineKey);
    }

    private boolean findUniqueKey(String key){
        return processedDealRepository.uniqueKey(key);
    }


}
