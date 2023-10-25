package com.fiap.parquimetroapi.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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

    @GetMapping("/{id}")
    public ResponseEntity<CondutorDTO> detalhar(
            @PathVariable String id
    ){
        var dto = this.condutorService.detalhar(id);
        return ResponseEntity.ok(dto);
    }

}
