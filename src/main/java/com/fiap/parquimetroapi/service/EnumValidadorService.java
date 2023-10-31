package com.fiap.parquimetroapi.service;

import com.fiap.parquimetroapi.exception.ConstanteEnumInexistenteException;

public class EnumValidadorService {
    public static <E extends Enum<E>> E recupera(Class<E> e, String valor, String campoDTO) {
        try {
            E resultado = Enum.valueOf(e, valor);
            return resultado;
        } catch (IllegalArgumentException ex) {
            throw new ConstanteEnumInexistenteException(
                    "Valor '" + valor + "' inválido para '" + campoDTO + "'"
            );
        }
    }
}
