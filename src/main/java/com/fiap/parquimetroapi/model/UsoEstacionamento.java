package com.fiap.parquimetroapi.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Document
@Data
public class UsoEstacionamento {

    @Id
    private String id;

    private Condutor condutor;

    private Veiculo veiculo;

    private Estacionamento estacionamento;

    private LocalDateTime inicio;

    private LocalDateTime fim;

    private BigDecimal valorDevido;

    private boolean pago;

    private TipoTempoEstacionado tipoTempoEstacionado;

}
