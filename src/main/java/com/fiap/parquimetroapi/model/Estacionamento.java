package com.fiap.parquimetroapi.model;


import com.fiap.parquimetroapi.exception.EstacionamentoSimultaneoException;
import lombok.Getter;
import org.hibernate.validator.constraints.br.CNPJ;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Document
@Getter
public class Estacionamento {
    @Id
    private String id;
    @CNPJ
    private String cnpj;
    private Long lotacaoMaxima;
    private BigDecimal valorHora;

    @Transient
    private Long lotacaoAtual;

    @Transient
    private static final Map<String, Estacionamento> estacionamentos = new HashMap<>();

    @Transient
    private static final Set<Veiculo> idsVeiculosEstacionados = new HashSet<>();

    private Estacionamento(String cnpj, Long lotacaoMaxima, BigDecimal valorHora){
        this.cnpj = cnpj;
        this.lotacaoMaxima = lotacaoMaxima;
        this.valorHora = valorHora;
    }

    public static Estacionamento getInstance(String cnpj, Long lotacaoMaxima, BigDecimal valorHora){
        Estacionamento estacionamento = estacionamentos.get(cnpj);
        if (estacionamento != null) {
            return estacionamento;
        }
        Estacionamento novoEstacionamento = new Estacionamento(cnpj, lotacaoMaxima, valorHora);
        novoEstacionamento.setLotacaoAtual(novoEstacionamento.getLotacaoMaxima());
        estacionamentos.put(cnpj, novoEstacionamento);
        return novoEstacionamento;

    }


    public void estaciona(Veiculo veiculo) {
        if (idsVeiculosEstacionados.contains(veiculo)) {
            throw new EstacionamentoSimultaneoException(
                    "Existe registro de estacionamento em andamento para o veículo de placa '" +
                            veiculo.getPlaca() + "'");
        }
        idsVeiculosEstacionados.add(veiculo);
        this.lotacaoAtual -= 1L;
    }

    private void setLotacaoAtual(Long lotacaoAtual){
        if (lotacaoAtual < 0) {
            throw new IllegalArgumentException("Não é possível criar estacionamento com lotação não positiva");
        }
        this.lotacaoAtual = lotacaoAtual;
    }
}
