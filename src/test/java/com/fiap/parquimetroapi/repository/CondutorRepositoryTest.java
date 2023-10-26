package com.fiap.parquimetroapi.repository;

import com.fiap.parquimetroapi.dto.CondutorDTO;
import com.fiap.parquimetroapi.model.Condutor;
import com.fiap.parquimetroapi.model.Usuario;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DataMongoTest
class CondutorRepositoryTest {

    private Condutor condutor;
    private final String LOGIN_DEFAULT = "login_default";
    @Autowired
    private CondutorRepository repository;

    @Autowired
    MongoTemplate mongoTemplate;

    @BeforeEach
    public void  setUp(){
        condutor = new CondutorDTO(
                "Fulano",
                "Endereço do Fulano",
                "fulano@email.com",
                "123456789",
                null
        ).toModel();
        condutor.setUsuario(new Usuario("fulano@email.com", "123456"));
    }
    @AfterEach
    public void tearDownDatabase(){
        mongoTemplate.getDb().drop();
    }

    @DisplayName("Se não houver condutores retorna vazio")
    @Test
    public void testCenario1(){
        // Arrange

        // Act
        var condutorPesquisado = repository.findFirstByLogin(LOGIN_DEFAULT);

        // Assert
        assertTrue(condutorPesquisado.isEmpty());
    }

    @DisplayName("Se houver condutor com login pesquisado retorna ele")
    @Test
    public void testCenario2(){
        // Arrange
        repository.save(condutor);
        // Act
        var condutorPesquisado = repository.findFirstByLogin(condutor.getEmail());

        // Assert
        assertTrue(condutorPesquisado.isPresent());
        assertEquals(condutorPesquisado.get().getNome(), condutor.getNome());
        assertEquals(condutorPesquisado.get().getEndereco(), condutor.getEndereco());
        assertEquals(condutorPesquisado.get().getEmail(), condutor.getEmail());
        assertEquals(condutorPesquisado.get().getTelefone(), condutor.getTelefone());
    }

    @DisplayName("Se houver condutor com login diferente pesquisado retorna vazio")
    @Test
    public void testCenario3(){
        // Arrange
        repository.save(condutor);
        // Act
        var condutorPesquisado = repository.findFirstByLogin(LOGIN_DEFAULT);

        // Assert
        assertTrue(condutorPesquisado.isEmpty());
   }

    @DisplayName("Se houver mais de um condutor com mesmo login identifica")
    @Test
    public void testCenario4(){
        // Arrange
        repository.save(condutor);
        var condutor2 = new CondutorDTO(
                "Ciclano",
                "Endereço do Ciclano",
                "fulano@email.com",
                "987654321",
                null
        ).toModel();
        condutor2.setUsuario(new Usuario("fulano@email.com", "654321"));
        // Act
        var condutorPesquisado = repository.findFirstByLogin("fulano@email.com");

        // Assert
        assertTrue(condutorPesquisado.isPresent());
        assertEquals(condutorPesquisado.get().getNome(), "Fulano");
    }

}