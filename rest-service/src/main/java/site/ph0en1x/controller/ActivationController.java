package site.ph0en1x.controller;

import lombok.extern.log4j.Log4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import site.ph0en1x.service.UserActivationService;

@RestController
@Log4j
@RequestMapping("/user")
public class ActivationController {
    private final UserActivationService activationService;

    public ActivationController(UserActivationService activationService) {
        this.activationService = activationService;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/activation")
    public ResponseEntity<?> activation(@RequestParam("id") String id) {
        var res = activationService.activation(id);
        if (res) {
            return ResponseEntity.ok().body("Регистрация успешно прошла");
        }
        return ResponseEntity.badRequest().body("Ошибка в запросе");
    }
}
