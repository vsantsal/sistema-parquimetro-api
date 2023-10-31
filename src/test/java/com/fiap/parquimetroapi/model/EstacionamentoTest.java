package com.fiap.parquimetroapi.model;

import com.fiap.parquimetroapi.exception.EstacionamentoSimultaneoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class EstacionamentoTest {

    private Estacionamento sut;

    private Veiculo veiculo;

    @BeforeEach
    public void setSut(){
        this.sut = Estacionamento.getInstance("sut", 100L, BigDecimal.TEN);
        this.veiculo = new Veiculo(Placa.criar("ABC1234"));

    }

    @DisplayName("Teste não é possível instanciar dois estacionamentos distintos com mesmo cnpj")
    @Test
    public void testCenario1(){
        // Arrange
        this.sut.estaciona(veiculo);

        // Act
        Estacionamento outroEstacionamento = Estacionamento.getInstance(
                "sut", 101L, BigDecimal.ONE
        );

        // Assert
        assertEquals(
                this.sut.getCnpj(),outroEstacionamento.getCnpj()
        );
        assertEquals(
                this.sut.getLotacaoAtual(), outroEstacionamento.getLotacaoAtual()
        );
        assertEquals(
                this.sut.getLotacaoMaxima(), outroEstacionamento.getLotacaoMaxima()
        );
        assertEquals(
                this.sut.getValorHora(), outroEstacionamento.getValorHora()
        );

    }

    @DisplayName("Teste lotação atual decrementa conforme estacionamentos")
    @Test
    public void testCenario2(){
        // Arrange
        Long lotacaoAtualInicial = this.sut.getLotacaoAtual();

        // Act
        this.sut.estaciona(veiculo);

        // Assert
        assertEquals(
                lotacaoAtualInicial - 1L,
                this.sut.getLotacaoAtual()
        );

    }

    @DisplayName("Teste não é possível estacionar mesmo veículo ao mesmo tempo mais de uma vez")
    @Test
    public void testCenario3(){
        // Arrange
        this.sut.estaciona(veiculo);

        // Act & Assert
        assertThrows(
                EstacionamentoSimultaneoException.class,
                () -> this.sut.estaciona(veiculo)
        );


    }

}