package se.oyabun.proctor.configuration;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Proctor admin service context configuration
 */
@Configuration
@ComponentScan("se.oyabun.proctor.web.admin")
public class ProctorAdminWebServiceContextConfiguration {

}
