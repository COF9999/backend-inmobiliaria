package com.realstate.habitar.infraestructure.classes.model;

import com.realstate.habitar.infraestructure.classes.abstraxct.BaseFunnelConfigAbstract;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "funnel_sales_config")
public class FunnelSalesConfig extends BaseFunnelConfigAbstract {
}
