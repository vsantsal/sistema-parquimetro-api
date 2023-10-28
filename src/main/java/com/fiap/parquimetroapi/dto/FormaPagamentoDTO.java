package com.fiap.parquimetroapi.dto;

import com.fiap.parquimetroapi.model.Condutor;
import com.fiap.parquimetroapi.model.FormaPagamento;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record FormaPagamentoDTO(
        @NotBlank
        String tipo,

        List<String> tiposAceitosTempoEstacionado
) {
        public FormaPagamentoDTO(Condutor condutor) {
                this(condutor.getFormaPagamento().toString(),
                        condutor.getFormaPagamento()
                                .getTiposAceitosTempoEstacionado()
                                .stream()
                                .map(Enum::toString)
                                .toList());
        }

        public FormaPagamento toModel() {
                return FormaPagamento.valueOf(tipo());
        }
}
