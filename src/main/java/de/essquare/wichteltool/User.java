package de.essquare.wichteltool;

import java.util.Objects;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIndexHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName = "User")
public class User {

    private String userId;
    private String email;
    private String name;
    private String image;
    private String code;
    private String partner;

    public User() {
    }

    public User(String userId, String email,String name, String image, String code, String partner) {
        this.userId = userId;
        this.email = email;
        this.name = name;
        this.image = image;
        this.code = code;
        this.partner = partner;
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

    public User withName(String name) {
        setName(name);
        return this;
    }

    public User withImage(String image) {
        setImage(image);
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
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

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final User user = (User) o;
        return Objects.equals(userId, user.userId) &&
               Objects.equals(email, user.email) &&
               Objects.equals(name, user.name) &&
               Objects.equals(image, user.image) &&
               Objects.equals(code, user.code) &&
               Objects.equals(partner, user.partner);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, email, name, image, code, partner);
    }

    @Override
    public String toString() {
        return "User{" +
               "userId='" + userId + '\'' +
               ", email='" + email + '\'' +
               ", name='" + name + '\'' +
               ", image='" + image + '\'' +
               ", code='" + code + '\'' +
               ", partner='" + partner + '\'' +
               '}';
    }
}
