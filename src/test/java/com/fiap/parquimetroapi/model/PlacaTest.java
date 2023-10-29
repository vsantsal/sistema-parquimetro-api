package com.fiap.parquimetroapi.model;

import com.fiap.parquimetroapi.exception.PlacaInvalidaException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class PlacaTest {

    @DisplayName("Teste deve permitir criação de placa com o padrão Mercosul")
    @ParameterizedTest
    @CsvSource({"ABC1D23"})
    public void testCenario1(String valor) {
        // Arrange
        Placa placa = Placa.criar(valor);
        // Act
        String valorObtido = placa.toString();
        // Assert
        assertEquals(valorObtido, valor);
    }

    @DisplayName("Teste deve permitir placa no padrão anterior")
    @ParameterizedTest
    @CsvSource({"ABC1234"})
    public void testCenario2(String valor) {
        // Arrange
        Placa placa = Placa.criar(valor);
        // Act
        String valorObtido = placa.toString();
        // Assert
        assertEquals(valorObtido, valor);
    }

    @DisplayName("Teste não deve permitir placa fora dos padrões vigentes")
    @ParameterizedTest
    @CsvSource({"AB12345", "ABCD123", "ABC123"})
    public void testCenario3(String valor) {
        // Arrange & Act & Assert
        assertThrows(
                PlacaInvalidaException.class,
                () -> Placa.criar(valor)
        );

    }

}