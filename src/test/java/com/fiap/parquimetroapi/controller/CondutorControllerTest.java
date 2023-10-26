package com.fiap.parquimetroapi.controller;

import com.fiap.parquimetroapi.model.Condutor;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@Disabled
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
    @ParameterizedTest
    @CsvSource({"abc", "abc@", "@mail.com",
                "algum email@email.com",
                "email..invalido@email.com.br"})
    public void testCenario1(String email) throws Exception {
        // Arrange/Act
        this.mockMvc.perform(
                        post(ENDPOINT)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        "{\"nome\": \"Motorista X\", " +
                                                "\"endereco\": \"Avenida Y, 123\", " +
                                                "\"email\": \"" + email + "\", " +
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

    @DisplayName("Teste de cadastro de condutor sem nome retorna erro")
    @Test
    public void testCenario3() throws Exception {
        // Arrange/Act
        this.mockMvc.perform(
                        post(ENDPOINT)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        "{\"email\": \"motoristax@gospeed.com\", " +
                                                "\"endereco\": \"Avenida Y, 123\", " +
                                                "\"telefone\": \"5599123456789\"}"
                                )
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
    public void testCenario4() throws Exception {
        // Arrange/Act
        this.mockMvc.perform(
                        post(ENDPOINT)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        "{\"email\": \"motoristax@gospeed.com\", " +
                                                "\"endereco\": \"Avenida Y, 123\", " +
                                                "\"nome\": \"Motorista X\"}"
                                )
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
    public void testCenario5() throws Exception {
        // Arrange/Act
        this.mockMvc.perform(
                        post(ENDPOINT)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        "{\"email\": \"motoristax@gospeed.com\", " +
                                                "\"telefone\": \"5599123456789\", " +
                                                "\"nome\": \"Motorista X\"}"
                                )
                )

                // Assert
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$[0].campo",
                        Matchers.is("endereco")))
                .andExpect(jsonPath("$[0].mensagem",
                        Matchers.is("Endereço é obrigatório")));
    }

    @DisplayName("Teste de detalhamento de condutor com id válido na API")
    @Test
    public void testCenario6() throws Exception {
        // Arrange
        Condutor condutor = new Condutor();
        condutor.setNome("Motorista X");
        condutor.setEmail("motoristax@gospeed.com");
        condutor.setEndereco("Avenida Y, n 123");
        condutor.setTelefone("5599987654321");
        var condutorSalvo = mongoTemplate.insert(condutor);

        // Act
        this.mockMvc.perform(
                get(ENDPOINT + "/" + condutorSalvo.getId()))
        // Assert
            .andExpect(status().isOk())
                .andExpect(jsonPath("$.id",
                        Matchers.is(condutorSalvo.getId())))
                .andExpect(jsonPath("$.nome",
                        Matchers.is(condutorSalvo.getNome())))
                .andExpect(jsonPath("$.telefone",
                        Matchers.is(condutorSalvo.getTelefone())))
                .andExpect(jsonPath("$.email",
                        Matchers.is(condutorSalvo.getEmail())))
                .andExpect(jsonPath("$.endereco",
                        Matchers.is(condutorSalvo.getEndereco())));
    }

    @DisplayName("Teste de detalhamento de condutor com id inexistente na API")
    @Test
    public void testCenario7() throws Exception {
        // Act
        this.mockMvc.perform(
                        get(ENDPOINT + "/abc")
                )

                // Assert
                .andExpect(status().isNotFound());
    }

}