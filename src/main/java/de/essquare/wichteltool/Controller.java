package de.essquare.wichteltool;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class Controller {

    private final Service wichteltoolService;

    public Controller(Service wichteltoolService) {
        this.wichteltoolService = wichteltoolService;
    }

    @PostMapping(path = "email")
    public ResponseEntity<Void> postEmail(@RequestBody Map<String, String> data) {
        String email = data.get("email");
        if (email == null || email.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        wichteltoolService.postEmail(data.get("email"));

        return ResponseEntity.status(HttpStatus.OK).build();
    }
    
    @PostMapping(path = "/code", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<User> postCode(@RequestBody Map<String, String> data) {
        User user = wichteltoolService.postCode(data.get("email"), data.get("code"));

        if (user == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    @PostMapping(path = "/user", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> postCode(@RequestBody User user) {
        return ResponseEntity.status(wichteltoolService.saveUser(user)).build();
    }
}
