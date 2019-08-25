package org.mcezario.diff.http;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = WebConfig.class)
public class ControllerExceptionHandlerTest {

  @Autowired
  private WebApplicationContext wac;

  private MockMvc mockMvc;

  @Before
  public void setup () {
      this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
  }
  
  @Test
  public void shouldValidateBean() throws Exception {
    
    this.mockMvc.perform(
        post("/api/test").contentType(MediaType.APPLICATION_JSON).content("{}")
      )
    .andExpect(status().isBadRequest())
    .andExpect(jsonPath("$", hasSize(4)))
    .andExpect(jsonPath("$[0].code", is("age")))
    .andExpect(jsonPath("$[0].message", is("Age should not be less than 18")))
    .andExpect(jsonPath("$[1].code", is("brand")))
    .andExpect(jsonPath("$[1].message", is("must not be null")))
    .andExpect(jsonPath("$[2].code", is("id")))
    .andExpect(jsonPath("$[2].message", is("must not be null")))
    .andExpect(jsonPath("$[3].code", is("name")))
    .andExpect(jsonPath("$[3].message", is("must not be empty")));

  }
  
  @Test
  public void shouldReturnBusinessException() throws Exception {
    
    this.mockMvc.perform(
        post("/api/test").contentType(MediaType.APPLICATION_JSON).content("{ \"id\": \"1\", \"name\": \"Jose\", \"age\": 20, \"brand\": \"VISA\" }")
      )
    .andExpect(status().isUnprocessableEntity())
    .andExpect(jsonPath("$", hasSize(1)))
    .andExpect(jsonPath("$[0].code", is("test.error")))
    .andExpect(jsonPath("$[0].message", is("Test message error")));
  }
  
  @Test
  public void shouldReturnInternalErrorException() throws Exception {
    
    this.mockMvc.perform(get("/api/test"))
    .andExpect(status().isInternalServerError())
    .andExpect(jsonPath("$", hasSize(1)))
    .andExpect(jsonPath("$[0].code", is("INTERNAL_SERVER_ERROR")))
    .andExpect(jsonPath("$[0].message", is("Internal Server Error")));
  }

  @Test
  public void shouldValidateHeader() throws Exception {

    this.mockMvc.perform(
            get("/api/test/header"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].code", is("missingParam")))
            .andExpect(jsonPath("$[0].message", is("Missing request header 'validateHeader' for method parameter of type String")));
  }

  @Test
  public void shouldValidateRequestParam() throws Exception {

    this.mockMvc.perform(
            get("/api/test/request"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].code", is("missingParam")))
            .andExpect(jsonPath("$[0].message", is("Required String parameter 'validateRequest' is not present")));
  }
  
}

@EnableWebMvc
@Configuration
class WebConfig {
  
  @Bean
  public TestController testController () {
    return new TestController();
  }
  
  @Bean
  public ControllerExceptionHandler controllerExceptionHandler() {
    return new ControllerExceptionHandler();
  }
  
}


