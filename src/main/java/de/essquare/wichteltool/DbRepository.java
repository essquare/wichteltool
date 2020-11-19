package de.essquare.wichteltool;

import java.util.Map;

import org.springframework.stereotype.Repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;

@Repository
public class DbRepository {

    private final DynamoDBMapper userDynamoDBMapper;
    private final DynamoDBMapperConfig userTableConfig;

    public DbRepository(DynamoDBMapper userDynamoDBMapper,
                        DynamoDBMapperConfig userTableConfig) {
        this.userDynamoDBMapper = userDynamoDBMapper;
        this.userTableConfig = userTableConfig;
    }

    public User getByEmail(String email) {
        DynamoDBQueryExpression<User> queryExpression = new DynamoDBQueryExpression<User>()
                .withIndexName("email-index")
                .withConsistentRead(false)
                .withKeyConditionExpression("email = :email")
                .withExpressionAttributeValues(Map.of(":email", new AttributeValue(email)))
                .withProjectionExpression("userId, email, name, image, code, partner");
        return userDynamoDBMapper.query(User.class, queryExpression, userTableConfig).get(0);
    }

    public void save(User user) {
        userDynamoDBMapper.save(user, userTableConfig);
    }
}
