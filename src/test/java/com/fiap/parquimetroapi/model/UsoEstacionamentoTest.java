package com.fiap.parquimetroapi.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class UsoEstacionamentoTest {

    private UsoEstacionamento usoEstacionamento;

    private final Condutor condutorDefault = new Condutor();

    private final Estacionamento estacionamentoDefault = new Estacionamento(
            "71146289000108", 100L, BigDecimal.TEN);
    private final LocalDateTime inicioEstacionamentoDefault = LocalDateTime
            .of(2023, 10, 1, 10, 30, 15);

    @BeforeEach
    public void setUP(){
        usoEstacionamento = new UsoEstacionamento();
        usoEstacionamento.setCondutor(condutorDefault);
        usoEstacionamento.setEstacionamento(estacionamentoDefault);
        usoEstacionamento.setInicio(inicioEstacionamentoDefault);

    }

    @DisplayName("Teste monitoramento deve adicionar alerta para horário fixo faltando 10 minutos")
    @Test
    public void testCenario1() {
        // Arrange
        usoEstacionamento.setTipoTempoEstacionado(TipoTempoEstacionado.FIXO);
        usoEstacionamento.setDuracaoEsperada(Duration.ofHours(1L));
        LocalDateTime dataHoraFaltando10Minutos = LocalDateTime
                .of(2023, 10, 1, 11, 20, 15);

        // Act
        usoEstacionamento.monitora(dataHoraFaltando10Minutos);

        // Assert
        assertEquals(
                TipoTempoEstacionado.FIXO.getAlerta(),
                usoEstacionamento.getAlertasEmitidos().get(0));
    }

    @DisplayName("Teste monitoramento não deve adicionar alerta para horário fixo antes de 10 minutos")
    @Test
    public void testCenario2() {
        // Arrange
        usoEstacionamento.setTipoTempoEstacionado(TipoTempoEstacionado.FIXO);
        usoEstacionamento.setDuracaoEsperada(Duration.ofHours(3L));
        LocalDateTime dataHoraFaltandoMais10Minutos = LocalDateTime
                .of(2023, 10, 1, 13, 20, 14);

        // Act
        usoEstacionamento.monitora(dataHoraFaltandoMais10Minutos);

        // Assert
        assertEquals(0,
                usoEstacionamento.getAlertasEmitidos().size());
    }

    @DisplayName("Teste monitoramento deve adicionar alerta para horário variável faltando 10 minutos")
    @Test
    public void testCenario3() {
        // Arrange
        usoEstacionamento.setTipoTempoEstacionado(TipoTempoEstacionado.VARIAVEL);
        LocalDateTime dataHoraFaltando10Minutos = LocalDateTime
                .of(2023, 10, 1, 11, 20, 15);

        // Act
        usoEstacionamento.monitora(dataHoraFaltando10Minutos);

        // Assert
        assertEquals(
                TipoTempoEstacionado.VARIAVEL.getAlerta(),
                usoEstacionamento.getAlertasEmitidos().get(0));
    }

    @DisplayName("Teste monitoramento não deve adicionar alerta para horário variável faltando mais de 10 minutos")
    @Test
    public void testCenario4() {
        // Arrange
        usoEstacionamento.setTipoTempoEstacionado(TipoTempoEstacionado.VARIAVEL);
        LocalDateTime dataHoraFaltando10Minutos = LocalDateTime
                .of(2023, 10, 1, 11, 20, 15);

        // Act
        usoEstacionamento.monitora(dataHoraFaltando10Minutos);

        // Assert
        assertEquals(
                TipoTempoEstacionado.VARIAVEL.getAlerta(),
                usoEstacionamento.getAlertasEmitidos().get(0));
    }

}