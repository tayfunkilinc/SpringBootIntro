package com.tpe.controller;

import com.tpe.dto.UserDto;
import com.tpe.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    //user'i kaydetme:register
    //request:  http://localhost:8080/register + POST + body
    //response : mesaj + 201
    @RequestMapping("/register")
    @PostMapping
    public ResponseEntity<String> registerUser(@Valid @RequestBody UserDto userDto){
        userService.saveUser(userDto);
        return new ResponseEntity<>("User is registered succesfully...", HttpStatus.CREATED);
    }


}
