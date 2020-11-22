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

    @PostMapping(path = "email", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public void postEmail(@RequestBody Map<String, String> formData) {
        wichteltoolService.postEmail(formData.get("email"));
    }
    
    @PostMapping(path = "/code", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<User> postCode(@RequestParam String email, @RequestParam String logincode) {
        User user = wichteltoolService.postCode(email, logincode);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.status(HttpStatus.OK).body(user);
    }
}
