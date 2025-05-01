package io.github.akuniutka.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import java.util.function.Consumer;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "io.github.akuniutka")
@RequiredArgsConstructor
@Slf4j
public class PersistenceConfig {

    private static final String DATABASE_SCHEMA_PATH = "schema.sql";
    private static final String DATABASE_DATA_PATH = "data.sql";

    private final Environment environment;

    @Bean
    public DataSource dataSource() throws SQLException {
        final HikariConfig config = new HikariConfig();
        putPropertyIfNotNull("jdbc.driverClassName", config::setDriverClassName);
        putPropertyIfNotNull("jdbc.url", config::setJdbcUrl);
        putPropertyIfNotNull("jdbc.username", config::setUsername);
        putPropertyIfNotNull("jdbc.password", config::setPassword);
        final DataSource dataSource = new HikariDataSource(config);
        runInitScriptIfRequired(dataSource);
        return dataSource;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(final DataSource dataSource) {
        final HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        final LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setDataSource(dataSource);
        factory.setJpaVendorAdapter(vendorAdapter);
        factory.setPackagesToScan("io.github.akuniutka");
        factory.setJpaProperties(hibernateProperties());
        return factory;
    }

    @Bean
    public JpaTransactionManager transactionManager(final EntityManagerFactory entityManagerFactory) {
        final JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory);
        return transactionManager;
    }

    private Properties hibernateProperties() {
        final Properties properties = new Properties();
        properties.put("hibernate.jdbc.time_zone", environment.getRequiredProperty("hibernate.jdbc.time_zone"));
        properties.put("hibernate.show_sql", environment.getProperty("hibernate.show_sql", "false"));
        return properties;
    }

    private void runInitScriptIfRequired(final DataSource dataSource) throws SQLException {
        final String propertyValue = environment.getProperty("jdbc.run-init-script");
        if (propertyValue == null || !propertyValue.equalsIgnoreCase("true")) {
            return;
        }
        final Connection connection = dataSource.getConnection();
        ClassPathResource script = new ClassPathResource(DATABASE_SCHEMA_PATH);
        if (!script.exists()) {
            return;
        }
        ScriptUtils.executeSqlScript(connection, script);
        log.info("Database schema initialized");
        script = new ClassPathResource(DATABASE_DATA_PATH);
        if (!script.exists()) {
            return;
        }
        ScriptUtils.executeSqlScript(connection, script);
        log.info("Database populated with initial data");
    }

    private void putPropertyIfNotNull(final String propertyName, final Consumer<String> consumer) {
        final String propertyValue = environment.getProperty(propertyName);
        if (propertyValue != null) {
            consumer.accept(propertyValue);
        }
    }
}
