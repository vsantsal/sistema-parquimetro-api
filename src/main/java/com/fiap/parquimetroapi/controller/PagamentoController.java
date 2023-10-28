package com.fiap.parquimetroapi.controller;

import com.fiap.parquimetroapi.dto.FormaPagamentoDTO;
import com.fiap.parquimetroapi.service.PagamentoService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pagamentos")
public class PagamentoController {

    @Autowired
    private PagamentoService pagamentoService;

    @PostMapping("/forma")
    public ResponseEntity<FormaPagamentoDTO> registrar(
            @RequestBody @Valid FormaPagamentoDTO dto
    ){
        FormaPagamentoDTO dtoSalvo = pagamentoService.registrarForma(dto);
        return ResponseEntity.ok(dtoSalvo);
    }
}
