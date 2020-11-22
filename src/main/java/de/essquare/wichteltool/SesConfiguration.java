package de.essquare.wichteltool;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;

@Configuration
public class SesConfiguration {

    private final String dynamoUrl;
    private final String awsRegion;

    public SesConfiguration(@Value("${aws.ses.endpoint.url}") String endpointUrl,
                            @Value("${aws.region}") String awsRegion) {
        this.dynamoUrl = endpointUrl;
        this.awsRegion = awsRegion;
     }

    @Bean
    public AmazonSimpleEmailService sesClient() {
        EndpointConfiguration endpointConfiguration = new EndpointConfiguration(dynamoUrl, awsRegion);
        return AmazonSimpleEmailServiceClientBuilder.standard()
                                                    .withEndpointConfiguration(endpointConfiguration)
                                                    .build();

    }

}