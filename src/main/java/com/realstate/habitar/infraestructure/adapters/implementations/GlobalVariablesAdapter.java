package com.realstate.habitar.infraestructure.adapters.implementations;

import com.realstate.habitar.global.domain.ports.DaoCrudPort;
import com.realstate.habitar.infraestructure.adapters.interfaces.globalVariables.GlobalVariablesRepository;
import com.realstate.habitar.infraestructure.classes.model.GlobalVariables;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository

public class GlobalVariablesAdapter implements DaoCrudPort<GlobalVariables>, GlobalVariablesRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<GlobalVariables> getListGlobalVariables() {
        return List.of();
    }

    @Override
    public List<GlobalVariables> selectAll() {
        return entityManager
                .createQuery("SELECT g FROM GlobalVariables g",GlobalVariables.class)
                .getResultList();
    }

    @Override
    public Optional<GlobalVariables> get(Long id) {
        return Optional.empty();
    }

    @Override
    public Optional<GlobalVariables> create(GlobalVariables object) {
        return Optional.empty();
    }

    @Override
    public Optional<GlobalVariables> update(GlobalVariables object) {
        return Optional.empty();
    }

    @Override
    public Optional<GlobalVariables> delete(Long id) {
        return Optional.empty();
    }
}
