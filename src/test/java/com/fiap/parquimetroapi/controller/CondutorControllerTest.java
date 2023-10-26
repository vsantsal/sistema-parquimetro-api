package com.fiap.parquimetroapi.controller;

import com.fiap.parquimetroapi.dto.CondutorDTO;
import com.fiap.parquimetroapi.model.Condutor;
import com.fiap.parquimetroapi.model.Usuario;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class CondutorControllerTest {

    private Condutor condutor;

    private final String ENDPOINT = "/condutores";

    @Autowired
    private MockMvc mockMvc;

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

    @DisplayName("Teste de detalhamento de condutor identifica infos para o id do próprio usuario")
    @Test
    public void testCenario1() throws Exception {
        // Arrange
        mongoTemplate.insert(condutor);

        // Act
        this.mockMvc.perform(
                get(ENDPOINT + "/" + condutor.getId())
                        .with(user(condutor.getUsuario()))
                )
        // Assert
            .andExpect(status().isOk())
                .andExpect(jsonPath("$.id",
                        Matchers.is(condutor.getId())))
                .andExpect(jsonPath("$.nome",
                        Matchers.is(condutor.getNome())))
                .andExpect(jsonPath("$.telefone",
                        Matchers.is(condutor.getTelefone())))
                .andExpect(jsonPath("$.email",
                        Matchers.is(condutor.getEmail())))
                .andExpect(jsonPath("$.endereco",
                        Matchers.is(condutor.getEndereco())));
    }

    @DisplayName("Teste de detalhamento de condutor nao visualiza outros ids na api")
    @Test
    public void testCenario2() throws Exception {
        // Arrange
        mongoTemplate.insert(condutor);

        // Act
        this.mockMvc.perform(
                        get(ENDPOINT + "/abc")
                                .with(user(condutor.getUsuario()))
                )

                // Assert
                .andExpect(status().isNotFound());
    }

}