package com.example.demo;

import com.example.demo.controllers.OrderController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class OrderControllerTest {
    @InjectMocks
    private OrderController orderController;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private UserRepository userRepository;

    private static final Logger log = LoggerFactory.getLogger("OrderController.class");


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

    public List<UserOrder> createMockOrders() {
        List<UserOrder> orders = new ArrayList<>();

        for (int i = 0; i <= 2; i++) {
            UserOrder userOrder = new UserOrder();

            Cart cart = createMockCart(createMockUser());

            userOrder.setUser(createMockUser());
            userOrder.setId(Long.valueOf(i));
            userOrder.setItems(cart.getItems());
            userOrder.setTotal(cart.getTotal());

            orders.add(userOrder);
        }
        return orders;
    }

    @Before
    public void setup() {
        when(userRepository.findByUsername("tinnt24")).thenReturn(createMockUser());
        when(orderRepository.findByUser(any())).thenReturn(createMockOrders());
    }

    @Test
    public void submitOrderTest() throws Exception {
        ResponseEntity<UserOrder> res = orderController.submit("tinnt24");

        log.debug("Starting test case: submitOrderTest");

        assertNotNull(res);
        assertEquals(200, res.getStatusCodeValue());

        UserOrder order = res.getBody();

        assertNotNull(order);
        assertEquals(createMockItemList(), order.getItems());
        assertEquals(createMockUser().getId(), order.getUser().getId());
        assertEquals(createMockUser().getUsername(), order.getUser().getUsername());
        verify(orderRepository, times(1)).save(order);
    }

    @Test
    public void purchaseHistoryTest() throws Exception {
        ResponseEntity<List<UserOrder>> res =
                orderController.getOrdersForUser("tinnt24");

        log.debug("Starting test case: purchaseHistoryTest");

        assertNotNull(res);
        assertEquals(200, res.getStatusCodeValue());

        List<UserOrder> orderList = res.getBody();

        assertNotNull(orderList);
        assertEquals(createMockOrders().size(), orderList.size());
    }
}
