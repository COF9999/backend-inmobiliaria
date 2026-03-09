package com.realstate.habitar.domain.hubspot;

import java.util.Map;

public class ObjectHubSpot {
    private String nameObjectHubSpot;
    private Map<String,String> mapProperties;

    public ObjectHubSpot(String nameObjectHubSpot, Map<String, String> mapProperties) {
        this.nameObjectHubSpot = nameObjectHubSpot;
        this.mapProperties = mapProperties;
    }

    public String getNameObjectHubSpot() {
        return nameObjectHubSpot;
    }

    public Map<String, String> getMapProperties() {
        return mapProperties;
    }
}
