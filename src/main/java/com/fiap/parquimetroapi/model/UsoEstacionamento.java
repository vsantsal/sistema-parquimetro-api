package com.fiap.parquimetroapi.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    private Duration duracaoEfetiva;

    private Duration duracaoEsperada;

    @Transient
    private List<String> alertasEmitidos = new ArrayList<>();

    @Transient
    private Long SEGUNDOS_FALTANTES_PARA_ALERTA = 600L;

    @Transient
    private LocalDateTime inicioVirtual;

    public void monitora(LocalDateTime dataHora){
        this.ajustaDuracao();
        Duration tempoDecorrido = Duration.between(
                inicioVirtual,
                dataHora
        );
        if ((duracaoEsperada.getSeconds() - tempoDecorrido.getSeconds()) <= SEGUNDOS_FALTANTES_PARA_ALERTA) {
            alertasEmitidos.add(tipoTempoEstacionado.getAlerta());
        }

    }

    private void ajustaDuracao(){
        if (tipoTempoEstacionado.equals(TipoTempoEstacionado.VARIAVEL)) {
            duracaoEsperada = Duration.ofHours(1L);
        }
    }

    public void setInicio(LocalDateTime inicio){
        this.inicio = inicio;
        this.inicioVirtual = inicio;

    }

}
