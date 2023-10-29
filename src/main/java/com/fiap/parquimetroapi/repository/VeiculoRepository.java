package com.fiap.parquimetroapi.repository;

import com.fiap.parquimetroapi.model.Placa;
import com.fiap.parquimetroapi.model.Veiculo;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface VeiculoRepository extends MongoRepository<Veiculo, String> {
    boolean existsByPlaca(Placa placa);

}
