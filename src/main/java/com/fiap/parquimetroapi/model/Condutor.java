package com.fiap.parquimetroapi.model;

import jakarta.validation.constraints.Email;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
public class Condutor {
    @Id
    private String id;
    private String nome;
    private String endereco;
    @Email(message = "E-mail inválido")
    private String email;
    private String telefone;
}
