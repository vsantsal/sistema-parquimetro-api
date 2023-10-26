package com.fiap.parquimetroapi.service;

import com.fiap.parquimetroapi.model.Condutor;
import com.fiap.parquimetroapi.repository.CondutorRepository;
import com.fiap.parquimetroapi.dto.CondutorDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;


@Service
public class CondutorService {

    @Autowired
    private CondutorRepository condutorRepository;

    public CondutorDTO detalhar(String id) {
        var condutorDeUsuario = validaUsuarioLogadoERetorna(id);
        return new CondutorDTO(condutorDeUsuario);
    }

    public void descadastra(String id) {
        var condutorDeUsuario = validaUsuarioLogadoERetorna(id);
        var usuario = condutorDeUsuario.getUsuario();
        usuario.setAtivo(false);
        usuario.setAtualizadoEm(LocalDateTime.now());
        condutorDeUsuario.setUsuario(usuario);
        condutorRepository.save(condutorDeUsuario);
    }

    public CondutorDTO atualizar(String id, CondutorDTO dto) {
        var condutorDeUsuario = validaUsuarioLogadoERetorna(id);
        condutorDeUsuario.setNome(dto.nome());
        condutorDeUsuario.setEndereco(dto.endereco());
        condutorDeUsuario.setTelefone(dto.telefone());
        condutorRepository.save(condutorDeUsuario);
        return new CondutorDTO(condutorDeUsuario);

    }

    private Condutor validaUsuarioLogadoERetorna(String id){
        var usuarioLogado = RegistroCondutorService.getUsuarioLogado();
        var condutorDeUsuario = this.condutorRepository.findFirstByLogin(usuarioLogado.getLogin());
        if (condutorDeUsuario.isEmpty() ||
                !Objects.equals(condutorDeUsuario.get().getId(), id)){
            throw new DataRetrievalFailureException("Recurso inválido");
        }
        return condutorDeUsuario.get();
    }

}
