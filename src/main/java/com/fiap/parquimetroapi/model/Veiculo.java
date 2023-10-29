package com.fiap.parquimetroapi.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Veiculo {

    @Id
    private String id;

    private Placa placa;

}
