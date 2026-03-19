package com.realstate.habitar.domain.dtos.user;

public record RoleChangeDto(String hubId,
                            String strOldRole,
                            String strNewRole,
                            String type
) {
}
