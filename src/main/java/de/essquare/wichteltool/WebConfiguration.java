package de.essquare.wichteltool;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Configuration
public class WebConfiguration implements WebMvcConfigurer {

    private final String[] allowedOrigins;

    public WebConfiguration(@Value("${web.allowed.origins}") String[] allowedOrigins) {
        if (allowedOrigins.length == 0) {
            throw new IllegalArgumentException("'web.allowed.origins' may not be empty");
        }

        if (Arrays.stream(allowedOrigins).anyMatch(StringUtils::isEmpty)) {
            throw new IllegalArgumentException("'web.allowed.origins' may not contain any empty values");
        }

        this.allowedOrigins = allowedOrigins;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry
                .addMapping("/**")
                .allowedMethods("GET")
                .allowedOrigins(allowedOrigins);
    }

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.stream()
                  .filter(converter -> converter instanceof MappingJackson2HttpMessageConverter)
                  .forEach(jacksonConv -> reconfigureJacksonConverter((MappingJackson2HttpMessageConverter) jacksonConv));
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addRedirectViewController("/", "index.html");
    }

    private void reconfigureJacksonConverter(MappingJackson2HttpMessageConverter jacksonConverter) {
        ObjectMapper objectMapper = jacksonConverter.getObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.registerModule(new JavaTimeModule());
    }
}
