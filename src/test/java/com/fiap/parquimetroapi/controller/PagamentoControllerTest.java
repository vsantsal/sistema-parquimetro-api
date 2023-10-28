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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class PagamentoControllerTest {

    private Condutor condutor;

    private final String ENDPOINT_FORMA_PGTO = "/pagamentos/forma";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    MongoTemplate mongoTemplate;
    @Autowired
    private CondutorRepository condutorRepository;

    @BeforeEach
    public void  setUp(){
        condutor = new CondutorDTO(
                "Fulano",
                "Endereço do Fulano",
                "fulano@email.com",
                "123456789",
                null
        ).toModel();
        condutor.setUsuario(new Usuario("fulano", "123456"));
    }

    @AfterEach
    public void tearDownDatabase(){
        mongoTemplate.getDb().drop();
    }

    @DisplayName("Testa registro de forma de pagamento valida para condutor")
    @Test
    public void testCenario1() throws Exception {
        // Arrange
        condutorRepository.save(condutor);

        // Act
        this.mockMvc.perform(
                post(ENDPOINT_FORMA_PGTO)
                        .with(user(condutor.getUsuario()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                "{\"tipo\": \"DEBITO\"}"
                        )
        )
        // Assert
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tipo",
                        Matchers.is("DEBITO")))
                ;

    }

    @DisplayName("Testa registro de forma de pagamento invalida para condutor")
    @Test
    public void testCenario2() throws Exception {
        // Arrange
        condutorRepository.save(condutor);

        // Act
        this.mockMvc.perform(
                        post(ENDPOINT_FORMA_PGTO)
                                .with(user(condutor.getUsuario()))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        "{\"tipo\": \"INVALIDO\"}"
                                )
                )
                // Assert
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem",
                        Matchers.is("Forma de pagamento 'INVALIDO' inválida")))
        ;

    }

    @DisplayName("Testa registro de forma de pagamento sem corpo de requisição")
    @Test
    public void testCenario3() throws Exception {
        // Arrange
        condutorRepository.save(condutor);

        // Act
        this.mockMvc.perform(
                        post(ENDPOINT_FORMA_PGTO)
                                .with(user(condutor.getUsuario())))
                // Assert
                .andExpect(status().isBadRequest())
        ;

    }

}