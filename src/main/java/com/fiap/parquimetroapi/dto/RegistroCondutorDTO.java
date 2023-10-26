package com.fiap.parquimetroapi.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.Valid;


public record RegistroCondutorDTO (
        @JsonAlias("condutor")
                @Valid
        CondutorDTO condutorDTO,

        String senha
        ){

}
