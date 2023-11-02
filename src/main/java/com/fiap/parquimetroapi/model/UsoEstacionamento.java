package com.fiap.parquimetroapi.model;


import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document
@Getter
@Setter
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

    private List<String> alertasEmitidos;

    @Version
    private Long version;

    @Transient
    private Long SEGUNDOS_FALTANTES_PARA_ALERTA = 600L;

    public void encerra(LocalDateTime dataHora){
        acaoDefaultMonitoraEncerra(dataHora);

        // calcula valor devido
        this.setFim(dataHora);
        if (tipoTempoEstacionado.equals(TipoTempoEstacionado.FIXO)) {
            this.setValorDevido(
                    this.estacionamento.getValorHora().multiply(
                            new BigDecimal(this.duracaoEsperada.toMinutes()).divide(new BigDecimal("60"))
                    )
            );
        } else {
            this.setValorDevido(
                    this.estacionamento.getValorHora().multiply(
                            new BigDecimal(this.duracaoEfetiva.toMinutes()).divide(new BigDecimal("60"), RoundingMode.CEILING))
                    );
        }
        this.setPago(true);
    }

    public void monitora(LocalDateTime dataHora){
        acaoDefaultMonitoraEncerra(dataHora);

        // Encerra se FIXO e tempo decorrido igual ou maior que esperado
        if (tipoTempoEstacionado.equals(TipoTempoEstacionado.FIXO) &&
                duracaoEfetiva.getSeconds() >= duracaoEsperada.getSeconds()){
            this.setFim(this.getInicio().plus(duracaoEsperada));
            this.setValorDevido(
                    this.estacionamento.getValorHora().multiply(
                            new BigDecimal(this.duracaoEsperada.toHours())
                    )
            );
        }

        // Amplia se VARIAVEL e tempo decorrido igual ou maior que esperado
        if (tipoTempoEstacionado.equals(TipoTempoEstacionado.VARIAVEL) &&
                duracaoEfetiva.getSeconds() >= duracaoEsperada.getSeconds()) {
            this.setDuracaoEsperada(this.duracaoEsperada.plusHours(1));
        }

    }

    private void ajustaDuracao(){
        if (tipoTempoEstacionado.equals(TipoTempoEstacionado.VARIAVEL)) {
            duracaoEsperada = Duration.ofHours(1L);
        }
    }

    private void acaoDefaultMonitoraEncerra(LocalDateTime dataHora){
        this.ajustaDuracao();
        // lida com alertas
        Duration tempoDecorrido = Duration.between(
                inicio,
                dataHora
        );
        if (alertasEmitidos == null) {
            alertasEmitidos = new ArrayList<>();
        }

        if ((duracaoEsperada.getSeconds() - tempoDecorrido.getSeconds()) <= SEGUNDOS_FALTANTES_PARA_ALERTA) {
            alertasEmitidos.add(tipoTempoEstacionado.getAlerta());
        }

        // Atualiza dados
        this.setDuracaoEfetiva(tempoDecorrido);
    }

}
