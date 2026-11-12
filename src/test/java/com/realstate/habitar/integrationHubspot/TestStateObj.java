package com.realstate.habitar.integrationHubspot;

import com.realstate.habitar.application.usecases.dealRegistry.DealRegistryService;
import com.realstate.habitar.domain.enums.StatesObjects;
import com.realstate.habitar.infraestructure.classes.model.DealRegistry;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;


@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // <--- ESTO ES LA CLAVE
@Transactional
public class TestStateObj {


    @Autowired
    EntityManager entityManager;

    @Autowired
    DealRegistryService dealRegistryService;



    @Test
    void select(){
        Optional<DealRegistry> optionalDealRegistry = dealRegistryService.findDealUserId("57979911073-81663068");

        System.out.println(optionalDealRegistry.orElseThrow().toString());
    }

    @Test
    void notIn(){


    }

    @Test
    void diagnosticTest() {
        String idABuscar = "57979911073-81663068";

        // 1. Prueba búsqueda directa con JPQL
        List<DealRegistry> lista = entityManager.createQuery(
                        "SELECT d FROM DealRegistry d WHERE d.dealUserId = :id", DealRegistry.class)
                .setParameter("id", idABuscar)
                .getResultList();

        System.out.println("¿Encontrado con JPQL directo?: " + !lista.isEmpty());

        // 2. Si el anterior da FALSE, prueba con LIKE (para detectar espacios invisibles)
        if (lista.isEmpty()) {
            long conLike = entityManager.createQuery(
                            "SELECT COUNT(d) FROM DealRegistry d WHERE d.dealUserId LIKE :id", Long.class)
                    .setParameter("id", "%" + idABuscar + "%")
                    .getSingleResult();
            System.out.println("¿Encontrado usando LIKE?: " + (conLike > 0));
        }

        // 3. Prueba tu servicio
        boolean servicioOk = dealRegistryService.findDealUserId(idABuscar).isPresent();
        System.out.println("¿Servicio encuentra el registro?: " + servicioOk);
    }

    @Test
    void printAnyThing(){
        Long total = entityManager.createQuery("SELECT COUNT(d) FROM DealRegistry d", Long.class).getSingleResult();
        System.out.println("Total registros en tabla: " + total);

// Esto imprimirá lo que realmente está llegando
        entityManager.createQuery("SELECT d.dealUserId FROM DealRegistry d", String.class)
                .getResultList()
                .forEach(id -> System.out.println("ID en DB: '" + id + "'"));
    }

    @Test
    void finalM(){
        Set<StatesObjects> includeValues = StatesObjects.getSetpartiallyNotRegister();
        Long count = entityManager.createQuery("SELECT COUNT(d) FROM DealRegistry d WHERE d.dealUserId = :key and d.state IN :includevalues", Long.class)
                .setParameter("key", "57979911073-81663068")
                .setParameter("includevalues", includeValues)
                .getSingleResult();

        System.out.println(count);
    }
}
