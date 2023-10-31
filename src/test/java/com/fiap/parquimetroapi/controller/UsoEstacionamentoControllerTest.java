package com.fiap.parquimetroapi.controller;

import com.fiap.parquimetroapi.dto.CondutorDTO;
import com.fiap.parquimetroapi.model.*;
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

import java.time.LocalDateTime;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class UsoEstacionamentoControllerTest {

    private Condutor condutor;

    private Veiculo veiculo;

    private final String CNPJ_ESTACIONAMENTO = "71146289000108";

    private final String RAIZ_ENDPOINT = "/estacionamentos";
    private final String SUFIXO_ENDPOINT_INICIO_REGISTRO = "/iniciar-registro";

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

        veiculo = new Veiculo(Placa.criar("ABC1234"));

    }

    @AfterEach
    public void tearDownDatabase(){
        mongoTemplate.getDb().drop();
    }

    @DisplayName("Testa registro válido de início de estacionamento cria recurso")
    @Test
    public void testCenario1() throws Exception {
        // Arrange
        condutor.setFormaPagamento(FormaPagamento.CARTAO_DE_CREDITO);
        veiculoRepository.save(veiculo);
        condutor.associa(veiculo);
        condutorRepository.save(condutor);

        // Act
        this.mockMvc.perform(
                        post(RAIZ_ENDPOINT + SUFIXO_ENDPOINT_INICIO_REGISTRO)
                                .with(user(condutor.getUsuario()))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        "{\"placaVeiculo\": \"" + veiculo.getPlaca() +"\" ," +
                                        "\"cnpjEstacionamento\": \"" + CNPJ_ESTACIONAMENTO +"\" ," +
                                                "\"tipoTempoEstacionado\": \"VARIAVEL\" ," +
                                                "\"inicio\": \"" + LocalDateTime.now() +"\"}"
                                )
                )
                // Assert
                .andExpect(status().isCreated())

        ;

    }

    @DisplayName("Testa não é possível iniciar estacionamento sem forma de pagamento preferida")
    @Test
    public void testCenario2() throws Exception {
        // Arrange
        veiculoRepository.save(veiculo);
        condutor.associa(veiculo);
        condutorRepository.save(condutor);

        // Act
        this.mockMvc.perform(
                        post(RAIZ_ENDPOINT + SUFIXO_ENDPOINT_INICIO_REGISTRO)
                                .with(user(condutor.getUsuario()))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        "{\"placaVeiculo\": \"" + veiculo.getPlaca() +"\" ," +
                                                "\"cnpjEstacionamento\": \"" + CNPJ_ESTACIONAMENTO +"\" ," +
                                                "\"tipoTempoEstacionado\": \"VARIAVEL\" ," +
                                                "\"inicio\": \"" + LocalDateTime.now() +"\"}"
                                )
                )
                // Assert
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem",
                        Matchers.is("É necessário selecionar forma de pagamento válida antes de estacionar")))
        ;

    }

    @DisplayName("Testa não é possível estacionar para tipo período inválido na forma de pagamento")
    @Test
    public void testCenario3() throws Exception {
        // Arrange
        condutor.setFormaPagamento(FormaPagamento.PIX);
        veiculoRepository.save(veiculo);
        condutor.associa(veiculo);
        condutorRepository.save(condutor);

        // Act
        this.mockMvc.perform(
                        post(RAIZ_ENDPOINT + SUFIXO_ENDPOINT_INICIO_REGISTRO)
                                .with(user(condutor.getUsuario()))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        "{\"placaVeiculo\": \"" + veiculo.getPlaca() +"\" ," +
                                                "\"cnpjEstacionamento\": \"" + CNPJ_ESTACIONAMENTO +"\" ," +
                                                "\"tipoTempoEstacionado\": \"VARIAVEL\" ," +
                                                "\"inicio\": \"" + LocalDateTime.now() +"\"}"
                                )
                )
                // Assert
                .andExpect(status().isBadRequest())

        ;

    }

    @DisplayName("Testa não é possível estacionar para tipo período inexistente")
    @Test
    public void testCenario4() throws Exception {
        // Arrange
        condutor.setFormaPagamento(FormaPagamento.PIX);
        veiculoRepository.save(veiculo);
        condutor.associa(veiculo);
        condutorRepository.save(condutor);

        // Act
        this.mockMvc.perform(
                        post(RAIZ_ENDPOINT + SUFIXO_ENDPOINT_INICIO_REGISTRO)
                                .with(user(condutor.getUsuario()))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        "{\"placaVeiculo\": \"" + veiculo.getPlaca() +"\" ," +
                                                "\"cnpjEstacionamento\": \"" + CNPJ_ESTACIONAMENTO +"\" ," +
                                                "\"tipoTempoEstacionado\": \"INEXISTENTE\" ," +
                                                "\"inicio\": \"" + LocalDateTime.now() +"\"}"
                                )
                )
                // Assert
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem",
                        Matchers.is("Valor 'INEXISTENTE' inválido para 'tipoTempoEstacionado'")))


        ;

    }

    @DisplayName("Testa não é possível iniciar estacionamento com veículo não associado")
    @Test
    public void testCenario5() throws Exception {
        // Arrange
        condutor.setFormaPagamento(FormaPagamento.DEBITO);
        veiculoRepository.save(veiculo);
        condutorRepository.save(condutor);

        // Act
        this.mockMvc.perform(
                        post(RAIZ_ENDPOINT + SUFIXO_ENDPOINT_INICIO_REGISTRO)
                                .with(user(condutor.getUsuario()))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        "{\"placaVeiculo\": \"" + veiculo.getPlaca() +"\" ," +
                                                "\"cnpjEstacionamento\": \"" + CNPJ_ESTACIONAMENTO +"\" ," +
                                                "\"tipoTempoEstacionado\": \"VARIAVEL\" ," +
                                                "\"inicio\": \"" + LocalDateTime.now() +"\"}"
                                )
                )
                // Assert
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem",
                        Matchers.is(
                                "Não foi possível localizar o veículo correspondente à placa '" +
                                veiculo.getPlaca() + "'")))
        ;

    }

    @DisplayName("Testa não é possível selecionar período fixo sem informar duração")
    @Test
    public void testCenario6() throws Exception {
        // Arrange
        condutor.setFormaPagamento(FormaPagamento.PIX);
        veiculoRepository.save(veiculo);
        condutor.associa(veiculo);
        condutorRepository.save(condutor);

        // Act
        this.mockMvc.perform(
                        post(RAIZ_ENDPOINT + SUFIXO_ENDPOINT_INICIO_REGISTRO)
                                .with(user(condutor.getUsuario()))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        "{\"placaVeiculo\": \"" + veiculo.getPlaca() +"\" ," +
                                                "\"cnpjEstacionamento\": \"" + CNPJ_ESTACIONAMENTO +"\" ," +
                                                "\"tipoTempoEstacionado\": \"FIXO\" ," +
                                                "\"inicio\": \"" + LocalDateTime.now() +"\"}"
                                )
                )
                // Assert
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem",
                        Matchers.is("Necessário informar duração para tipoTempoEstacionado 'FIXO'")))
        ;

    }

}