package com.realstate.habitar.application.usecases.hubspot;

import java.util.Map;

public interface HubspotCommand {
    Map<String, Object> findOwnerByHubId(String hubId);
}
