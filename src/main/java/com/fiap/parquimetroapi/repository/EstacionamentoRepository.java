package com.fiap.parquimetroapi.repository;

import com.fiap.parquimetroapi.model.Estacionamento;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface EstacionamentoRepository extends MongoRepository<Estacionamento, String> {
    Optional<Estacionamento> findByCnpj(String cnpj);
}
