package de.essquare.wichteltool;

import java.util.Collections;
import java.util.List;
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

    private final DbRepository wichteltoolDbRepository;
    private final AmazonSimpleEmailService sesClient;

    public Service(DbRepository wichteltoolDbRepository,
                   AmazonSimpleEmailService sesClient) {
        this.wichteltoolDbRepository = wichteltoolDbRepository;
        this.sesClient = sesClient;
    }

    public void postEmail(String email) {
        User user = wichteltoolDbRepository.getByEmail(email);

        if (user == null) {
            user = User.build()
                       .withUserId(UUID.randomUUID().toString())
                       .withEmail(email);
        }

        String logincode = UUID.randomUUID().toString().replaceAll("-", ""); // it's easier to copy&paste without dashes
        System.out.println("logincode: " + logincode);
        user.setCode(logincode);
        if (wichteltoolDbRepository.usersEmpty()) {
            // the first user becomes admin
            user.setAdmin(true);
        }
        wichteltoolDbRepository.save(user);

        SendEmailRequest request = new SendEmailRequest()
                .withDestination(new Destination().withToAddresses(email))
                .withMessage(new Message()
                        .withBody(new Body().withText(new Content().withCharset("UTF-8").withData("dein Login Code lautet " + logincode)))
                        .withSubject(new Content().withCharset("UTF-8").withData("Login Code")))
                .withSource("wichteltool@essquare.de");
        sesClient.sendEmail(request);
    }

    public User postCode(String email, String logincode) {
        if (email == null || logincode == null) {
            return null;
        }

        User user = wichteltoolDbRepository.getByEmail(email);
        if (user == null) {
            return null;
        }

        if (!logincode.equals(user.getCode())) {
            return null;
        }

        user.setCode(UUID.randomUUID().toString());
        wichteltoolDbRepository.save(user);

        // don't deliver the partner's userId, deliver the partner's username
        if (user.getPartner() != null) {
            User partner = wichteltoolDbRepository.load(user.getPartner());
            if (partner != null) {
                user.setPartner(partner.getUsername());
            }
        }

        return user;
    }

    public HttpStatus saveUser(final User userWithNewData) {
        User currentUser = wichteltoolDbRepository.load(userWithNewData.getUserId());

        if (!currentUser.getCode().equals(userWithNewData.getCode())) {
            return HttpStatus.FORBIDDEN;
        }

        // this cannot be overwritten
        userWithNewData.setPartner(currentUser.getPartner());
        userWithNewData.setAdmin(currentUser.isAdmin());

        wichteltoolDbRepository.save(userWithNewData);

        return HttpStatus.OK;
    }

    public List<String> getPlayers() {
        return wichteltoolDbRepository.loadAllUsers().stream()
                                      .map(user ->
                                                   user.getUsername() == null || user.getUsername().isBlank()
                                                   ? user.getEmail()
                                                   : user.getUsername())
                                      .collect(Collectors.toList());
    }

    public HttpStatus linkPartner(final String userId, final String code) {
        User currentUser = wichteltoolDbRepository.load(userId);

        if (!currentUser.getCode().equals(code)) {
            return HttpStatus.FORBIDDEN;
        }

        if (!currentUser.isAdmin()) {
            return HttpStatus.FORBIDDEN;
        }

        List<User> allUsers = wichteltoolDbRepository.loadAllUsers();
        Collections.shuffle(allUsers);

        allUsers.get(0).setPartner(allUsers.get(allUsers.size() - 1).getUserId());
        for (int i = 1; i < allUsers.size(); i++) {
            allUsers.get(i).setPartner(allUsers.get(i - 1).getUserId());
        }

        return HttpStatus.OK;
    }
}
