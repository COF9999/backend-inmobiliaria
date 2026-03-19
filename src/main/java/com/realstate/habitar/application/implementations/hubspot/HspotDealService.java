package com.realstate.habitar.application.implementations.hubspot;
import com.realstate.habitar.domain.dispactchers.FunnelSalesType;
import com.realstate.habitar.domain.dispactchers.PipelineHandler;
import com.realstate.habitar.domain.dispactchers.PipelineType;
import com.realstate.habitar.domain.dtos.hubspot.HubSpotDealKeyRecord;
import com.realstate.habitar.domain.dtos.hubspot.HubspotDealDtoApp;
import com.realstate.habitar.domain.dtos.sales.LiquidationTimeRecord;
import com.realstate.habitar.domain.hubspot.ObjectHubSpot;
import com.realstate.habitar.domain.hubspot.Operators;
import com.realstate.habitar.domain.interfaces.Execute;
import com.realstate.habitar.domain.rules.CronoRules;
import com.realstate.habitar.infraestructure.adapters.interfaces.hubspot.HspotClientRepository;
import com.realstate.habitar.infraestructure.adapters.interfaces.processDeal.ProcessedDealRepository;
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
    public void liquidateOnlyDeal(HubSpotDealKeyRecord hubSpotDealKeyRecord){
        HubspotDealDtoApp hubspotDealDtoApp = hubspotClientRepository.findDealByPrincipalInfo(hubSpotDealKeyRecord);

        boolean isUnique = findNotIsProcessed(hubspotDealDtoApp.id()+"-"+hubspotDealDtoApp.ownerId());

        if (isUnique == false ){
            throw new IllegalArgumentException("Deal ya esta procesado");
        }

        System.out.println("ONLYDEAL");

        List<HubspotDealDtoApp> listDeals = new ArrayList<>();
        listDeals.add(hubspotDealDtoApp);
        Map<String,Map<String,List<HubspotDealDtoApp>>> grouped = grouped(listDeals);

        System.out.println(grouped.toString());

        grouped.forEach((pipeline, listDealsByOwner) -> {
            FunnelSalesType funnelSalesType = new FunnelSalesType("DEAL");
            processCloseDeal(pipeline, listDealsByOwner, funnelSalesType, (p, list, extra) -> {
                PipelineHandler handler = getPipelineHandler(p);
                System.out.println(handler.pipelineKey());
                handler.handle(listDealsByOwner,extra);
            });
        });
    }

    @Transactional
    public void liquidationReportByMonth(LiquidationTimeRecord liquidationMonthRecord){

        LiquidationTimeRecord timeRecord = CronoRules.getMileSecondsTime(liquidationMonthRecord);

        List<HubspotDealDtoApp> listDeals= getDealsHubspot(timeRecord);

        Map<String,Map<String,List<HubspotDealDtoApp>>> grouped = grouped(listDeals);

        /*
        grouped.forEach((pipeline, listDealsByOwner) -> {
            callGenerateReport(pipeline,listDealsByOwner);
        });

         */
    }

    @Transactional
    public List<HubspotDealDtoApp> getDealsHubspot(LiquidationTimeRecord liquidationTimeRecord){

        List<HubspotDealDtoApp> list =  Arrays
                .stream(PipelineType.values())
                .map(p-> getDataFromHubSpot(p,liquidationTimeRecord))
                .flatMap(List::stream)
                .filter(deal-> findNotIsProcessed(deal.id()+"-"+deal.ownerId()))
                .toList();

        list.forEach(System.out::println);

        return list;
    }



    private List<HubspotDealDtoApp> getDataFromHubSpot(PipelineType pipelineType, LiquidationTimeRecord liquidationTimeRecord){
        try {
            LiquidationTimeRecord timeRecord = CronoRules.getMileSecondsTime(liquidationTimeRecord);
            List<Map<String,Object>> filters = buildStuctureConsultHubSpot(true,pipelineType,timeRecord,null);
            return hubspotClientRepository.findDeals(filters);
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

    /*
    private void callGenerateReport(String pipelineType,Map<String,List<HubspotDealDtoApp>> dealsByOwner){
        PipelineHandler handler = getPipelineHandler(pipelineType);
        handler
                .generateReport(dealsByOwner);
    }


     */
    private <T1,T2,T3 >void processCloseDeal(T1 t1,T2 t2,T3 t3,Execute<T1,T2,T3> action){
            action.execute(t1,t2,t3);
    }



    private PipelineHandler getPipelineHandler(String pipelineKey){
        return handlerFactory.getHandler(pipelineKey);
    }

    private boolean findNotIsProcessed(String key){
        return processedDealRepository.notIsProcessed(key);
    }


}
