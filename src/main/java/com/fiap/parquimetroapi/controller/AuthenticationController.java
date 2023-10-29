package com.fiap.parquimetroapi.controller;

import com.fiap.parquimetroapi.dto.UsuarioDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import com.fiap.parquimetroapi.dto.RegistroCondutorDTO;
import com.fiap.parquimetroapi.dto.LoginRespostaDTO;
import com.fiap.parquimetroapi.infra.security.TokenService;
import com.fiap.parquimetroapi.model.Usuario;
import com.fiap.parquimetroapi.service.RegistroCondutorService;


@RestController
@RequestMapping("/auth")
public class AuthenticationController {
    @Autowired
    private RegistroCondutorService registroService;

    @Autowired
    private TokenService tokenService;


    @PostMapping("/login")
    public ResponseEntity login(
            @RequestBody @Valid UsuarioDTO dto
    ){
        var usuarioAAutenticar = new Usuario(dto.login(), dto.senha());
        boolean autenticado = registroService.autenticar(usuarioAAutenticar);
        if (!autenticado){
            throw new AuthenticationCredentialsNotFoundException("Usuário e/ou senha incorretos");
        }

        var token = tokenService.gerarToken(usuarioAAutenticar);

        return ResponseEntity.ok(new LoginRespostaDTO(token));

    }

    @PostMapping("/registrar")
    public ResponseEntity registrar(@RequestBody @Valid RegistroCondutorDTO dto) {
        var dtoSalvo = registroService.registrar(dto);
        return ResponseEntity.ok(dtoSalvo);
    }
}
