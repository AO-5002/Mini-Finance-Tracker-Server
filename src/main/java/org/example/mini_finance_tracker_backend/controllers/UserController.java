package org.example.mini_finance_tracker_backend.controllers;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.apache.coyote.Response;
import org.example.mini_finance_tracker_backend.dtos.UserDto;
import org.example.mini_finance_tracker_backend.entities.UserEntity;
import org.example.mini_finance_tracker_backend.mappings.UserMapper;
import org.example.mini_finance_tracker_backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;
import java.util.Optional;

@RestController
@RequestMapping(path = "/user/private", produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin(
        origins = "http://localhost:5173",
        allowedHeaders = "*",
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS}
)
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    @PostMapping
    public ResponseEntity<UserDto> createUser(@RequestBody UserDto newUser, Authentication auth){

        // Extract the user auth0_id from auth via getName()
        String userAuth0Id = auth.getName();
        var dtoToUser = userMapper.userDtoToUser(newUser);
        dtoToUser.setAuth0Id(userAuth0Id);
        userRepository.save(dtoToUser);
        return ResponseEntity.ok(userMapper.userToUserDto(dtoToUser));

    }




}