package de.essquare.wichteltool;

import java.util.UUID;

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
        user.setCode(logincode);
        wichteltoolDbRepository.save(user);

        SendEmailRequest request = new SendEmailRequest()
                .withDestination(new Destination().withToAddresses(email))
                .withMessage(new Message()
                        .withBody(new Body().withText(new Content().withCharset("UTF-8").withData("dein Login Code lautet " + logincode)))
                        .withSubject(new Content().withCharset("UTF-8").withData("Login Code")))
                .withSource("wichteltool@essquare.de");
        sesClient.sendEmail(request);
    }

    public User postCode(final String emailOrPhone, final String logincode) {
        if (emailOrPhone == null || logincode == null) {
            return null;
        }

        User user = wichteltoolDbRepository.getByEmail(emailOrPhone);
        if (user == null) {
            return null;
        }

        if (!logincode.equals(user.getCode())) {
            return null;
        }

        user.setCode(UUID.randomUUID().toString());

        return user;
    }
}
