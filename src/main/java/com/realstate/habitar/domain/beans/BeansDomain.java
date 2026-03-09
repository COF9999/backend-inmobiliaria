package com.realstate.habitar.domain.beans;

import com.realstate.habitar.application.usecases.components.SalesComponentInterface;
import com.realstate.habitar.domain.FunnelSales;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeansDomain {

    @Bean
    public FunnelSales funnelSales(){
        return new FunnelSales();
    }
}
