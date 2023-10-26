package com.fiap.parquimetroapi.controller;

import com.fiap.parquimetroapi.dto.CondutorDTO;
import com.fiap.parquimetroapi.model.Condutor;
import com.fiap.parquimetroapi.model.Usuario;
import com.fiap.parquimetroapi.repository.CondutorRepository;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class AuthenticationControllerTest {

    private Condutor condutor;
    private String ENDPOINT = "/auth/registrar";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CondutorRepository repository;

    @Autowired
    MongoTemplate mongoTemplate;

    @BeforeEach
    public void  setUp(){
        condutor = new CondutorDTO(
                "Fulano",
                "Endereço do Fulano",
                "fulano@email.com",
                "123456789",
                null
        ).toModel();
        condutor.setUsuario(new Usuario("fulano@email.com", "123456"));
    }
    @AfterEach
    public void tearDownDatabase(){
        mongoTemplate.getDb().drop();
    }

    @DisplayName("Teste de registro para condutor com mesmo e-mail registrado anteriormente retorna erro")
    @Test
    public void testCenario1() throws Exception {
        // Arrange
        repository.save(condutor);
        // Act
        this.mockMvc.perform(
                        post(ENDPOINT)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        "{\"condutor\":" +
                                                "{\"nome\": \"Ciclano\", " +
                                                "\"email\": \"fulano@email.com\", " +
                                                "\"endereco\": \"Endereço de Ciclano\", " +
                                                "\"telefone\": \"987654321\"}, " +
                                                "\"senha\": \"123456\"}}"

                                )
                )
                // Assert
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.mensagem",
                        Matchers.is("Condutor já cadastrado")));
    }

    @DisplayName("Teste de registro para novo condutor")
    @Test
    public void testCenario2() throws Exception {
        // Arrange
        this.mockMvc.perform(
                        post(ENDPOINT)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        "{\"condutor\":" +
                                                "{\"nome\": \"Ciclano\", " +
                                                "\"email\": \"ciclano@email.com\", " +
                                                "\"endereco\": \"Endereço de Ciclano\", " +
                                                "\"telefone\": \"987654321\"}, " +
                                                "\"senha\": \"123456\"}}"

                                )
                )
                // Assert
                .andExpect(status().isOk());
    }

    @DisplayName("Teste de cadastro de condutor com e-mail inválido retorna erro")
    @ParameterizedTest
    @CsvSource({"abc", "abc@", "@mail.com",
            "algum email@email.com",
            "email..invalido@email.com.br"})
    public void testCenario3(String email) throws Exception {
        // Arrange/Act
        this.mockMvc.perform(
                        post(ENDPOINT)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        "{\"condutor\":" +
                                                "{\"nome\": \"Ciclano\", " +
                                                "\"email\": \"" + email + "\", " +
                                                "\"endereco\": \"Endereço de Ciclano\", " +
                                                "\"telefone\": \"987654321\"}, " +
                                                "\"senha\": \"123456\"}}"
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
    public void testCenario4() throws Exception {
        // Arrange/Act
        this.mockMvc.perform(
                        post(ENDPOINT)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        "{\"condutor\":" +
                                                "{\"nome\": \"Ciclano\", " +
                                                "\"endereco\": \"Endereço de Ciclano\", " +
                                                "\"telefone\": \"987654321\"}, " +
                                                "\"senha\": \"123456\"}}"
                                )
                )

                // Assert
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$[0].campo",
                        Matchers.is("email")))
                .andExpect(jsonPath("$[0].mensagem",
                        Matchers.is("E-mail é obrigatório")));
    }


    @DisplayName("Teste de cadastro de condutor sem nome retorna erro")
    @Test
    public void testCenario5() throws Exception {
        // Arrange/Act
        this.mockMvc.perform(
                        post(ENDPOINT)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        "{\"condutor\":" +
                                                "{\"email\": \"ciclano@email.com\", " +
                                                "\"endereco\": \"Endereço de Ciclano\", " +
                                                "\"telefone\": \"987654321\"}, " +
                                                "\"senha\": \"123456\"}}"                               )
                )

                // Assert
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$[0].campo",
                        Matchers.is("nome")))
                .andExpect(jsonPath("$[0].mensagem",
                        Matchers.is("Nome é obrigatório")));
    }

    @DisplayName("Teste de cadastro de condutor sem telefone retorna erro")
    @Test
    public void testCenario6() throws Exception {
        // Arrange/Act
        this.mockMvc.perform(
                        post(ENDPOINT)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        "{\"condutor\":" +
                                                "{\"nome\": \"Ciclano\", " +
                                                "\"email\": \"ciclano@email.com\", " +
                                                "\"endereco\": \"Endereço de Ciclano\", " +
                                                "\"senha\": \"123456\"}}"                               )
                )

                // Assert
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$[0].campo",
                        Matchers.is("telefone")))
                .andExpect(jsonPath("$[0].mensagem",
                        Matchers.is("Telefone é obrigatório")));
    }

    @DisplayName("Teste de cadastro de condutor sem endereço retorna erro")
    @Test
    public void testCenario7() throws Exception {
        // Arrange/Act
        this.mockMvc.perform(
                        post(ENDPOINT)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        "{\"condutor\":" +
                                                "{\"nome\": \"Ciclano\", " +
                                                "\"email\": \"ciclano@email.com\", " +
                                                "\"telefone\": \"987654321\"}, " +
                                                "\"senha\": \"123456\"}}"                               )
                )

                // Assert
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$[0].campo",
                        Matchers.is("endereco")))
                .andExpect(jsonPath("$[0].mensagem",
                        Matchers.is("Endereço é obrigatório")));
    }

}