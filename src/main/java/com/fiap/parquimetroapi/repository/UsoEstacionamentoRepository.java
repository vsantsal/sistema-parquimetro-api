package com.fiap.parquimetroapi.repository;

import com.fiap.parquimetroapi.model.UsoEstacionamento;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UsoEstacionamentoRepository extends MongoRepository<UsoEstacionamento, String> {
}
