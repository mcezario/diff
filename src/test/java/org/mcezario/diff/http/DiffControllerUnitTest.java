package org.mcezario.diff.http;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration
public class DiffControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void shouldReturnLeftSizeSuccessfully() throws Exception {
        mockMvc.perform(
                post("/v1/diff/1/left").contentType(MediaType.APPLICATION_JSON).content("{}")
        ).andExpect(status().isOk());
    }

    @Test
    public void shouldReturnRightSizeSuccessfully() throws Exception {
        mockMvc.perform(
                post("/v1/diff/1/right").contentType(MediaType.APPLICATION_JSON).content("{}")
        ).andExpect(status().isOk());
    }

}
