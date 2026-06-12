package com.as.OpenKnowledgeStream.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class AppConfig {

    @Bean
    public WebClient webClient() {
        return WebClient.builder().baseUrl("https://en.wikipedia.org/w/api.php?action=query&list=recentchanges&format=json&rclimit=100")
                .defaultHeader("accept", "application/json")
                .build();
    }
}
