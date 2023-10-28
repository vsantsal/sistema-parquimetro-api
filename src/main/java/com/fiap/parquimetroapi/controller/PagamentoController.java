package com.fiap.parquimetroapi.controller;

import com.fiap.parquimetroapi.dto.FormaPagamentoDTO;

import com.fiap.parquimetroapi.service.CondutorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pagamentos")
public class PagamentoController {

    @Autowired
    private CondutorService condutorService;

    @PostMapping("/forma")
    public ResponseEntity<FormaPagamentoDTO> registrar(
            @RequestBody @Valid FormaPagamentoDTO dto
    ){
        FormaPagamentoDTO dtoSalvo = condutorService.registrarFormaPagamento(dto);
        return ResponseEntity.ok(dtoSalvo);
    }

    @GetMapping("/forma")
    public ResponseEntity<FormaPagamentoDTO> consultar(){
        FormaPagamentoDTO dtoSalvo = condutorService.consultarFormaPagamento();
        return ResponseEntity.ok(dtoSalvo);
    }

}
