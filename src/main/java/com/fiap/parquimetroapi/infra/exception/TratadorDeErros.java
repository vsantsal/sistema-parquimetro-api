package com.fiap.parquimetroapi.infra.exception;

import com.fiap.parquimetroapi.exception.*;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestControllerAdvice
public class TratadorDeErros {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity tratarErro400(MethodArgumentNotValidException exception) {
        var erros = exception.getFieldErrors();
        List<DadosErrosValidacao> camposComErro = new ArrayList<>();
        for (FieldError erro: erros) {
            var camposComErroTratados = Arrays.stream(
                    erro.getField().split("[.]")).toList();
            if (camposComErroTratados.size() > 1) {
                camposComErro.add(
                        new DadosErrosValidacao(camposComErroTratados.get(1),
                                erro.getDefaultMessage()));
            } else {
                camposComErro.add(
                        new DadosErrosValidacao(erro.getField(),
                                erro.getDefaultMessage()));
            }
        }

        return ResponseEntity.badRequest().body(camposComErro);


    }

    @ExceptionHandler({
            IllegalArgumentException.class,
            PlacaInvalidaException.class,
            FormaPagamentoAusenteException.class,
            TipoTempoEstacionadoInvalido.class,
            ConstanteEnumInexistenteException.class,
            VeiculoInexistenteException.class
    })
    public ResponseEntity tratarErroDevolvendo400(Exception exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new ErroSoComMensagemValidacao(exception.getMessage())
        );
    }

    @ExceptionHandler(DataRetrievalFailureException.class)
    public ResponseEntity tratarErroDevolvendo404(DataRetrievalFailureException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ErroSoComMensagemValidacao(exception.getMessage())
        );
    }

    @ExceptionHandler(AuthenticationCredentialsNotFoundException.class)
    public ResponseEntity tratarErroDevolvendo403(AuthenticationCredentialsNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                new ErroSoComMensagemValidacao(exception.getMessage())
        );
    }

    @ExceptionHandler({CondutorExistenteException.class, VeiculoExistenteException.class})
    public ResponseEntity tratarErroDevolvendo409(Exception exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                new ErroSoComMensagemValidacao(exception.getMessage())
        );
    }

    private record DadosErrosValidacao(String campo, String mensagem) {
    }

    private record ErroSoComMensagemValidacao(String mensagem){ }
}
