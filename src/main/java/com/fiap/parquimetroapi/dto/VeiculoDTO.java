package com.fiap.parquimetroapi.dto;


import com.fiap.parquimetroapi.model.Placa;
import com.fiap.parquimetroapi.model.Veiculo;
import jakarta.validation.constraints.NotBlank;


public record VeiculoDTO(
        @NotBlank
        String placa,

        String veiculoId,

        String condutorId
) {
    public VeiculoDTO(Veiculo veiculo) {
        this(veiculo.getPlaca().toString(),
                veiculo.getId(),
                veiculo.getCondutor().getId());
    }


    public Veiculo toModel() {
        Placa placa1 = Placa.criar(placa);
        return new Veiculo(placa1);
    }
}
