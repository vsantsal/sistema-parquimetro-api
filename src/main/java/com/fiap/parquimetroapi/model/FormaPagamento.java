package com.fiap.parquimetroapi.model;

import java.util.List;

public enum FormaPagamento {
    CARTAO_DE_CREDITO {
        @Override
        public List<TipoTempoEstacionado> getTiposAceitosTempoEstacionado() {
            return null;
        }
    },
    DEBITO {
        @Override
        public List<TipoTempoEstacionado> getTiposAceitosTempoEstacionado() {
            return null;
        }
    },
    PIX {
        @Override
        public List<TipoTempoEstacionado> getTiposAceitosTempoEstacionado() {
            return null;
        }
    };

    public abstract List<TipoTempoEstacionado> getTiposAceitosTempoEstacionado();
}
