package com.fiap.parquimetroapi.model;

import java.util.List;

public enum FormaPagamento {
    CARTAO_DE_CREDITO {
        @Override
        public List<TipoTempoEstacionado> getTiposAceitosTempoEstacionado() {
            return List.of(TipoTempoEstacionado.FIXO, TipoTempoEstacionado.VARIAVEL);
        }
    },
    DEBITO {
        @Override
        public List<TipoTempoEstacionado> getTiposAceitosTempoEstacionado() {
            return List.of(TipoTempoEstacionado.FIXO, TipoTempoEstacionado.VARIAVEL);
        }
    },
    PIX {
        @Override
        public List<TipoTempoEstacionado> getTiposAceitosTempoEstacionado() {

            return List.of(TipoTempoEstacionado.FIXO);
        }
    };

    public abstract List<TipoTempoEstacionado> getTiposAceitosTempoEstacionado();
}
