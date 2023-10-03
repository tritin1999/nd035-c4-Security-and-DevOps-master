package com.example.demo.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartRepository cartRepository;

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserController(UserRepository userRepository, CartRepository cartRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.cartRepository = cartRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<User> findById(@PathVariable Long id) throws Exception {
        try {
            log.info("Get user with id: " + id);
            return ResponseEntity.of(userRepository.findById(id));
        } catch (Exception e) {
            log.error("Error has been occurred");
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{username}")
    public ResponseEntity<User> findByUserName(@PathVariable String username) throws Exception {
        try {
            log.info("Get user with username: " + username);
            User user = userRepository.findByUsername(username);
            return user == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(user);
        } catch (Exception e) {
            log.error("Error has been occurred");
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/create")
    public ResponseEntity<User> createUser(@RequestBody CreateUserRequest createUserRequest) throws Exception {
        try {

            log.info("=== Start creating user for  ===" + createUserRequest.getUsername());
            User user = new User();
            user.setUsername(createUserRequest.getUsername());

            log.info("... Start creating cart for " + createUserRequest.getUsername());
            Cart cart = new Cart();

            log.info("... Saving cart for " + createUserRequest.getUsername());
            cartRepository.save(cart);
            user.setCart(cart);

            if (createUserRequest.getPassword().length() < 7 || !createUserRequest.getPassword().equals(createUserRequest.getConfirmPassword())
            ) {
                log.error("Credentials is not valid (password not matched/ required greater than 6 characters) for ", createUserRequest.getUsername());
                return ResponseEntity.badRequest().build();
            }

            log.info("... Encrypting password");
            user.setPassword(bCryptPasswordEncoder.encode(createUserRequest.getPassword()));


            userRepository.save(user);
            log.info("... User " + createUserRequest.getUsername() + " has been created, thank you so much!");

            return ResponseEntity.ok(user);

        } catch (Exception ex) {
            log.error("Error has been occurred" + ex.getMessage());

            return ResponseEntity.badRequest().build();
        }
    }

}
