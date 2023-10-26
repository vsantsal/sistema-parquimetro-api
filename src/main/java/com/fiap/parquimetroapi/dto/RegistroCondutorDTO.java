package com.fiap.parquimetroapi.dto;

import com.fasterxml.jackson.annotation.JsonAlias;


public record RegistroCondutorDTO (
        @JsonAlias("condutor")
        CondutorDTO condutorDTO,

        String senha
        ){

}
