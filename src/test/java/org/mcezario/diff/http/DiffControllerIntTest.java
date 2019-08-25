package org.mcezario.diff.http;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mcezario.diff.domains.ComparisonDetail;
import org.mcezario.diff.domains.DiffDetail;
import org.mcezario.diff.http.json.EncodedDataRequest;
import org.mcezario.diff.usecases.DiffAnalyser;
import org.mcezario.diff.usecases.exceptions.CalculateDifferenceException;
import org.mcezario.diff.usecases.exceptions.DiffNotFoundException;
import org.mcezario.diff.usecases.exceptions.RequiredSidesException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration
public class DiffControllerIntTest {

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
    public void shouldReturn_EQUALS_Successfully() throws Exception {
        // Prepare
        when(diffAnalyser.compare(anyString())).thenReturn(DiffDetail.equals());

        mockMvc.perform(get("/v1/diff/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result", is(ComparisonDetail.EQUALS.name())))
                .andExpect(jsonPath("$.similarity", is("100")))
                .andExpect(jsonPath("$.difference").doesNotExist());
    }

    @Test
    public void shouldReturn_DIFFERENT_SIZE_Successfully() throws Exception {
        // Prepare
        when(diffAnalyser.compare(anyString())).thenReturn(DiffDetail.differentSize("56.34"));

        mockMvc.perform(get("/v1/diff/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result", is(ComparisonDetail.DIFFERENT_SIZE.name())))
                .andExpect(jsonPath("$.similarity", is("56.34")))
                .andExpect(jsonPath("$.difference").doesNotExist());
    }

    @Test
    public void shouldReturn_DIFFERENT_CONTENT_Successfully() throws Exception {
        // Prepare
        when(diffAnalyser.compare(anyString())).thenReturn(DiffDetail.differentContent("96.34", "Differences..."));

        mockMvc.perform(get("/v1/diff/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result", is(ComparisonDetail.DIFFERENT_CONTENT.name())))
                .andExpect(jsonPath("$.similarity", is("96.34")))
                .andExpect(jsonPath("$.difference", is("Differences...")));
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
    public void shouldValidRequiredSidesBeforeComparison() throws Exception {
        // Prepare
        doThrow(new RequiredSidesException()).when(diffAnalyser).compare(anyString());

        mockMvc.perform(get("/v1/diff/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].code", is("003")))
                .andExpect(jsonPath("$[0].message", is("The left and right sides must be filled in to compare the difference.")));
    }

    @Test
    public void shouldReturnExceptionWhenOccursErrorOnDifferenceVerification() throws Exception {
        // Prepare
        doThrow(new CalculateDifferenceException()).when(diffAnalyser).compare(anyString());

        mockMvc.perform(get("/v1/diff/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].code", is("002")))
                .andExpect(jsonPath("$[0].message", is("Error to compare the difference between left and right side.")));
    }

    @Test
    public void shouldReturnExceptionWhenDiffIdDoesNotExists() throws Exception {
        // Prepare
        doThrow(new DiffNotFoundException()).when(diffAnalyser).compare(anyString());

        mockMvc.perform(get("/v1/diff/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].code", is("001")))
                .andExpect(jsonPath("$[0].message", is("Invalid Id.")));
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
