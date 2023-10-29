package com.fiap.parquimetroapi.repository;

import com.fiap.parquimetroapi.model.Condutor;
import com.fiap.parquimetroapi.model.Placa;
import com.fiap.parquimetroapi.model.Veiculo;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface VeiculoRepository extends MongoRepository<Veiculo, String> {
    boolean existsByPlaca(Placa placa);

    List<Veiculo> findByCondutor(Condutor condutor);

}
