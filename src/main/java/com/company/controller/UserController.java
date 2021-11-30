package com.company.controller;

import com.company.dto.user.UserDto;
import com.company.dto.user.UserPrintDto;
import com.company.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Validated
@RestController
@RequestMapping
public class UserController {
    private final UserService userService;
    private static final Logger log = LogManager.getLogger(UserController.class);

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/user")
    public ResponseEntity<UserPrintDto> getUser() {
        UserPrintDto user = userService.getCurrentUserForPrint();
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<?> updateUser(@PathVariable(name = "id") Integer userId, @Valid @RequestBody UserDto user) {
        //действие - обновить бронь
        log.info(String.format("Try create user with name[%s] ID[%d]", user.getName(), userId));
        userService.update(userId, user);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/successLogout")
    public ResponseEntity<String> logout() {
        return new ResponseEntity<>("Logout success", HttpStatus.OK);
    }

    @PostMapping("/user/registration")
    public ResponseEntity<?> createUser(@Valid @RequestBody UserDto user) {
        //действие - создать бронь
        log.info(String.format("Try create user with name[%s]", user.getName()));
        userService.saveUser(user);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @DeleteMapping(value = "/admin/user/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable(name = "id") int id) {
        userService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception e) {
        log.error(e);
        return new ResponseEntity<>("Exception message: " + e.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
