package com.realstate.habitar.infraestructure.adapters.implementations;

import com.realstate.habitar.domain.dispactchers.PipelineType;
import com.realstate.habitar.domain.dtos.hubspot.HubSpotSearchResponse;
import com.realstate.habitar.domain.dtos.hubspot.HubspotDealDtoApp;
import com.realstate.habitar.domain.enums.HubspotEnum;
import com.realstate.habitar.infraestructure.adapters.interfaces.hubspot.HspotClientRepository;
import com.realstate.habitar.infraestructure.advicers.exceptions.ExternalServiceException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.reactive.resource.NoResourceFoundException;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;


@Repository
public class HubspotDealClient implements HspotClientRepository {

    private final WebClient hubspotWebClient;

    @Value("${hubspot.api.deals}")
    private String apiDeals;

    @Value("${hubspot.api.owners}")
    private String apiOwners;


    public HubspotDealClient(WebClient hubspotWebClient){
        this.hubspotWebClient = hubspotWebClient;
    }



    @Override
    public List<HubspotDealDtoApp> findDeals(List<Map<String,Object>> mapList) {
        System.out.println("**********************FIND-DEALS");
        Map<String, Object> requestBody = Map.of(
                "filterGroups", List.of(
                        Map.of(
                                "filters", mapList
                        )
                ),
                "properties", HubspotEnum.PropertiesHubspot.getListCommonProperties(),
                "limit", 100
        );
        try {
            return hubspotWebClient.post()
                    .uri(apiDeals)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(HubSpotSearchResponse.class)
                    .block()
                    .getResults()
                    .stream()
                    .peek(System.out::println)
                    .map(h-> mergeUser(new HubspotDealDtoApp(
                            h.id(),
                            h.properties().get("hubspot_owner_id"),
                            null,
                            PipelineType.getFieldNameByMap(h.properties().get("pipeline")),
                            h.properties())
                    ))
                    .toList();
        }catch (WebClientResponseException e) {
            String errorBody = e.getResponseBodyAsString();
            System.err.println("Cuerpo del error HubSpot: " + errorBody);
            throw new ExternalServiceException("Error HTTP desde HubSpot: " + errorBody);
        } catch (Exception e) {
            throw new ExternalServiceException(
                    "Error inesperado comunicándose con HubSpot "+e.getMessage());
        }
    }

    @Override
    public <T> HubspotDealDtoApp findDealByPrincipalInfo(Supplier<T> supplierDealId,Supplier<List<String>> supplierListProperties) {
        try {
            // Se usa el endpoint: /crm/v3/objects/deals/{dealId}
            HubspotDealDtoApp response = hubspotWebClient.get()
                    .uri(uriBuilder -> {
                        uriBuilder.path("/crm/v3/objects/deals/{dealId}");
                        // Agregamos cada propiedad individualmente para que la URL sea perfecta
                        for (String property : supplierListProperties.get()) {
                            uriBuilder.queryParam("properties", property);
                        }
                        return uriBuilder.build(supplierDealId.get());
                    })
                    .retrieve()
                    .bodyToMono(HubspotDealDtoApp.class)
                    .block();

            if (response == null) {
                throw new NoResourceFoundException("Elemento negocio no encontrado");
            }

            System.out.println("#####################");
            System.out.println(response);



            HubspotDealDtoApp hubspotDealDtoApp = mergeUser(new HubspotDealDtoApp(
                    response.id(),
                    response.properties().get(HubspotEnum.PropertiesHubspot.HUBSPOT_OWNER_ID.getValue()),
                    null,
                    PipelineType.getFieldNameByMap(response.properties().get(HubspotEnum.PropertiesHubspot.PIPELINE.getValue())),
                    response.properties()
            ));
            if (hubspotDealDtoApp==null){
                throw new IllegalArgumentException("ERROR");
            }
            System.out.println(hubspotDealDtoApp);
            System.out.println("*************CLientREpo");
            return hubspotDealDtoApp;
        } catch (WebClientResponseException e) {
            String errorBody = e.getResponseBodyAsString();
            throw new ExternalServiceException("Error HTTP desde HubSpot: " + errorBody);
        }catch (Exception e){
            throw new ExternalServiceException(
                    "Error inesperado comunicándose con HubSpot "+e.getMessage());
        }
    }

    private HubspotDealDtoApp mergeUser(HubspotDealDtoApp hubspotDealDtoApp){
        Map<String,Object> userFound = getOwnerByHubIDstatus(hubspotDealDtoApp.ownerId());
        String firstName = (String) userFound.get("firstName");
        String lastName = (String) userFound.get("lastName");
        return new HubspotDealDtoApp(hubspotDealDtoApp.id()
                ,hubspotDealDtoApp.ownerId()
                ,firstName+" "+lastName,
                hubspotDealDtoApp.pipelineType(),
                hubspotDealDtoApp.properties()
        );
    }

    public Map<String, Object> getOwnerByHubIDstatus(String hubId) {
        try {
            // 1. Intentar obtener el propietario activo (comportamiento por defecto)
            return fetchOwner(hubId, false);
        } catch (WebClientResponseException.NotFound e) {
            try {
                // 2. Si no se encuentra, intentar buscarlo como archivado
                return fetchOwner(hubId, true);
            } catch (WebClientResponseException.NotFound ex) {
                throw new ExternalServiceException("El propietario con ID " + hubId + " no existe en HubSpot (ni activo ni archivado).");
            }
        } catch (Exception e) {
            throw new ExternalServiceException("Error al comunicarse con HubSpot: " + e.getMessage());
        }
    }

    private Map<String, Object> fetchOwner(String hubId, boolean isArchived) {
        return hubspotWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(apiOwners + "{id}")
                        .queryParam("archived", isArchived)
                        .build(hubId))
                .retrieve()
                .bodyToMono(Map.class)
                .block();
    }
}