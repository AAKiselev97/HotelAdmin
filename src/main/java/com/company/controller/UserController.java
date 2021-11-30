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
import java.util.List;

@Validated
@RestController
@RequestMapping()
public class UserController {
    private final UserService userService;
    private static final Logger log = LogManager.getLogger(UserController.class);

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/admin/user")
    public ResponseEntity<List<UserPrintDto>> getAllUser() {
        List<UserPrintDto> userPrintDtoList = userService.getAllUser();
        return userPrintDtoList != null
                ? new ResponseEntity<>(userPrintDtoList, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/user/registration")
    public ResponseEntity<?> createUser(@Valid @RequestBody UserDto user) {
        //действие - создать бронь
        log.info(String.format("Try create user with name[%s]", user.getName()));
        try {
            userService.saveUser(user);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (Exception e) {
            log.error(e);
            return new ResponseEntity<>(HttpStatus.NOT_MODIFIED);
        }
    }

    @PutMapping(value = "/admin/user/{id}")
    public ResponseEntity<?> updateUser(@PathVariable(name = "id") Integer userId, @Valid @RequestBody UserDto user) {
        //действие - обновить бронь
        log.info(String.format("Try create user with name[%s] ID[%d]", user.getName(), userId));
        try {
            userService.update(userId, user);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            log.error(e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping(value = "/admin/user/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable(name = "id") int id) {
        try {
            userService.delete(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            log.error(e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}