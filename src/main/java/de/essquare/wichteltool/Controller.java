package de.essquare.wichteltool;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class Controller {

    private static final Logger LOG = LoggerFactory.getLogger(Controller.class);

    private final Service service;

    public Controller(Service service) {
        this.service = service;
    }

    @PostMapping(path = "email")
    public ResponseEntity<Void> postEmail(@RequestBody Map<String, String> data) {
        // ingress
        LOG.info("POST /api/email with {}", data);

        // validation
        String email = data.get(User.EMAIL_KEY);
        if (email == null || email.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        // business logic
        service.postEmail(data.get("email"));

        // egress
        LOG.info("POST /api/email successful");
        return ResponseEntity.status(HttpStatus.OK).build();
    }
    
    @PostMapping(path = "/code", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<User> postCode(@RequestBody Map<String, String> data) {
        // ingress
        LOG.info("POST /api/code with {}", data);

        // no explicit validation

        // business logic
        User user = service.postCode(data.get(User.EMAIL_KEY), data.get(User.CODE_KEY));
        if (user == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // egress
        LOG.info("POST /api/code successful");
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    @GetMapping(path = "/user", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<User> getUser(@RequestParam String userId, @RequestParam String code) {
        // ingress
        LOG.info("GET /api/user with userId {} and code {}", userId, code);

        // no explicit validation

        // business logic
        User user = service.getUser(userId, code);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        // egress
        LOG.info("GET /api/user successful");
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    @PostMapping(path = "/user", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> postUser(@RequestBody Map<String, String> data) {
        // ingress
        LOG.info("POST /api/user with {}", data);

        // no explicit validation

        // business logic
        HttpStatus httpStatus = service.saveUser(data);

        // egress
        LOG.info("POST /api/user successful");
        return ResponseEntity.status(httpStatus).build();
    }

    @GetMapping(path = "/players", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<String>> getPlayers() {
        // ingress
        LOG.info("GET /api/players");

        // no explicit validation

        // business logic
        List<String> players = service.getPlayers();

        // egress
        LOG.info("GET /api/players successful");
        return ResponseEntity.status(HttpStatus.OK).body(players);
    }

    @PostMapping(path = "/linkPartner", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> linkPartner(@RequestBody Map<String, String> data) {
        // ingress
        LOG.info("POST /api/linkPartner with {}", data);

        // no explicit validation

        // business logic
        HttpStatus httpStatus = service.linkPartner(data.get(User.USER_ID_KEY), data.get(User.CODE_KEY));

        // egress
        LOG.info("POST /api/linkPartner successful");
        return ResponseEntity.status(httpStatus).build();
    }
}
