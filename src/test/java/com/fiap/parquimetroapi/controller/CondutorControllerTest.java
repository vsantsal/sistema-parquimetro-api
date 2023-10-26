package com.fiap.parquimetroapi.controller;

import com.fiap.parquimetroapi.model.Condutor;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

    @DisplayName("Teste de detalhamento de condutor com id válido na API")
    @WithMockUser(username = "tester")
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
    @WithMockUser(username = "tester")
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