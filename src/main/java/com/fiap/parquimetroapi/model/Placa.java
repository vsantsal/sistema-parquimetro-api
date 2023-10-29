package com.fiap.parquimetroapi.model;

import com.fiap.parquimetroapi.exception.PlacaInvalidaException;

import java.util.List;

public class Placa {
    private final String valor;

    private static final List<String> padroes = List.of(
            // Padrão anterior
            "[A-Z]{3}[0-9]{4}",
            // Padrão Mercosul
            "[A-Z]{3}[0-9]{1}[A-Z]{1}[0-9]{2}");

    private Placa(String valor) {
        this.valor = valor;
    }
    public static Placa criar(String valor){
        // Valida se valor de placa e válido
        padroes.stream()
                .filter(valor::matches)
                .findFirst()
                .orElseThrow(
                        () -> new PlacaInvalidaException("valor incorreto de placa informado")
                );

        // Cria placa se padrão válido
        return new Placa(valor);
    }

    @Override
    public String toString() {

        return valor;
    }
}
