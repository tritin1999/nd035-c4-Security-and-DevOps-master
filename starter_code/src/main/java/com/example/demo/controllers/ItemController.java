package com.example.demo.controllers;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;

@RestController
@RequestMapping("/api/item")
public class ItemController {

    @Autowired
    private ItemRepository itemRepository;

    private static final Logger log = LoggerFactory.getLogger(ItemController.class);

    @GetMapping
    public ResponseEntity<List<Item>> getItems() throws Exception {
        try {
            log.info("... Getting list all items");
            return ResponseEntity.ok(itemRepository.findAll());
        } catch (Exception e) {
            log.error("Error has been occurred");
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Item> getItemById(@PathVariable Long id) throws Exception {
        try {
            log.info("... Getting item by id: " + id);
            return ResponseEntity.of(itemRepository.findById(id));
        } catch (Exception e) {
            log.error("Error has been occurred");
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<List<Item>> getItemsByName(@PathVariable String name) throws Exception {
        try {
            log.info("... Getting items by name:" + name);
            List<Item> items = itemRepository.findByName(name);
            return items == null || items.isEmpty() ? ResponseEntity.notFound().build()
                    : ResponseEntity.ok(items);
        } catch (Exception e) {
            log.error("Error has been occurred");
            return ResponseEntity.badRequest().build();
        }

    }

}
