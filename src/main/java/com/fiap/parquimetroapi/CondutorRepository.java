package com.fiap.parquimetroapi;

import com.fiap.parquimetroapi.model.Condutor;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CondutorRepository extends MongoRepository<Condutor, String> {
}
