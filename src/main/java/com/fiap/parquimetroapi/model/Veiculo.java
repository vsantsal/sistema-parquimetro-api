package com.fiap.parquimetroapi.model;

import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
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

    @Version
    private Long version;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Veiculo veiculo = (Veiculo) o;

        return placa.equals(veiculo.placa);
    }

    @Override
    public int hashCode() {
        return placa.hashCode();
    }
}
