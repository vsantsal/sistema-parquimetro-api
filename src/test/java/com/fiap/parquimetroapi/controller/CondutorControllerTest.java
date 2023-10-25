package com.fiap.parquimetroapi.controller;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class CondutorControllerTest {

    private final String ENDPOINT = "/condutores";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    MongoTemplate mongoTemplate;

    @AfterEach
    public void tearDownDatabase(){
        mongoTemplate.getDb().drop();
    }

    @DisplayName("Teste de cadastro de condutor com e-mail inválido retorna erro")
    @Test
    public void testCenario1() throws Exception {
        // Arrange/Act
        this.mockMvc.perform(
                        post(ENDPOINT)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        "{\"nome\": \"Motorista X\", " +
                                                "\"endereco\": \"Avenida Y, 123\", " +
                                                "\"email\": \"abc\", " +
                                                "\"telefone\": \"5599123456789\"}"
                                )
                )

                // Assert
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$[0].campo",
                        Matchers.is("email")))
                .andExpect(jsonPath("$[0].mensagem",
                        Matchers.is("E-mail inválido")));
    }

    @DisplayName("Teste de cadastro de condutor sem e-mail retorna erro")
    @Test
    public void testCenario2() throws Exception {
        // Arrange/Act
        this.mockMvc.perform(
                        post(ENDPOINT)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        "{\"nome\": \"Motorista X\", " +
                                                "\"endereco\": \"Avenida Y, 123\", " +
                                                "\"telefone\": \"5599123456789\"}"
                                )
                )

                // Assert
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$[0].campo",
                        Matchers.is("email")))
                .andExpect(jsonPath("$[0].mensagem",
                        Matchers.is("E-mail é obrigatório")));
    }

}