package com.fiap.parquimetroapi.controller;

import com.fiap.parquimetroapi.dto.UsoComControleTempoDTO;
import com.fiap.parquimetroapi.dto.UsoEstacionamentoDTO;
import com.fiap.parquimetroapi.service.UsoEstacionamentoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/estacionamentos")
public class UsoEstacionamentoController {
    @Autowired
    private UsoEstacionamentoService usoEstacionamentoService;

    @PostMapping("/usar")
    public ResponseEntity<UsoComControleTempoDTO> iniciar(
            @RequestBody @Valid UsoEstacionamentoDTO dto,
            UriComponentsBuilder uriComponentsBuilder
    ){
        var dtoSalvo = usoEstacionamentoService.iniciarRegistro(dto);
        var uri = uriComponentsBuilder
                .path("/estacionamentos/consultar/{id}")
                .buildAndExpand(dtoSalvo.id()).toUri();
        return ResponseEntity.created(uri).body(dtoSalvo);
    }

    @GetMapping("/consultar/{id}")
    public ResponseEntity<UsoComControleTempoDTO> detalhar(
            @PathVariable String id
    ) {
        var horaDaSolicitacao = LocalDateTime.now();
        var dto = this.usoEstacionamentoService.detalhar(id, horaDaSolicitacao);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/consultar")
    public ResponseEntity<Page<UsoComControleTempoDTO>> listar(
            @PageableDefault(size = 10, sort = {"inicio"})Pageable paginacao
            )
    {
        Page<UsoComControleTempoDTO> pagina = usoEstacionamentoService.listar(paginacao);
        return ResponseEntity.ok(pagina);
    }
}
