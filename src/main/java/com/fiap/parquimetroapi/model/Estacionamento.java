package com.fiap.parquimetroapi.model;


import com.fiap.parquimetroapi.exception.EstacionamentoLotadoException;
import com.fiap.parquimetroapi.exception.EstacionamentoSimultaneoException;
import com.fiap.parquimetroapi.exception.VeiculoNaoEstacionadoException;
import lombok.Getter;
import org.hibernate.validator.constraints.br.CNPJ;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
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
    private LocalDateTime criadoEm;

    @Transient
    private Long vagasDisponiveis;

    @Transient
    private final Set<Veiculo> veiculosEstacionados = new HashSet<>();

    public Estacionamento(String cnpj, Long lotacaoMaxima, BigDecimal valorHora){
        this.setLotacaoMaxima(lotacaoMaxima);
        this.cnpj = cnpj;
        this.valorHora = valorHora;
        this.vagasDisponiveis = this.lotacaoMaxima;
        this.criadoEm = LocalDateTime.now();

    }

    public void estaciona(Veiculo veiculo) {
        // Se for identificado que o veículo já está estacionado, lança exceção informando        
        if (veiculosEstacionados.contains(veiculo)) {
            throw new EstacionamentoSimultaneoException(
                    "Existe registro de estacionamento em andamento para o veículo de placa '" +
                            veiculo.getPlaca() + "'");
        }
        
        // Se capacidade máxima tiver sido alcançada, lança exceção informando
        if (vagasDisponiveis == 0L) {
            throw new EstacionamentoLotadoException("Estacionamento de CNPJ '" + cnpj + "' lotado");
        }

        veiculosEstacionados.add(veiculo);
        this.vagasDisponiveis -= 1L;
    }

    private void setLotacaoMaxima(Long lotacaoMaxima){
        if (lotacaoMaxima <= 0) {
            throw new IllegalArgumentException("Não é possível criar estacionamento com lotação não positiva");
        }
        this.lotacaoMaxima = lotacaoMaxima;
    }

    public void libera(Veiculo veiculo) {
        if (!veiculosEstacionados.contains(veiculo)) {
            throw new VeiculoNaoEstacionadoException("Não é possível liberar vaga para veículo não estacionado");
        }
        veiculosEstacionados.remove(veiculo);
        this.vagasDisponiveis += 1L;
    }

}
