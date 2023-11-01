package com.fiap.parquimetroapi.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VeiculoTest {

    private Veiculo veiculo;

    private final Placa placa = Placa.criar("ABC1234");

    @BeforeEach
    public void setUp(){
        this.veiculo = new Veiculo(placa);
    }

    @DisplayName("Testa inativação de veículo")
    @Test
    public void testCenario1(){
        // Act
        veiculo.inativa();

        // Assert
        assertFalse(veiculo.isAtivo());
        assertNotEquals(veiculo.getAtualizadoEm(), veiculo.getCriadoEm());
    }

    @DisplayName("Testa alteração de condutor")
    @Test
    public void testCenario2(){
        // Act
        veiculo.setCondutor(new Condutor());

        // Assert
        assertTrue(veiculo.isAtivo());
        assertNotNull(veiculo.getCondutor());
        assertNotEquals(veiculo.getAtualizadoEm(), veiculo.getCriadoEm());
    }

    @DisplayName("Testa veículos com mesma placa são iguais")
    @Test
    public void testCenario3(){
        // Arrange
        Veiculo outroVeiculo = new Veiculo(placa);

        // Act
        boolean saoIguais = outroVeiculo.equals(veiculo);

        // Assert
        assertTrue(saoIguais);
    }

}