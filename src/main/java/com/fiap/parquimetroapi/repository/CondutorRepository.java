package com.fiap.parquimetroapi.repository;

import com.fiap.parquimetroapi.model.Condutor;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;

public interface CondutorRepository extends MongoRepository<Condutor, String> {
    @Query("{ 'usuario.login' : ?0}")
    Optional<Condutor> findFirstByLogin(String login);
}
