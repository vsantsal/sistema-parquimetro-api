package com.fiap.parquimetroapi.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FormaPagamentoTest {

    @DisplayName("Testa PIX somente pode ser utilizado no pagamento de estacionamentos com tipo de tempo fixo")
    @Test
    public void testCenario1(){
        // Arrange
        FormaPagamento formaPagamento = FormaPagamento.PIX;

        // Act
        List<TipoTempoEstacionado> tiposAceitos = formaPagamento.getTiposAceitosTempoEstacionado();

        // Assert
        assertEquals(1, tiposAceitos.size());
        assertIterableEquals(List.of(TipoTempoEstacionado.FIXO), tiposAceitos);
    }

    @DisplayName("Testa Cartao de credito pode ser utilizado no pagamento de estacionamentos com tipos de tempo fixo e variavel")
    @Test
    public void testCenario2(){
        // Arrange
        FormaPagamento formaPagamento = FormaPagamento.CARTAO_DE_CREDITO;

        // Act
        List<TipoTempoEstacionado> tiposAceitos = formaPagamento.getTiposAceitosTempoEstacionado();

        // Assert
        assertEquals(2, tiposAceitos.size());
        assertIterableEquals(List.of(
                                    TipoTempoEstacionado.FIXO,
                                    TipoTempoEstacionado.VARIAVEL
                                    ),
                            tiposAceitos);
    }

    @DisplayName("Testa debito pode ser utilizado no pagamento de estacionamentos com tipos de tempo fixo e variavel")
    @Test
    public void testCenario3(){
        // Arrange
        FormaPagamento formaPagamento = FormaPagamento.DEBITO;

        // Act
        List<TipoTempoEstacionado> tiposAceitos = formaPagamento.getTiposAceitosTempoEstacionado();

        // Assert
        assertEquals(2, tiposAceitos.size());
        assertIterableEquals(List.of(
                        TipoTempoEstacionado.FIXO,
                        TipoTempoEstacionado.VARIAVEL
                ),
                tiposAceitos);
    }

}