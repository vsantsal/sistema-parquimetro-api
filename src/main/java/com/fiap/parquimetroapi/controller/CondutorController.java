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

    @GetMapping("/{id}")
    public ResponseEntity<CondutorDTO> detalhar(
            @PathVariable String id
    ){
        var dto = this.condutorService.detalhar(id);
        return ResponseEntity.ok(dto);
    }

}
