package com.sentiment.backend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

  @Value("${spring.application.name:Sentiment Analysis API}")
  private String appName;

  @Value("${app.version:1.0.0}")
  private String appVersion;

  @Value("${server.port:8082}")
  private String serverPort;

  @Bean
  public OpenAPI customOpenAPI() {
    Server localServer = new Server();
    localServer.setUrl("http://localhost:" + serverPort);
    localServer.setDescription("Servidor Local");

    Contact contact = new Contact();
    contact.setName("H12-25-B-Equipo 20-Data Science");
    contact.setEmail("contato@equipeone.com");

    License license = new License()
        .name("MIT License")
        .url("https://opensource.org/licenses/MIT");

    Info info = new Info()
        .title(appName)
        .version(appVersion)
        .description("API para análise de sentimento usando Machine Learning")
        .contact(contact)
        .license(license);

    return new OpenAPI()
        .info(info)
        .servers(List.of(localServer));
  }
}
