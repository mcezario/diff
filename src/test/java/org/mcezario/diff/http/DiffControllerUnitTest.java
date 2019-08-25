package org.mcezario.diff.http;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mcezario.diff.http.json.EncodedDataRequest;
import org.mcezario.diff.usecases.DiffAnalyser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration
public class DiffControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DiffAnalyser diffAnalyser;

    @Test
    public void shouldReturnLeftSizeSuccessfully() throws Exception {
        mockMvc.perform(post("/v1/diff/1/left").contentType(MediaType.APPLICATION_JSON).content(newValidEncodeInput()))
                .andExpect(status().isCreated());
    }

    @Test
    public void shouldReturnRightSizeSuccessfully() throws Exception {
        mockMvc.perform(post("/v1/diff/1/right").contentType(MediaType.APPLICATION_JSON).content(newValidEncodeInput()))
                .andExpect(status().isCreated());
    }

    @Test
    public void shouldValidateTheContentOfLeftSide() throws Exception {
        mockMvc.perform(post("/v1/diff/1/left").contentType(MediaType.APPLICATION_JSON).content(newInvalidEncodeInput()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].code", is("content")))
                .andExpect(jsonPath("$[0].message", is("The content must be a valid base64 format.")));
    }

    @Test
    public void shouldValidateTheContentOfRightSide() throws Exception {
        mockMvc.perform(post("/v1/diff/1/right").contentType(MediaType.APPLICATION_JSON).content(newInvalidEncodeInput()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].code", is("content")))
                .andExpect(jsonPath("$[0].message", is("The content must be a valid base64 format.")));
    }

    private static String newValidEncodeInput() {
        return toString(new EncodedDataRequest("ewoiY29kZSI6IDEKfQ=="));
    }

    private static String newInvalidEncodeInput() {
        return toString(new EncodedDataRequest("invalid"));
    }

    public static <T> String toString(T object) {
        final ObjectMapper jsonMapper = new ObjectMapper();

        try {
            return jsonMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }
}
