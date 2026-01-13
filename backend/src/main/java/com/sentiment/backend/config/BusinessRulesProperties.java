package com.sentiment.backend.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Data
@Component
@ConfigurationProperties(prefix = "business-rules")
public class BusinessRulesProperties {

  private Map<String, SectorConfig> sectors;
  private List<String> criticalAlerts;
  private Map<String, ResponseTemplate> responseTemplates;

  @Data
  public static class SectorConfig {
    private List<String> keywords;
  }

  @Data
  public static class ResponseTemplate {
    private String positivo;
    private String neutro;
    private String negativo;
  }
}
