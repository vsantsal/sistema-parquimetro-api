package com.fiap.parquimetroapi.dto;

import com.fiap.parquimetroapi.model.Condutor;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


public record CondutorDTO(
        String nome,
        String endereco,

        @NotBlank(message = "E-mail é obrigatório")
        @Email(message = "E-mail inválido")
        String email,
        String telefone,

        String id
) {
    public Condutor toModel(){
        Condutor condutor = new Condutor();
        condutor.setNome(nome());
        condutor.setEndereco(endereco());
        condutor.setEmail(email());
        condutor.setTelefone(telefone());
        return  condutor;
    }

    public CondutorDTO(Condutor condutor) {
        this(
                condutor.getNome(), condutor.getEndereco(),
                condutor.getEmail(), condutor.getTelefone(),
                condutor.getId()
        );
    }
}
