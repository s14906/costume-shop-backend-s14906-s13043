package com.costumeshop.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@PropertySources({
        @PropertySource("classpath:shop.properties")
})
public class PropertyService {
    private final Environment environment;

    public String getProperty(String propertyName) {
        String property = environment.getProperty(propertyName);
        if (property != null) {
            return property;
        }
        throw new RuntimeException("Could not find property: " + propertyName);
    }
}
