package de.essquare.wichteltool;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;

@Configuration
public class DbConfiguration {

    private final String dynamoUrl;
    private final String awsRegion;
    private final String userTableName;

    public DbConfiguration(@Value("${aws.dynamo.endpoint.url}") String endpointUrl,
                           @Value("${aws.region}") String awsRegion,
                           @Value("${aws.dynamo.user.tablename}") String userTableName) {
        this.dynamoUrl = endpointUrl;
        this.awsRegion = awsRegion;
        this.userTableName = userTableName;
    }

    @Bean
    public AmazonDynamoDB amazonDynamoDB() {
        EndpointConfiguration endpointConfiguration = new EndpointConfiguration(dynamoUrl, awsRegion);
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