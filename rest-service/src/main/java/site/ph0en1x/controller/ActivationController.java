package site.ph0en1x.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import site.ph0en1x.service.UserActivationService;

@RestController
@RequestMapping("/user")
public class ActivationController {
    private final UserActivationService activationService;

    public ActivationController(UserActivationService activationService) {
        this.activationService = activationService;
    }

    public ResponseEntity<?> activation(@RequestParam("id") String id) {
        var res = activationService.activation(id);
        if (res) {
            return ResponseEntity.ok().body("Регистрация успешно прошла");
        }
        return ResponseEntity.internalServerError().build();
    }
}
