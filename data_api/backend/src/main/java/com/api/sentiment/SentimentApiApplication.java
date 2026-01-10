package com.api.sentiment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class SentimentApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(SentimentApiApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate() {
        // Configure sane timeouts to avoid hanging connections
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5000);
        factory.setReadTimeout(5000);
        return new RestTemplate(factory);
    }

}