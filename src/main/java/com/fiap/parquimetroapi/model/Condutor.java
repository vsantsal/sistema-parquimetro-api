package com.fiap.parquimetroapi.model;

import jakarta.validation.constraints.Email;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document
@Data
public class Condutor {
    @Id
    private String id;
    private String nome;
    private String endereco;
    private String email;
    private String telefone;
    private Usuario usuario;

    private FormaPagamento formaPagamento;
}
