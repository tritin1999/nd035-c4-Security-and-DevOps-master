package com.example.demo;

import com.example.demo.controllers.UserController;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserControllerTest {
    private UserController userController;
    private final UserRepository userRepository = mock(UserRepository.class);
    private final CartRepository cartRepository = mock(CartRepository.class);
    private final BCryptPasswordEncoder encoder = mock(BCryptPasswordEncoder.class);
    private static final Logger log = LoggerFactory.getLogger("UserController.class");

    @Before
    public void setUp() {
        userController = new UserController();
        InitialClass.setUp(userController, "userRepository", userRepository);
        InitialClass.setUp(userController, "cartRepository", cartRepository);
        InitialClass.setUp(userController, "bCryptPasswordEncoder", encoder);
    }

    @Test
    public void createUserTest() throws Exception {
        /* Fake hashed password */
        when(encoder.encode("123456789")).thenReturn("tinnt24Password");

        CreateUserRequest createUserRequest = new CreateUserRequest();

        createUserRequest.setUsername("tinnt24");
        createUserRequest.setPassword("123456789");
        createUserRequest.setConfirmPassword("123456789");

        final ResponseEntity<User> response = userController.createUser(createUserRequest);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        User user = response.getBody();
        log.debug("Starting test case: createUserTest");

        assertNotNull(user);
        assertEquals(0, user.getId());
        assertEquals("tinnt24", user.getUsername());
        assertEquals("tinnt24Password", user.getPassword());
    }

    @Test
    public void getUserByIdTest() throws Exception {
        long id = 1L;

        User user = new User();
        user.setUsername("tinnt24");
        user.setPassword("123456789");
        user.setId(id);

        /* Fake data repsonse */

        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        ResponseEntity<User> responseEntity = userController.findById(id);

        assertNotNull(responseEntity);
        assertEquals(200, responseEntity.getStatusCodeValue());

        User userRes = responseEntity.getBody();

        log.debug("Starting test case: getUserByIdTest");

        assertNotNull(userRes);
        assertEquals(id, userRes.getId());
        assertEquals("tinnt24", userRes.getUsername());
        assertEquals("123456789", userRes.getPassword());
    }

    @Test
    public void getUserByName() throws  Exception {
        User user = new User();
        long id = 1L;

        user.setUsername("tinnt24");
        user.setPassword("123456789");
        user.setId(id);

        when(userRepository.findByUsername("tinnt24")).thenReturn(user);
        ResponseEntity<User> responseEntity = userController.findByUserName("tinnt24");

        assertNotNull(responseEntity);
        assertEquals(200, responseEntity.getStatusCodeValue());

        User userRes = responseEntity.getBody();

        log.debug("Starting test case: getUserByName");

        assertNotNull(userRes);
        assertEquals(id, userRes.getId());
        assertEquals("tinnt24", userRes.getUsername());
        assertEquals("123456789", userRes.getPassword());
    }
}
