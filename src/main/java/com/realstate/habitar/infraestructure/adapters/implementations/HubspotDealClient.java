package com.realstate.habitar.infraestructure.adapters.implementations;

import com.realstate.habitar.domain.dispactchers.PipelineType;
import com.realstate.habitar.domain.dtos.hubspot.HubSpotDealKeyRecord;
import com.realstate.habitar.domain.dtos.hubspot.HubSpotSearchResponse;
import com.realstate.habitar.domain.dtos.hubspot.HubspotDealDtoApp;
import com.realstate.habitar.infraestructure.adapters.interfaces.hubspot.HspotClientRepository;
import com.realstate.habitar.infraestructure.advicers.exceptions.ExternalServiceException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.reactive.resource.NoResourceFoundException;

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
    public List<HubspotDealDtoApp> findDeals(List<Map<String,Object>> mapList) {
        System.out.println("**********************FIND-DEALS");
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
    public HubspotDealDtoApp findDealByPrincipalInfo(HubSpotDealKeyRecord hubSpotDealKeyRecord) {
        try {
            // Se usa el endpoint: /crm/v3/objects/deals/{dealId}
            HubspotDealDtoApp response = hubspotWebClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/crm/v3/objects/deals/{dealId}") // Ruta limpia sin "search"
                            .queryParam("properties", "hubspot_owner_id,dealname,amount,dealstage,pipeline,closedate")
                            .build(hubSpotDealKeyRecord.dealId())) // WebClient acepta String aquí, no hace falta parsear
                    .retrieve()
                    .bodyToMono(HubspotDealDtoApp.class) // Clase que represente un solo Deal
                    .block();

            if (response == null) {
                throw new NoResourceFoundException("Elemento negocio no encontrado");
            }

            HubspotDealDtoApp mappedResponse = new HubspotDealDtoApp(
                    response.id(),
                    response.properties().get("hubspot_owner_id"),
                    null,
                    response.properties().get("pipeline"),
                    response.properties()
            );

            return mergeUser(mappedResponse);

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