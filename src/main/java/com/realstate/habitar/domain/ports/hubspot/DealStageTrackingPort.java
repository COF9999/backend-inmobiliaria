package com.realstate.habitar.domain.ports.hubspot;

import java.util.Optional;

public interface DealStageTrackingPort {
    Optional<Object> findByObjectId(Long objectId);


}
