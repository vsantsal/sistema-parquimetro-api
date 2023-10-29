package com.fiap.parquimetroapi.controller;

import com.fiap.parquimetroapi.dto.VeiculoDTO;
import com.fiap.parquimetroapi.service.CondutorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/veiculos")
public class VeiculoController {

    @Autowired
    private CondutorService condutorService;

    @PostMapping
    public ResponseEntity<VeiculoDTO> registrar(
            @RequestBody @Valid VeiculoDTO dto
    ){
        VeiculoDTO dtoSalvo = condutorService.registrarVeiculo(dto);
        return ResponseEntity.ok(dtoSalvo);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity descadastra(
            @PathVariable String id
    ){
        this.condutorService.desassociaVeiculo(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<VeiculoDTO>> listar(){
        var dto = this.condutorService.listarVeiculos();
        return ResponseEntity.ok(dto);
    }

}
