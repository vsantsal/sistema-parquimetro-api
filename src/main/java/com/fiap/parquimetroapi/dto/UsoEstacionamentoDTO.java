package com.fiap.parquimetroapi.dto;

import com.fiap.parquimetroapi.model.UsoEstacionamento;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.br.CNPJ;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;

public record UsoEstacionamentoDTO (
    @NotBlank
    String placaVeiculo,

    @NotBlank
    @CNPJ
    String cnpjEstacionamento,

    @NotBlank
    String tipoTempoEstacionado,

    @NotNull
    LocalDateTime inicio,

    Duration duracao,

    LocalDateTime fim,

    BigDecimal total,

    String id


){

    public UsoEstacionamentoDTO(UsoEstacionamento uso) {
        this(
                uso.getVeiculo().getPlaca().toString(),
                uso.getEstacionamento().getCnpj(),
                uso.getTipoTempoEstacionado().toString(),
                uso.getInicio(),
                uso.getDuracaoEsperada(),
                uso.getFim(),
                uso.getValorDevido(),
                uso.getId()
        );
    }
}
