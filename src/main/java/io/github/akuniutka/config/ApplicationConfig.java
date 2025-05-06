package io.github.akuniutka.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.time.Clock;
import java.time.Duration;

@Configuration
@PropertySource("classpath:application.properties")
@ComponentScan(basePackages = "io.github.akuniutka")
public class ApplicationConfig {

    @Bean
    public Clock clock() {
        // Adjust application clock precision to that of PostgreSQL (1 microsecond)
        return Clock.tick(Clock.systemDefaultZone(), Duration.ofNanos(1_000));
    }
}
