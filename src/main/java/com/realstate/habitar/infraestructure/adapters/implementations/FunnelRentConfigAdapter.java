package com.realstate.habitar.infraestructure.adapters.implementations;

import com.realstate.habitar.infraestructure.adapters.interfaces.funnelrentconfig.FunnelRentConfigRepository;
import com.realstate.habitar.infraestructure.classes.model.FunnelRentConfig;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class FunnelRentConfigAdapter implements FunnelRentConfigRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<FunnelRentConfig> getAll() {
        return entityManager
                .createQuery("SELECT fnrent FROM FunnelRentConfig fnrent", FunnelRentConfig.class)
                .getResultList();
    }
}
