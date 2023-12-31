package com.fiap.parquimetroapi.controller;

import com.fiap.parquimetroapi.dto.CondutorDTO;
import com.fiap.parquimetroapi.model.Condutor;
import com.fiap.parquimetroapi.model.Placa;
import com.fiap.parquimetroapi.model.Usuario;
import com.fiap.parquimetroapi.model.Veiculo;
import com.fiap.parquimetroapi.repository.CondutorRepository;
import com.fiap.parquimetroapi.repository.VeiculoRepository;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class VeiculoControllerTest {

    private Condutor condutor;

    private final String ENDPOINT = "/veiculos";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    MongoTemplate mongoTemplate;
    @Autowired
    private CondutorRepository condutorRepository;

    @Autowired
    private VeiculoRepository veiculoRepository;

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


    @DisplayName("Teste de registro para novo veiculo com placa valida")
    @Test
    public void testCenario1() throws Exception {
        // Arrange
        condutorRepository.save(condutor);

        // Act
        this.mockMvc.perform(
                        post(ENDPOINT)
                                .with(user(condutor.getUsuario()))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        "{\"placa\": \"ABC1234\"}"

                                )
                )
                // Assert
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.placa",
                        Matchers.is("ABC1234")))
                .andExpect(jsonPath("$.condutorId",
                        Matchers.is(condutor.getId())))
        ;
    }

    @DisplayName("Teste de registro para novo veiculo com placa inválida")
    @Test
    public void testCenario2() throws Exception {
        // Arrange
        condutorRepository.save(condutor);

        // Act
        this.mockMvc.perform(
                        post(ENDPOINT)
                                .with(user(condutor.getUsuario()))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        "{\"placa\": \"ABC123\"}"

                                )
                )
                // Assert
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem",
                        Matchers.is("Valor incorreto de placa informado")))
        ;
    }

    @DisplayName("Teste de registro para veiculo com placa válida mas associado a outro condutor")
    @Test
    public void testCenario3() throws Exception {
        // Arrange
        String placaEmComum = "ABC1234";
        Condutor outroCondutor =  new CondutorDTO(
                "Ciclano",
                "Endereço do Ciclano",
                "ciclano@email.com",
                "987654321",
                null
        ).toModel();
        outroCondutor.setUsuario(
                new Usuario("username2", "654321"));
        condutorRepository.save(condutor);
        condutorRepository.save(outroCondutor);
        Veiculo veiculo = new Veiculo(Placa.criar(placaEmComum));
        veiculo.setCondutor(outroCondutor);
        veiculoRepository.save(veiculo);


        // Act
        this.mockMvc.perform(
                        post(ENDPOINT)
                                .with(user(condutor.getUsuario()))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        "{\"placa\": \"" + placaEmComum + "\"}"

                                )
                )
                // Assert
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.mensagem",
                        Matchers.is("Veículo já em uso na plataforma")))
        ;
    }


    @DisplayName("Exclusão de veiculo retorna status 204 se veiculo associado ao condutor")
    @Test
    public void testCenario4() throws Exception {
        // Arrange
        Veiculo veiculo = new Veiculo(Placa.criar("ABC1234"));
        veiculo.setCondutor(condutor);
        condutorRepository.save(condutor);
        veiculoRepository.save(veiculo);

        // Act
        this.mockMvc.perform(
                        delete(ENDPOINT + "/" + veiculo.getId())
                                .with(user(condutor.getUsuario()))
                )

                // Assert
                .andExpect(status().isNoContent());

        var veiculoAtualizado = veiculoRepository.findById(veiculo.getId());
        assertTrue(veiculoAtualizado.isPresent());
        assertFalse(veiculoAtualizado.get().isAtivo());
        assertNotEquals(veiculoAtualizado.get().getCriadoEm(), veiculoAtualizado.get().getAtualizadoEm());

    }

    @DisplayName("Exclusão de veiculo retorna status 404 se veiculo não associado ao condutor")
    @Test
    public void testCenario5() throws Exception {
        // Arrange
        Condutor outroCondutor =  new CondutorDTO(
                "Ciclano",
                "Endereço do Ciclano",
                "ciclano@email.com",
                "987654321",
                null
        ).toModel();
        outroCondutor.setUsuario(
                new Usuario("username2", "654321"));
        condutorRepository.save(condutor);
        condutorRepository.save(outroCondutor);
        Veiculo veiculo = new Veiculo(Placa.criar("ABC1234"));
        veiculo.setCondutor(outroCondutor);
        veiculoRepository.save(veiculo);


        // Act
        this.mockMvc.perform(
                        delete(ENDPOINT + "/" + veiculo.getId())
                                .with(user(condutor.getUsuario()))
                )

                // Assert
                .andExpect(status().isNotFound());

        var veiculoAtualizado = veiculoRepository.findById(veiculo.getId());
        assertTrue(veiculoAtualizado.isPresent());
        assertTrue(veiculoAtualizado.get().isAtivo());

    }

    @DisplayName("Listagem de veículos para repositório vazio")
    @Test
    public void testCenario6() throws Exception {
        // Arrange
        condutorRepository.save(condutor);

        // Act
        this.mockMvc.perform(
                        get(ENDPOINT)
                                .with(user(condutor.getUsuario()))
                )
                // Assert
                .andExpect(status().isNotFound());
    }

    @DisplayName("Listagem de veículos para repositório não vazio sem veículo associado ao condutor")
    @Test
    public void testCenario7() throws Exception {
        // Arrange
        Condutor outroCondutor =  new CondutorDTO(
                "Ciclano",
                "Endereço do Ciclano",
                "ciclano@email.com",
                "987654321",
                null
        ).toModel();
        outroCondutor.setUsuario(
                new Usuario("username2", "654321"));
        condutorRepository.save(condutor);
        condutorRepository.save(outroCondutor);
        Veiculo veiculo = new Veiculo(Placa.criar("ABC1234"));
        veiculo.setCondutor(outroCondutor);
        veiculoRepository.save(veiculo);

        // Act
        this.mockMvc.perform(
                        get(ENDPOINT)
                                .with(user(condutor.getUsuario()))
                )
                // Assert
                .andExpect(status().isNotFound());
    }

    @DisplayName("Listagem de veículos para repositório com 1 veículo associado ao condutor")
    @Test
    public void testCenario8() throws Exception {
        // Arrange
        condutorRepository.save(condutor);
        Veiculo veiculo = new Veiculo(Placa.criar("ABC1234"));
        veiculo.setCondutor(condutor);
        veiculoRepository.save(veiculo);

        // Act
        this.mockMvc.perform(
                        get(ENDPOINT)
                                .with(user(condutor.getUsuario()))
                )
                // Assert
                .andExpect(status().isOk())
                .andExpect(jsonPath("$",
                        Matchers.hasSize(1)))
                .andExpect(jsonPath("$[0].veiculoId",
                        Matchers.is(veiculo.getId())))
                .andExpect(jsonPath("$[0].condutorId",
                        Matchers.is(condutor.getId())))
                .andExpect(jsonPath("$[0].placa",
                        Matchers.is(veiculo.getPlaca().toString())))
        ;
    }

    @DisplayName("Listagem de veículos para repositório com mais veículos associados ao condutor")
    @Test
    public void testCenario9() throws Exception {
        // Arrange
        condutorRepository.save(condutor);
        // Veículo 1
        Veiculo veiculo1 = new Veiculo(Placa.criar("ABC1234"));
        veiculo1.setCondutor(condutor);
        veiculoRepository.save(veiculo1);

        // Veículo 2
        Veiculo veiculo2 = new Veiculo(Placa.criar("DEF5G78"));
        veiculo2.setCondutor(condutor);
        veiculoRepository.save(veiculo2);

        // Act
        this.mockMvc.perform(
                        get(ENDPOINT)
                                .with(user(condutor.getUsuario()))
                )
                // Assert
                .andExpect(status().isOk())
                .andExpect(jsonPath("$",
                        Matchers.hasSize(2)))
                .andExpect(jsonPath("$[0].veiculoId",
                        Matchers.is(veiculo1.getId())))
                .andExpect(jsonPath("$[0].condutorId",
                        Matchers.is(condutor.getId())))
                .andExpect(jsonPath("$[0].placa",
                        Matchers.is(veiculo1.getPlaca().toString())))
                .andExpect(jsonPath("$[1].veiculoId",
                        Matchers.is(veiculo2.getId())))
                .andExpect(jsonPath("$[1].condutorId",
                        Matchers.is(condutor.getId())))
                .andExpect(jsonPath("$[1].placa",
                        Matchers.is(veiculo2.getPlaca().toString())))
        ;
    }

    @DisplayName("Detalhamento de veículo para repositório vazio")
    @Test
    public void testCenario10() throws Exception {
        // Arrange
        condutorRepository.save(condutor);

        // Act
        this.mockMvc.perform(
                        get(ENDPOINT + "/abc")
                                .with(user(condutor.getUsuario()))
                )
                // Assert
                .andExpect(status().isNotFound());
    }

    @DisplayName("Detalhamento de veículo não associado ao condutor")
    @Test
    public void testCenario11() throws Exception {
        // Arrange
        Condutor outroCondutor =  new CondutorDTO(
                "Ciclano",
                "Endereço do Ciclano",
                "ciclano@email.com",
                "987654321",
                null
        ).toModel();
        outroCondutor.setUsuario(
                new Usuario("username2", "654321"));
        condutorRepository.save(condutor);
        condutorRepository.save(outroCondutor);
        Veiculo veiculo = new Veiculo(Placa.criar("ABC1234"));
        veiculo.setCondutor(outroCondutor);
        veiculoRepository.save(veiculo);

        // Act
        this.mockMvc.perform(
                        get(ENDPOINT + "/" + veiculo.getId())
                                .with(user(condutor.getUsuario()))
                )
                // Assert
                .andExpect(status().isNotFound());
    }

    @DisplayName("Detalhamento de veículo associado ao condutor")
    @Test
    public void testCenario12() throws Exception {
        // Arrange
        condutorRepository.save(condutor);
        Veiculo veiculo = new Veiculo(Placa.criar("ABC1234"));
        veiculo.setCondutor(condutor);
        veiculoRepository.save(veiculo);

        // Act
        this.mockMvc.perform(
                        get(ENDPOINT + "/" + veiculo.getId())
                                .with(user(condutor.getUsuario()))
                )
                // Assert
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.veiculoId",
                        Matchers.is(veiculo.getId())))
                .andExpect(jsonPath("$.condutorId",
                        Matchers.is(condutor.getId())))
                .andExpect(jsonPath("$.placa",
                        Matchers.is(veiculo.getPlaca().toString())))
                ;
    }

}