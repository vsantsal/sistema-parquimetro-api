package com.fiap.parquimetroapi.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;


public record RegistroCondutorDTO (
        @JsonAlias("condutor")
                @Valid
        CondutorDTO condutorDTO,

        @NotBlank
        String login,

        @NotBlank
        String senha
        ){

}
