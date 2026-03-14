package com.realstate.habitar.domain.beans;

import com.realstate.habitar.domain.baselogic.FunnelSales;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeansDomain {

    @Bean
    public FunnelSales funnelSales(){
        return new FunnelSales();
    }
}
