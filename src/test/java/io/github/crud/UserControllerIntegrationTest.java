package io.github.crud;

import io.github.crud.model.FuncRole;
import io.github.crud.model.IntegrationTests;
import io.github.crud.model.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Before
    public void setUp() throws Exception {
        restTemplate = restTemplate.withBasicAuth("admin", "admin");
    }

    @Test()
    @Category(IntegrationTests.class)
    public void getUsers() throws Exception {
        ResponseEntity<List<User>> responseEntity = restTemplate.exchange("/users", HttpMethod.GET, null, new ParameterizedTypeReference<List<User>>() {
        });
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    @Category(IntegrationTests.class)
    public void getUser() {
        ResponseEntity<User> responseEntity = restTemplate.getForEntity("/users/1", User.class);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    @Category(IntegrationTests.class)
    public void insertUser() throws Exception {
        User user = null;
        try {
            user = createUser(getTestUser("123@mail.com"));
        } finally {
            deleteUser(user);
        }
    }

    @Test
    @Category(IntegrationTests.class)
    public void updateUser() throws Exception {
        User user = null;
        try {
            user = createUser(getTestUser("125@mail.com"));
            user.setFirstName("different");
            restTemplate.exchange("/users", HttpMethod.PUT, new HttpEntity<>(user), new ParameterizedTypeReference<Void>() {
            });
            User updatedUser = restTemplate.getForObject("/users/" + user.getId(), User.class);
            assertEquals(user.getFirstName(), updatedUser.getFirstName());
        } finally {
            deleteUser(user);
        }
    }

    @Test
    @Category(IntegrationTests.class)
    public void insertUserWithEmptyPassword() throws Exception {
        User user = null;
        try {
            user = new User();
            user.setEmail("126@mail.com");
            user.setFirstName("first");
            user.setLastName("last");
            user.setFuncRoles(Arrays.asList(FuncRole.USER, FuncRole.DEVELOPER));
            user = createUser(user);
            assertNotEquals(null, user.getEmail());
        } finally {
            deleteUser(user);
        }
    }

    private User getTestUser(String email) {
        User user = new User();
        user.setEmail(email);
        user.setPassword("password");
        user.setFirstName("first");
        user.setLastName("last");
        user.setFuncRoles(Arrays.asList(FuncRole.USER, FuncRole.DEVELOPER));
        return user;
    }

    private User createUser(User user) {
        ResponseEntity<User> responseEntity = restTemplate.postForEntity("/users", user, User.class);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertTrue(responseEntity.getBody() != null);
        User responseUser = responseEntity.getBody();
        assertEquals(user.getEmail(), responseUser.getEmail());
        return responseUser;
    }

    private void deleteUser(User user) {
        if (user != null && user.getId() != null) {
            ResponseEntity<Void> responseEntity = restTemplate.exchange("/users/" + user.getId(), HttpMethod.DELETE,
                    null, new ParameterizedTypeReference<Void>() {
                    });
            assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        } else {
            throw new RuntimeException("Cannot delete user");
        }
    }
}
