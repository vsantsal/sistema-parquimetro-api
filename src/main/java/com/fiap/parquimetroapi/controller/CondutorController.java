package com.fiap.parquimetroapi.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.fiap.parquimetroapi.dto.CondutorDTO;
import com.fiap.parquimetroapi.service.CondutorService;

@RestController
@RequestMapping("/condutores")
public class CondutorController {
    @Autowired
    private CondutorService condutorService;

    @PostMapping
    public ResponseEntity<CondutorDTO> registrar(
            @RequestBody @Valid CondutorDTO dto,
            UriComponentsBuilder uriComponentsBuilder
    ){
        var dtoResposta = this.condutorService.registrar(dto);
        var uri = uriComponentsBuilder
                .path("/condutores/{id}")
                .buildAndExpand(dtoResposta.id())
                .toUri();
        return ResponseEntity.created(uri).body(dtoResposta);
    }

}
