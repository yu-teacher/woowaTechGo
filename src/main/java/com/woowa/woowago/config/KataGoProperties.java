package com.woowa.woowago.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "katago")
public class KataGoProperties {
    private String path;
    private String config;
    private String model;
}