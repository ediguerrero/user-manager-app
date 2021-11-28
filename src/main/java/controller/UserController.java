package controller;


import data.UserRequest;
import entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import service.UserService;
import java.util.Objects;


@RestController
@RequestMapping("user")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity adUser(@RequestBody UserRequest userRequest) {

        if (userService.checkEmail(userRequest.getEmail())) {
            return new ResponseEntity("correo inválido", HttpStatus.BAD_REQUEST);
        }

        if (userService.checkPassword(userRequest.getPassword())) {
            return new ResponseEntity("password inválido", HttpStatus.BAD_REQUEST);
        }
        String response = userService.validateUserByEmail(userRequest.getEmail());
        if (Objects.nonNull(response)) {
            return new ResponseEntity(response, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity(userService.validateUser(userRequest), HttpStatus.OK);
    }


}
