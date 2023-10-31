package com.fiap.parquimetroapi.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@Document
@Data
public class Estacionamento {
    @Id
    private String id;
    private String nome;
    private String endereco;
    private BigDecimal valorHora;
}
