package com.realstate.habitar.infraestructure.adapters.implementations;

import com.realstate.habitar.infraestructure.adapters.interfaces.salesCommission.SalesCommissionScaleRepository;
import com.realstate.habitar.infraestructure.classes.model.SalesCommissionScale;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Repository
public class SalesCommissionScaleAdapter implements SalesCommissionScaleRepository {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<SalesCommissionScale> findByRange(Long value) {
        try {
            Query query =  entityManager.createQuery("select s from SalesCommissionScale s where s.lowerLimit>=?1 and s.upperLimit<=?1", SalesCommissionScale.class);
            List<SalesCommissionScale> results = query.setParameter(1, value).setMaxResults(1).getResultList();
            return results.stream().findFirst();
        }catch (Exception e){
            return Optional.empty();
        }
    }


    @Override
    @Transactional(readOnly = true)
    public List<SalesCommissionScale> getAllCommisionsSalesScale() {
        try {
            return entityManager.createQuery("select s from SalesCommissionScale s")
                    .getResultList();
        }catch (Exception e){
            return Collections.emptyList();
        }
    }
}
