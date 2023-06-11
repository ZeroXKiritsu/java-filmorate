package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.yandex.practicum.filmorate.controllers.FilmController;

@WebMvcTest(FilmController.class)
public class FilmControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testAddFilm_InvalidName() throws Exception {
        String filmJson = "{ \"name\": \"\", \"description\": \"Some description\", \"releaseDate\": \"2022-01-01\", \"duration\": 120 }";
        mockMvc.perform(MockMvcRequestBuilders.post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(filmJson))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void testAddFilm_InvalidDescription() throws Exception {
        String filmJson = "{ \"name\": \"Film Name\", \"description\": \"Lorem ipsum dolor sit amet, consectetur adipiscing elit. Morbi eget libero et mauris fermentum vulputate. Curabitur id nulla ullamcorper.\", \"releaseDate\": \"2022-01-01\", \"duration\": 120 }";
        mockMvc.perform(MockMvcRequestBuilders.post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(filmJson))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void testAddFilm_InvalidReleaseDate() throws Exception {
        String filmJson = "{ \"name\": \"Film Name\", \"description\": \"Some description\", \"releaseDate\": \"1800-01-01\", \"duration\": 120 }";
        mockMvc.perform(MockMvcRequestBuilders.post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(filmJson))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void testAddFilm_InvalidDuration() throws Exception {
        String filmJson = "{ \"name\": \"Film Name\", \"description\": \"Some description\", \"releaseDate\": \"2022-01-01\", \"duration\": -1 }";
        mockMvc.perform(MockMvcRequestBuilders.post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(filmJson))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
}
