package com.realstate.habitar.infraestructure.adapters.interfaces;

public interface ProcessedDealRepository {
    boolean isExistKey(String key);

    boolean uniqueKey(String key);
}
