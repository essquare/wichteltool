package de.essquare.wichteltool;

import java.util.Objects;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIndexHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName = "User")
public class User {

    private String userId;
    private String email;
    private String username;
    private String code;
    private String partner;
    private boolean admin;

    public User() {
    }

    public User(String userId, String email, String username, String code, String partner, final boolean admin) {
        this.userId = userId;
        this.email = email;
        this.username = username;
        this.code = code;
        this.partner = partner;
        this.admin = admin;
    }

    public static User build() {
        return new User();
    }

    public User withUserId(String userId) {
        setUserId(userId);
        return this;
    }

    public User withEmail(String email) {
        setEmail(email);
        return this;
    }

    public User withUsername(String username) {
        setUsername(username);
        return this;
    }

    public User withCode(String code) {
        setCode(code);
        return this;
    }

    public User withPartner(String partner) {
        setPartner(partner);
        return this;
    }

    @DynamoDBHashKey
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @DynamoDBIndexHashKey(globalSecondaryIndexName = "email-index")
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPartner() {
        return partner;
    }

    public void setPartner(String partner) {
        this.partner = partner;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(final boolean admin) {
        this.admin = admin;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final User user = (User) o;
        return admin == user.admin &&
               Objects.equals(userId, user.userId) &&
               Objects.equals(email, user.email) &&
               Objects.equals(username, user.username) &&
               Objects.equals(code, user.code) &&
               Objects.equals(partner, user.partner);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, email, username, code, partner, admin);
    }

    @Override
    public String toString() {
        return "User{" +
               "userId='" + userId + '\'' +
               ", email='" + email + '\'' +
               ", username='" + username + '\'' +
               ", code='" + code + '\'' +
               ", partner='" + partner + '\'' +
               ", admin=" + admin +
               '}';
    }
}
