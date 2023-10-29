package com.fiap.parquimetroapi.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.time.LocalDateTime;

@Document
@Getter
public class Veiculo {

    @Id
    private String id;

    private Placa placa;

    @DocumentReference(lazy = true)
    private Condutor condutor;

    private LocalDateTime criadoEm;

    private LocalDateTime atualizadoEm;

    private boolean ativo;

    public Veiculo(Placa placa, Condutor condutor){
        this.placa = placa;
        this.condutor = condutor;
        this.criadoEm = LocalDateTime.now();
        this.atualizadoEm = this.criadoEm;
        this.ativo = true;

    }

    public Veiculo(Placa placa){
        this.placa = placa;
        this.criadoEm = LocalDateTime.now();
        this.atualizadoEm = this.criadoEm;
        this.ativo = true;
    }

    @Deprecated
    public Veiculo(){

    }

    public void setCondutor(Condutor condutor) {
        this.condutor = condutor;
        this.atualizadoEm = LocalDateTime.now();
    }

    public void inativa(){
        this.ativo = false;
        this.atualizadoEm = LocalDateTime.now();
    }

}
