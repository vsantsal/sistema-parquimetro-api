package com.fiap.parquimetroapi.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record FormaPagamentoDTO(
        @NotBlank
        String tipo,

        List<String> tiposAceitosTempoEstacionado
) {
}
