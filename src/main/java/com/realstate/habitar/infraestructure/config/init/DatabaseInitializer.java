package com.realstate.habitar.infraestructure.config.init;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.Statement;

@Order(1)
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(
        name = "app.db.init.enabled",
        havingValue = "true",
        matchIfMissing = false
)
public class DatabaseInitializer implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;

    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

        Integer countGlobal = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM global_variable",
                Integer.class
        );

        if (countGlobal != null && countGlobal == 0) {
            jdbcTemplate.update("""
                INSERT INTO global_variable (id, key_variable, value_variable) VALUES
                (1, 'account_owner', '80651139'),
                (2, 'percentage_sale', '3.0')
            """);
        }

        Integer countScale = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM sales_commission_scale",
                Integer.class
        );

        if (countScale != null && countScale == 0) {
            jdbcTemplate.update("""
                INSERT INTO sales_commission_scale 
                (id, commission_percentage, lower_limit, upper_limit) VALUES
                (1, 1.200, 0, 500000000),
                (2, 1.300, 500000001, 1000000000),
                (3, 1.400, 1000000001, 2000000000),
                (4, 1.420, 2000000001, 3000000000),
                (5, 1.450, 3000000001, NULL)
            """);
        }

        Integer countRole = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM role",
                Integer.class
        );

        if (countRole != null && countRole == 0) {
            jdbcTemplate.update("""
                INSERT INTO role (id, name) VALUES
                (1, 'ROLE_ADMIN'),
                (2, 'ROLE_USER')
            """);
        }

        insertRolesIfNotExists();
        insertUsersIfNotExists();
    }

    private void insertRolesIfNotExists() {

        Integer countRole = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM role",
                Integer.class
        );

        if (countRole != null && countRole == 0) {

            jdbcTemplate.update("""
                    INSERT INTO role (id, name) VALUES
                    (1, 'ROLE_ADMIN'),
                    (2, 'ROLE_USER')
                    """);
        }
    }

    private void insertUsersIfNotExists() {

        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM user",
                Integer.class
        );

        if (count != null && count == 0) {

            insertUserWithRole("Christián Felippe","2451","87564785","99",
                    "Christián Felippe Orjuela Forero",
                    "corjuela1030@cue.edu.co","ROLE_ADMIN");

            insertUserWithRole("Sebastian","0000000","80651139","0",
                    "Sebastian Cardona",
                    "scardona@habitarinmobiliaria.co","ROLE_ADMIN");

            insertUserWithRole("nicolas","47283915","87564804","12",
                    "NICOLAS CUERVO RIOS","nicolas@test.com","ROLE_USER");

            insertUserWithRole("thomas","59382014","88099857","34",
                    "Thomas Velez Molina","thomas@test.com","ROLE_USER");

            insertUserWithRole("andrea","68429173","88951710","56",
                    "Andrea Rodríguez García","andrea@test.com","ROLE_USER");

            insertUserWithRole("maria","75192046","391675794","78",
                    "Maria Uribe","maria@test.com","ROLE_USER");

            insertUserWithRole("lina","86204731","411856232","90",
                    "Lina Maria Mora Medina","lina@test.com","ROLE_USER");

            insertUserWithRole("legal","90318472","586606470","11",
                    "legal habitar","legal@test.com","ROLE_USER");

            insertUserWithRole("database","12837465","660246563","22",
                    "Database Habitar","database@test.com","ROLE_USER");

            insertUserWithRole("diana","34782910","1389487631","33",
                    "Diana Velasquez","diana@test.com","ROLE_USER");

            insertUserWithRole("juan","56473829","81663068","44",
                    "juan david celis","juan@test.com","ROLE_USER");

            insertUserWithRole("contabilidad","67584920","84412519","55",
                    "contabilidad habitar","contabilidad@test.com","ROLE_USER");

            insertUserWithRole("nicolasr","78910234","84864095","66",
                    "Nicolás Rodríguez","nicolasr@test.com","ROLE_USER");

            insertUserWithRole("simon","89021347","85206648","77",
                    "simon gallego","simon@test.com","ROLE_USER");
        }
    }

    private void insertUserWithRole(
            String username,
            String identification,
            String hubId,
            String rawPassword,
            String name,
            String email,
            String roleName) {

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {

            PreparedStatement ps = connection.prepareStatement("""
                    INSERT INTO user
                    (email, hub_id, identification, is_active, name, password, username)
                    VALUES (?, ?, ?, b'1', ?, ?, ?)
                    """, Statement.RETURN_GENERATED_KEYS);

            ps.setString(1, email);
            ps.setString(2, hubId);
            ps.setString(3, identification);
            ps.setString(4, name);
            ps.setString(5, passwordEncoder.encode(rawPassword));
            ps.setString(6, username);

            return ps;

        }, keyHolder);

        Long userId = keyHolder.getKey().longValue();

        Long roleId = jdbcTemplate.queryForObject(
                "SELECT id FROM role WHERE name = ?",
                Long.class,
                roleName
        );

        jdbcTemplate.update("""
                INSERT INTO users_roles (user_id, role_id)
                VALUES (?, ?)
                """,
                userId,
                roleId
        );

        insertUserLiquidation(userId, BigDecimal.ZERO);
    }

    private void insertUserLiquidation(Long userId, BigDecimal totalAmount) {

        jdbcTemplate.update("""
            INSERT INTO user_liquidation (total_amount, user_id)
            VALUES (?, ?)
            """,
                totalAmount,
                userId
        );
    }
}