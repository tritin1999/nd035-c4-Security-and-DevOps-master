package com.example.demo;

import com.example.demo.controllers.CartController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
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
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CartControllerTest {
    @InjectMocks
    private CartController cartController;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CartRepository cartRepository;
    @Mock
    private ItemRepository itemRepository;

    private static final Logger log = LoggerFactory.getLogger("CartController.class");


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

    public Cart createMockCart(User user) {
        Cart cart = new Cart();

        cart.setUser(user);
        cart.setId(1L);

        List<Item> items = createMockItemList();

        cart.setItems(createMockItemList());
        cart.setTotal(items.stream().map(Item::getPrice).reduce(BigDecimal::add).get());
        return cart;
    }

    public User createMockUser() {
        User user = new User();

        user.setId(1L);
        user.setUsername("tinnt24");
        user.setPassword("123456789");
        user.setCart(createMockCart(user));

        return user;
    }

    @Before
    public void setUp() {
        when(userRepository.findByUsername("tinnt24")).thenReturn(createMockUser());
        when(itemRepository.findById(any())).thenReturn(Optional.of(createMockItem(1L)));
    }

    @Test
    public void addItemToCartTest() throws Exception {
        ModifyCartRequest cartRequest = new ModifyCartRequest();

        cartRequest.setUsername("tinnt24");
        cartRequest.setItemId(1L);
        cartRequest.setQuantity(10);

        ResponseEntity<Cart> responseEntity = cartController.addTocart(cartRequest);
        log.debug("Starting test case: addItemToCartTest");

        assertNotNull(responseEntity);
        assertEquals(200, responseEntity.getStatusCodeValue());

        Cart cart = responseEntity.getBody();

        assertNotNull(cart);
        assertEquals("tinnt24", cart.getUser().getUsername());
        assertEquals(createMockItem(1L), cart.getItems().get(0));

        Cart newCart = createMockCart(createMockUser());
        assertEquals(newCart.getItems().size() + cartRequest.getQuantity(), cart.getItems().size());

        Item item = createMockItem(cartRequest.getItemId());
        BigDecimal itemPrice = item.getPrice();

        assertEquals(item.getPrice().multiply(BigDecimal.valueOf(cartRequest.getQuantity())).add(newCart.getTotal()), cart.getTotal());
    }

    @Test
    public void removeItemFromCartTest() throws Exception {
        ModifyCartRequest cartRequest = new ModifyCartRequest();

        cartRequest.setUsername("tinnt24");
        cartRequest.setItemId(1);
        cartRequest.setQuantity(1);

        ResponseEntity<Cart> cartResponseEntity = cartController.removeFromcart(cartRequest);

        log.debug("Starting test case: removeItemFromCartTest");

        assertNotNull(cartResponseEntity);
        assertEquals(200, cartResponseEntity.getStatusCodeValue());

        Cart cart = cartResponseEntity.getBody();
        Cart CompareCart = createMockCart(createMockUser());

        assertNotNull(cart);

        Item item = createMockItem(cartRequest.getItemId());
        BigDecimal itemPrice = item.getPrice();
        BigDecimal expectTotal = CompareCart.getTotal().subtract(itemPrice.multiply(BigDecimal.valueOf(cartRequest.getQuantity())));

        assertEquals("tinnt24", cart.getUser().getUsername());
        assertEquals(CompareCart.getItems().size() - cartRequest.getQuantity(), cart.getItems().size());
        assertEquals(createMockItem(2), cart.getItems().get(0));
        assertEquals(expectTotal, cart.getTotal());

        verify(cartRepository, times(1)).save(cart);
    }
}
