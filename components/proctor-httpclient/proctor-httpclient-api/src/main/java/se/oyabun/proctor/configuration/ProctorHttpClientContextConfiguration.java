package se.oyabun.proctor.configuration;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Proctor HTTP Client context configuration
 */
@Configuration
@ComponentScan("se.oyabun.proctor.http.client")
public class ProctorHttpClientContextConfiguration {
}
