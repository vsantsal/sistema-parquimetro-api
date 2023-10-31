package com.fiap.parquimetroapi.model;

public enum TipoTempoEstacionado {
    FIXO {
        @Override
        public String getAlerta() {
            return "Tempo de estacionamento prestes a expirar. Estacionamento será encerrado automaticamente.";
        }
    },
    VARIAVEL {
        @Override
        public String getAlerta() {
            return "Tempo de estacionamento prestes a expirar. Estacionamento será estendido automaticamente caso não seja encerrado.";
        }
    };

    public abstract String getAlerta();
}
