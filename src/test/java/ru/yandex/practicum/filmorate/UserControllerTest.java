package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.yandex.practicum.filmorate.controllers.UserController;

@WebMvcTest(UserController.class)
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testCreateUser_InvalidEmail() throws Exception {
        String userJson = "{ \"email\": \"invalid_email\", \"login\": \"user_login\", \"name\": \"User Name\", \"birthday\": \"1990-01-01\" }";
        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void testCreateUser_InvalidLogin() throws Exception {
        String userJson = "{ \"email\": \"user@example.com\", \"login\": \"user login\", \"name\": \"User Name\", \"birthday\": \"1990-01-01\" }";
        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void testCreateUser_InvalidBirthday() throws Exception {
        String userJson = "{ \"email\": \"user@example.com\", \"login\": \"user_login\", \"name\": \"User Name\", \"birthday\": \"2100-01-01\" }";
        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
}
