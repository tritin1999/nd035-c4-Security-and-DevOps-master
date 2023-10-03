package com.example.demo;

import com.example.demo.controllers.ItemController;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ItemControllerTest {
    @InjectMocks
    private ItemController itemController;

    @Mock
    private ItemRepository itemRepository;

    private static final Logger log = LoggerFactory.getLogger("ItemController.class");

    public Item createMockItem(long id) {
        Item item = new Item();

        item.setId(id);
        item.setPrice(BigDecimal.valueOf(id * 15));
        item.setName("Item " + item.getId());
        item.setDescription("Description: ");

        return item;
    }

    public List<Item> createMockItemList() {
        List<Item> items = new ArrayList<>();

        for (int i = 1; i <= 5; i++) {
            items.add(createMockItem(i));
        }
        return items;
    }


    @Before
    public void setup() {
        /* suppose to add one item with id = 1, 2, 3...  */
        when(itemRepository.findById(1L)).thenReturn(Optional.of(createMockItem(1)));
        when(itemRepository.findAll()).thenReturn(createMockItemList());
        when(itemRepository.findByName("item")).thenReturn(Arrays.asList(createMockItem(1), createMockItem(2), createMockItem(3), createMockItem(4), createMockItem(5)));
    }

    @Test
    public void getAllTest() throws Exception {
        ResponseEntity<List<Item>> res = itemController.getItems();

        assertNotNull(res);
        assertEquals(200, res.getStatusCodeValue());

        List<Item> resList = res.getBody();

        log.debug("Starting test case: getAllTest");

        assertNotNull(resList);
        assertEquals(createMockItemList(), resList);
        assertEquals(5, resList.size());
        verify(itemRepository, times(1)).findAll();
    }

    @Test
    public void getItemByIdTest() throws Exception {
        ResponseEntity<Item> res = itemController.getItemById(1L);

        assertNotNull(res);
        assertEquals(200, res.getStatusCodeValue());

        Item item = res.getBody();
        log.debug("Starting test case: getItemByIdTest");

        assertNotNull(item);
        assertEquals(createMockItem(1L), item);
        verify(itemRepository, times(1)).findById(1L);
    }

    @Test
    public void getItemsByNameTest() throws Exception {
        ResponseEntity<List<Item>> res = itemController.getItemsByName("item");

        assertNotNull(res);
        assertEquals(200, res.getStatusCodeValue());

        List<Item> resList = res.getBody();

        log.debug("Starting test case: getItemsByNameTest");

        assertNotNull(resList);
        assertEquals(createMockItemList(), resList);
        assertEquals(5, resList.size());
        verify(itemRepository, times(1)).findByName("item");
    }
}
