package com.fiap.parquimetroapi.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class TipoTempoEstacionadoTest {

    @DisplayName("Teste de alertas conforme tipo periodo")
    @ParameterizedTest
    @CsvSource({
            "FIXO, Tempo de estacionamento prestes a expirar. Estacionamento será encerrado automaticamente.",
            "VARIAVEL, Tempo de estacionamento prestes a expirar. Estacionamento será estendido automaticamente caso não seja encerrado."
    })

    public void testCenario1(String tipoTempo, String alertaEsperado){
        // Arrange
        TipoTempoEstacionado tipoTempoEstacionado = TipoTempoEstacionado.valueOf(tipoTempo);

        // Act
        String alertaObtido = tipoTempoEstacionado.getAlerta();

        // Assert
        assertEquals(alertaEsperado, alertaObtido);

    }

}