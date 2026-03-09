package com.realstate.habitar.infraestructure.config.init;

import com.realstate.habitar.infraestructure.config.data.GlobalVariablesCache;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class GlobalVariablesLoader  {

    private final JdbcTemplate jdbcTemplate;
    private final GlobalVariablesCache globalVariablesCache;

    public GlobalVariablesLoader(JdbcTemplate jdbcTemplate,GlobalVariablesCache globalVariablesCache){
        this.jdbcTemplate = jdbcTemplate;
        this.globalVariablesCache = globalVariablesCache;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void load() {
        String sql = "SELECT key_variable, value_variable FROM global_variable";
        try {
            jdbcTemplate.query(sql, rs -> {
                String key = rs.getString("key_variable");
                String val = rs.getString("value_variable");
                globalVariablesCache.put(key, val);
            });
        }catch (Exception e){
            System.err.println(">> Error al cargar variables globales: " + e.getMessage());
        }
    }
}
