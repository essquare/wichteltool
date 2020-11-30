package de.essquare.wichteltool;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;

@Configuration
public class Configurations implements WebMvcConfigurer {

    private static final Logger LOG = LoggerFactory.getLogger(Configurations.class);

    private final String[] allowedOrigins;
    private final String sesEndpointUrl;
    private final String dynamoDbEndpointUrl;
    private final String awsRegion;
    private final String userTableName;

    public Configurations(@Value("${web.allowed.origins}") String[] allowedOrigins,
                          @Value("${aws.ses.endpoint.url}") String sesEndpointUrl,
                          @Value("${aws.dynamo.endpoint.url}") String dynamoDbEndpointUrl,
                          @Value("${aws.region}") String awsRegion,
                          @Value("${aws.dynamo.user.tablename}") String userTableName) {
        if (allowedOrigins.length == 0) {
            throw new IllegalArgumentException("'web.allowed.origins' may not be empty");
        }

        if (Arrays.stream(allowedOrigins).anyMatch(StringUtils::isEmpty)) {
            throw new IllegalArgumentException("'web.allowed.origins' may not contain any empty values");
        }

        this.allowedOrigins = allowedOrigins;
        this.sesEndpointUrl = sesEndpointUrl;
        this.awsRegion = awsRegion;
        this.dynamoDbEndpointUrl = dynamoDbEndpointUrl;
        this.userTableName = userTableName;

        LOG.info("sesEndpointUrl {}", sesEndpointUrl);
        LOG.info("awsRegion {}", awsRegion);
        LOG.info("dynamoDbEndpointUrl {}", dynamoDbEndpointUrl);
        LOG.info("userTableName {}", userTableName);
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

    private void reconfigureJacksonConverter(MappingJackson2HttpMessageConverter jacksonConverter) {
        ObjectMapper objectMapper = jacksonConverter.getObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Bean
    public AmazonSimpleEmailService sesClient() {
        EndpointConfiguration endpointConfiguration = new EndpointConfiguration(sesEndpointUrl, awsRegion);
        return AmazonSimpleEmailServiceClientBuilder.standard()
                                                    .withEndpointConfiguration(endpointConfiguration)
                                                    .build();

    }

    @Bean
    public AmazonDynamoDB amazonDynamoDB() {
        EndpointConfiguration endpointConfiguration = new EndpointConfiguration(dynamoDbEndpointUrl, awsRegion);
        return AmazonDynamoDBClientBuilder.standard()
                                          .withEndpointConfiguration(endpointConfiguration)
                                          .build();
    }

    @Bean
    public DynamoDBMapper userDynamoDBMapper() {
        return new DynamoDBMapper(amazonDynamoDB(), userTableConfig());
    }

    @Bean
    public DynamoDBMapperConfig userTableConfig() {
        return DynamoDBMapperConfig.builder()
                                   .withTableNameOverride(new DynamoDBMapperConfig.TableNameOverride(userTableName))
                                   .build();
    }
}
