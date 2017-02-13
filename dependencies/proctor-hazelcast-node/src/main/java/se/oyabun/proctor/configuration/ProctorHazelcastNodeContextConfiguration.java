package se.oyabun.proctor.configuration;

import com.hazelcast.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProctorHazelcastNodeContextConfiguration {

    @Bean
    public Config hazelcastConfiguration() {

        return new Config();

    }

}
