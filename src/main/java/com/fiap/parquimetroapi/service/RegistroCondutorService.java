package com.fiap.parquimetroapi.service;

import com.fiap.parquimetroapi.dto.CondutorDTO;
import com.fiap.parquimetroapi.dto.RegistroCondutorDTO;
import com.fiap.parquimetroapi.model.Condutor;
import com.fiap.parquimetroapi.model.Usuario;
import com.fiap.parquimetroapi.exception.CondutorExistenteException;
import com.fiap.parquimetroapi.repository.CondutorRepository;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class RegistroCondutorService {


    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CondutorRepository condutorRepository;

    @Transactional
    public CondutorDTO registrar(RegistroCondutorDTO dto){
        // Se usuário já cadastrado, informa erro ao usuário
        if (condutorRepository.findFirstByLogin(dto.login()).isPresent()){
            throw new CondutorExistenteException("Condutor já cadastrado");
        }

        // Criptografa senha para salvar no BD
        String senhaCriptografada = passwordEncoder.encode(dto.senha());
        Usuario usuario = new Usuario(dto.login(), senhaCriptografada);
        LocalDateTime dataHoraCriacao = LocalDateTime.now();
        usuario.setCriadoEm(dataHoraCriacao);
        usuario.setAtualizadoEm(dataHoraCriacao);
        usuario.setAtivo(true);

        // Extrai modelo do DTO
        Condutor condutor = dto.condutorDTO().toModel();
        condutor.setUsuario(usuario);

        // Deriva informações
        Condutor condutorSalvo = condutorRepository.save(condutor);

        return new CondutorDTO(condutorSalvo);

    }

    @Transactional
    public boolean autenticar(Usuario usuario){
        var condutorAAutenticar = condutorRepository.findFirstByLogin(usuario.getLogin());
        return condutorAAutenticar
                .map(
                        condutor ->  passwordEncoder
                                .matches(usuario.getSenha(),
                                        condutor.getUsuario().getPassword()))
                .orElse(false);
    }

    public static Usuario getUsuarioLogado(){
        return (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

}
