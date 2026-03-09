package com.realstate.habitar.infraestructure.adapters.implementations;

import com.realstate.habitar.domain.PipelineType;

import com.realstate.habitar.domain.dtos.hubspot.HubSpotSearchResponse;
import com.realstate.habitar.domain.dtos.hubspot.HubspotDealDtoApp;
import com.realstate.habitar.domain.dtos.sales.LiquidationTimeRecord;
import com.realstate.habitar.infraestructure.adapters.interfaces.HspotClientRepository;
import com.realstate.habitar.infraestructure.advicers.exceptions.ExternalServiceException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    public List<HubspotDealDtoApp> findDeal(List<Map<String,Object>> mapList) {
        Map<String, Object> requestBody = Map.of(
                "filterGroups", List.of(
                        Map.of(
                                "filters", mapList
                        )
                ),
                "properties", List.of("hubspot_owner_id", "dealname", "amount", "dealstage", "pipeline", "closedate"),
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
                    .map(h-> new HubspotDealDtoApp(h.id(),h.properties().get("hubspot_owner_id"),h.properties().get("pipeline"),h.properties()))
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


/*
    public Boolean isOwnerActive(String hubId) {
        try {
            Map<String, Object> response = hubspotWebClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path(apiOwners + "{id}") // El marcador es {id}
                            .queryParam("archived", "true")
                            .build(hubId))            // AQUÍ se pasa el hubId al marcador {id}
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            // En HubSpot: archived = false significa que está ACTIVO
            return response != null && Boolean.FALSE.equals(response.get("archived"));

        } catch (WebClientResponseException e) {
            String errorBody = e.getResponseBodyAsString();
            System.err.println("Cuerpo del error HubSpot: " + errorBody);
            throw new ExternalServiceException("Error HTTP desde HubSpot: " + errorBody);
        } catch (Exception e) {
            throw new ExternalServiceException(
                    "Error inesperado comunicándose con HubSpot "+e.getMessage());
        }
    }

 */

}