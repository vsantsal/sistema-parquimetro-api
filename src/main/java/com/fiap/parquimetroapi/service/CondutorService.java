package com.fiap.parquimetroapi.service;

import com.fiap.parquimetroapi.repository.CondutorRepository;
import com.fiap.parquimetroapi.dto.CondutorDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.stereotype.Service;

@Service
public class CondutorService {

    @Autowired
    private CondutorRepository condutorRepository;

    public CondutorDTO detalhar(String id) {
        var condutor = this.condutorRepository
                .findById(id)
                .orElseThrow(
                        () -> new DataRetrievalFailureException(
                                "Condutor de id '" + id + "' não encontrado"
                        )
                );
        return new CondutorDTO(condutor);
    }
}
