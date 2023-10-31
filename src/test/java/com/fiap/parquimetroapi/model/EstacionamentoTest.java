package com.fiap.parquimetroapi.model;

import com.fiap.parquimetroapi.exception.EstacionamentoLotadoException;
import com.fiap.parquimetroapi.exception.EstacionamentoSimultaneoException;
import com.fiap.parquimetroapi.exception.VeiculoNaoEstacionadoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/*
* CNPJs de teste gerados em https://www.4devs.com.br/gerador_de_cnpj
* */

class EstacionamentoTest {

    private Estacionamento sut;

    private Veiculo veiculo;

    @BeforeEach
    public void setSut(){
        this.sut = new Estacionamento("71146289000108", 100L, BigDecimal.TEN);
        this.veiculo = new Veiculo(Placa.criar("ABC1234"));

    }

    @DisplayName("Teste lotação atual decrementa conforme estacionamentos")
    @Test
    public void testCenario2(){
        // Arrange
        Long vagasDisponiveisInicial = this.sut.getVagasDisponiveis();

        // Act
        this.sut.estaciona(veiculo);

        // Assert
        assertEquals(
                vagasDisponiveisInicial - 1L,
                this.sut.getVagasDisponiveis()
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

    @DisplayName("Teste não é possível instanciar estacionamento com lotação máxima negativa")
    @Test
    public void testCenario4(){
        // Act
        assertThrows(
                IllegalArgumentException.class,
                () -> new Estacionamento(
                        "60365912000199", -1L, BigDecimal.ONE
                )
        );

    }

    @DisplayName("Teste não é possível instanciar estacionamento com lotação máxima nula")
    @Test
    public void testCenario5(){
        // Act
        assertThrows(
                IllegalArgumentException.class,
                () -> new Estacionamento(
                        "60365912000199", 0L, BigDecimal.ONE
                )
        );

    }


    @DisplayName("Teste não é possível estacionar mais carros que a lotação máxima")
    @Test
    public void testCenario6(){
        // Arrange
        Estacionamento estacionamentoSohComUmaVaga = new
                Estacionamento("71146289000108", 1L, BigDecimal.ONE);
        Veiculo veiculo1 = new Veiculo(Placa.criar("DEF5678"));
        Veiculo veiculo2 = new Veiculo(Placa.criar("XYZ1A23"));
        estacionamentoSohComUmaVaga.estaciona(veiculo1);

        // Act & Assert
        assertThrows(
                EstacionamentoLotadoException.class,
                () -> estacionamentoSohComUmaVaga.estaciona(veiculo2)
        );


    }

    @DisplayName("Teste ao liberar veículo estacionado quantidade de vagas disponíveis aumenta")
    @Test
    public void testCenario7(){
        // Arrange
        this.sut.estaciona(veiculo);

        // Act
        this.sut.libera(veiculo);

        // Assert
        assertEquals(this.sut.getLotacaoMaxima(),
                this.sut.getVagasDisponiveis());

    }

    @DisplayName("Teste não é possível liberar veículo não estacionado")
    @Test
    public void testCenario8(){

        // Act & Assert
        assertThrows(
                VeiculoNaoEstacionadoException.class,
                () -> this.sut.libera(veiculo)

        );

    }


}