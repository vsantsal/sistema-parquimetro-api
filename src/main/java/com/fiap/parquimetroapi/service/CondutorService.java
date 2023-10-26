package com.fiap.parquimetroapi.service;

import com.fiap.parquimetroapi.repository.CondutorRepository;
import com.fiap.parquimetroapi.dto.CondutorDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.stereotype.Service;

import java.util.Objects;


@Service
public class CondutorService {

    @Autowired
    private CondutorRepository condutorRepository;

    public CondutorDTO detalhar(String id) {
        var usuarioLogado = RegistroCondutorService.getUsuarioLogado();
        var condutorDeUsuario = this.condutorRepository.findFirstByLogin(usuarioLogado.getLogin());
        if (condutorDeUsuario.isEmpty() ||
                !Objects.equals(condutorDeUsuario.get().getId(), id)){
            throw new DataRetrievalFailureException("Recurso inválido");
        }
        return new CondutorDTO(condutorDeUsuario.get());
    }

}
