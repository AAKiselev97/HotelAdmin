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
public class UserControllerForAdmin {
    private final UserService userService;
    private static final Logger log = LogManager.getLogger(UserControllerForAdmin.class);

    public UserControllerForAdmin(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/admin/user")
    public ResponseEntity<List<UserPrintDto>> getAllUser() {
        List<UserPrintDto> userPrintDtoList = userService.getAllUserPrintDto();
        return new ResponseEntity<>(userPrintDtoList, HttpStatus.OK);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception e) {
        log.error(e);
        return new ResponseEntity<>("Exception message: " + e.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
