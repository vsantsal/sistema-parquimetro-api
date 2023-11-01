package com.fiap.parquimetroapi.repository;

import com.fiap.parquimetroapi.model.Condutor;
import com.fiap.parquimetroapi.model.UsoEstacionamento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UsoEstacionamentoRepository extends MongoRepository<UsoEstacionamento, String> {
    Page<UsoEstacionamento> findAllByCondutor(Condutor condutor, Pageable page);
}
