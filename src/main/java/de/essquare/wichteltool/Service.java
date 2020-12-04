package de.essquare.wichteltool;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.model.Body;
import com.amazonaws.services.simpleemail.model.Content;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.Message;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;

@org.springframework.stereotype.Service
class Service {

    private final DbRepository dbRepository;
    private final AmazonSimpleEmailService sesClient;

    public Service(DbRepository dbRepository,
                   AmazonSimpleEmailService sesClient) {
        this.dbRepository = dbRepository;
        this.sesClient = sesClient;
    }

    public void postEmail(String email) {
        User user = dbRepository.getByEmail(email);
        if (user == null) {
            user = User.build()
                       .withUserId(UUID.randomUUID().toString())
                       .withEmail(email);
        }

        String logincode = UUID.randomUUID().toString().replaceAll("-", ""); // it's easier to copy&paste without dashes
        user.setCode(logincode);
        if (dbRepository.usersEmpty()) {
            // the first user becomes admin
            user.setAdmin(true);
        }
        dbRepository.save(user);

        SendEmailRequest request = new SendEmailRequest()
                .withDestination(new Destination().withToAddresses(email))
                .withMessage(new Message()
                                     .withBody(new Body().withText(new Content().withCharset("UTF-8").withData("dein Login Code lautet " + logincode)))
                                     .withSubject(new Content().withCharset("UTF-8").withData("Login Code")))
                .withSource("dirk.podolak@essquare.de"); // I'd like to  use "wichteltool@essquare.de", but it does not exist and therefore cannot be validated for AWS SES
        sesClient.sendEmail(request);
    }

    public User postCode(String email, String logincode) {
        if (email == null || logincode == null) {
            return null;
        }

        User user = dbRepository.getByEmail(email);
        if (user == null) {
            return null;
        }

        if (!logincode.equals(user.getCode())) {
            return null;
        }

        user.setCode(UUID.randomUUID().toString());
        dbRepository.save(user);

        return mapPartner(user);
    }

    public HttpStatus saveUser(Map<String, String> data) {
        String userId = data.get(User.USER_ID_KEY);
        User currentUser = dbRepository.load(userId);

        if (!currentUser.getCode().equals(data.get(User.CODE_KEY))) {
            return HttpStatus.FORBIDDEN;
        }

        // currently there is only one field that can be overwritten
        currentUser.setUsername(data.get(User.USERNAME_KEY));

        dbRepository.save(currentUser);

        return HttpStatus.OK;
    }

    public List<String> getPlayers() {
        return dbRepository.loadAllUsers().stream()
                           .map(this::getDescriptor)
                           .collect(Collectors.toList());
    }

    public HttpStatus linkPartner(String userId, String code) {
        User currentUser = dbRepository.load(userId);

        if (!currentUser.getCode().equals(code)) {
            return HttpStatus.FORBIDDEN;
        }

        if (!currentUser.isAdmin()) {
            return HttpStatus.FORBIDDEN;
        }

        List<User> allUsers = dbRepository.loadAllUsers();
        Collections.shuffle(allUsers);

        allUsers.get(0).setPartner(allUsers.get(allUsers.size() - 1).getUserId());
        for (int i = 1; i < allUsers.size(); i++) {
            allUsers.get(i).setPartner(allUsers.get(i - 1).getUserId());
        }
        dbRepository.saveUsers(allUsers);

        return HttpStatus.OK;
    }

    public User getUser(String userId, String code) {
        User user = dbRepository.load(userId);

        if (user == null) {
            return null;
        }

        if (Objects.equals(user.getCode(), code)) {
            return mapPartner(user);
        }

        return null;
    }

    private User mapPartner(User user) {
        // don't deliver the partner's userId, deliver the partner's username
        if (user.getPartner() != null) {
            User partner = dbRepository.load(user.getPartner());
            if (partner != null) {
                user.setPartner(getDescriptor(partner));
            }
        }
        return user;
    }

    private String getDescriptor(User user) {
        return user.getUsername() == null || user.getUsername().isBlank()
               ? user.getEmail()
               : user.getUsername();
    }
}
