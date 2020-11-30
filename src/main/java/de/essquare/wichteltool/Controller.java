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
        LOG.info("/api/email");
        LOG.info("data: {}", data);

        String email = data.get("email");
        if (email == null || email.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        service.postEmail(data.get("email"));

        return ResponseEntity.status(HttpStatus.OK).build();
    }
    
    @PostMapping(path = "/code", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<User> postCode(@RequestBody Map<String, String> data) {
        User user = service.postCode(data.get("email"), data.get("code"));

        if (user == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    @GetMapping(path = "/user", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<User> getUser(@RequestParam String userId, @RequestParam String code) {
        User user = service.getUser(userId, code);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    @PostMapping(path = "/user", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> postCode(@RequestBody User user) {
        HttpStatus httpStatus = service.saveUser(user);
        return ResponseEntity.status(httpStatus).build();
    }

    @GetMapping(path = "/players", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<String>> getPlayers() {
        return ResponseEntity.status(HttpStatus.OK).body(service.getPlayers());
    }

    @PostMapping(path = "/linkPartner", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> linkPartner(@RequestBody Map<String, String> data) {
        HttpStatus httpStatus = service.linkPartner(data.get("userId"), data.get("code"));
        return ResponseEntity.status(httpStatus).build();
    }
}
