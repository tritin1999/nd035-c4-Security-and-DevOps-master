package com.example.demo.controllers;

import java.util.Optional;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ItemRepository itemRepository;

    private static final Logger log = LoggerFactory.getLogger(CartController.class);

    @PostMapping("/addToCart")
    public ResponseEntity<Cart> addTocart(@RequestBody ModifyCartRequest request) throws Exception {
        try {
            log.info("... Getting user by username: " + request.getUsername());
            User user = userRepository.findByUsername(request.getUsername());

            if (user == null) {
                log.error("User is not found...");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            log.info("... Getting item has been requested to add to cart: ");
            Optional<Item> item = itemRepository.findById(request.getItemId());

            if (!item.isPresent()) {
                log.error("Cart items are empty to add ...");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            log.info("... Getting cart has been requested");
            Cart cart = user.getCart();

            log.info("... Processing ");
            IntStream.range(0, request.getQuantity())
                    .forEach(i -> cart.addItem(item.get()));

            cartRepository.save(cart);
            log.info("Cart item has been created successfully!");

            return ResponseEntity.ok(cart);
        } catch (Exception e) {
            log.error("Error has been occurred");
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/removeFromCart")
    public ResponseEntity<Cart> removeFromcart(@RequestBody ModifyCartRequest request) throws Exception {
        try {
            log.info("... Getting user by username: " + request.getUsername());
            User user = userRepository.findByUsername(request.getUsername());

            if (user == null) {
                log.error("User is not found...");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            log.info("... Getting item has been requested to remove from the cart: ");
            Optional<Item> item = itemRepository.findById(request.getItemId());

            if (!item.isPresent()) {
                log.error("Cart items are empty to remove ...");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            log.info("... Getting cart has been requested");
            Cart cart = user.getCart();

            log.info("... Processing ");
            IntStream.range(0, request.getQuantity())
                    .forEach(i -> cart.removeItem(item.get()));

            cartRepository.save(cart);
            log.info("Cart item has been removed successfully!");

            return ResponseEntity.ok(cart);
        } catch (Exception e) {
            log.error("Error has been occurred");
            return ResponseEntity.badRequest().build();
        }
    }

}
