package de.essquare.wichteltool;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedQueryList;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedScanList;
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
                .withProjectionExpression("userId, email, username, image, code, partner, admin");
        PaginatedQueryList<User> queryList = userDynamoDBMapper.query(User.class, queryExpression, userTableConfig);
        if (queryList.isEmpty()) {
            return null;
        }
        return queryList.get(0);
    }

    public boolean usersEmpty() {
        return userDynamoDBMapper.count(User.class, new DynamoDBScanExpression().withLimit(1), userTableConfig) == 0;
    }

    public void save(User user) {
        userDynamoDBMapper.save(user, userTableConfig);
    }

    public User load(String userId) {
        return userDynamoDBMapper.load(User.class, userId, userTableConfig);
    }

    public List<User> loadAllUsers() {
        return new ArrayList<>(userDynamoDBMapper.scan(User.class, new DynamoDBScanExpression(), userTableConfig));
    }

    public void saveUsers(List<User> users) {
        userDynamoDBMapper.batchSave(users);
    }
}
