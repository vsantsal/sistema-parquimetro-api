package com.fiap.parquimetroapi.controller;

import com.fiap.parquimetroapi.dto.UsoEstacionamentoDTO;
import com.fiap.parquimetroapi.service.UsoEstacionamentoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/estacionamentos")
public class UsoEstacionamentoController {
    @Autowired
    private UsoEstacionamentoService usoEstacionamentoService;

    @PostMapping("/iniciar-registro")
    public ResponseEntity<UsoEstacionamentoDTO> iniciar(
            @RequestBody @Valid UsoEstacionamentoDTO dto,
            UriComponentsBuilder uriComponentsBuilder
    ){
        var dtoSalvo = usoEstacionamentoService.iniciarRegistro(dto);
        var uri = uriComponentsBuilder
                .path("/estacionamentos/iniciar-registro/{id}")
                .buildAndExpand(dtoSalvo.id()).toUri();
        return ResponseEntity.created(uri).body(dtoSalvo);
    }
}
