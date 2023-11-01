package com.fiap.parquimetroapi.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document
@Getter
@Setter
public class Condutor {
    @Id
    private String id;
    private String nome;
    private String endereco;
    private String email;
    private String telefone;
    private Usuario usuario;

    private FormaPagamento formaPagamento;

    @DBRef
    private List<Veiculo> veiculos = new ArrayList<>();

    public void associa(Veiculo veiculo){
        veiculos.add(veiculo);
    }
}
