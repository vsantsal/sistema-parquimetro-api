package com.fiap.parquimetroapi.service;

import com.fiap.parquimetroapi.dto.FormaPagamentoDTO;
import com.fiap.parquimetroapi.model.Condutor;
import com.fiap.parquimetroapi.repository.CondutorRepository;
import com.fiap.parquimetroapi.dto.CondutorDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;


@Service
public class CondutorService {

    @Autowired
    private CondutorRepository condutorRepository;

    public CondutorDTO detalhar(String id) {
        var condutorDeUsuario = validaUsuarioLogadoERetorna(id);
        return new CondutorDTO(condutorDeUsuario);
    }

    public void descadastra(String id) {
        var condutorDeUsuario = validaUsuarioLogadoERetorna(id);
        var usuario = condutorDeUsuario.getUsuario();
        usuario.setAtivo(false);
        usuario.setAtualizadoEm(LocalDateTime.now());
        condutorDeUsuario.setUsuario(usuario);
        condutorRepository.save(condutorDeUsuario);
    }

    public CondutorDTO atualizar(String id, CondutorDTO dto) {
        var condutorDeUsuario = validaUsuarioLogadoERetorna(id);
        condutorDeUsuario.setNome(dto.nome());
        condutorDeUsuario.setEndereco(dto.endereco());
        condutorDeUsuario.setTelefone(dto.telefone());
        condutorDeUsuario.setEmail(dto.email());
        condutorRepository.save(condutorDeUsuario);
        return new CondutorDTO(condutorDeUsuario);

    }

    public FormaPagamentoDTO registrarFormaPagamento(FormaPagamentoDTO dto) {
        Condutor condutor = this.obterCondutorLogado();
        var formaPagamento = dto.toModel();

        condutor.setFormaPagamento(formaPagamento);
        condutorRepository.save(condutor);

        return  new FormaPagamentoDTO(condutor);


    }

    public FormaPagamentoDTO consultarFormaPagamento() {
        Condutor condutor = this.obterCondutorLogado();
        return new FormaPagamentoDTO(condutor);
    }

    private Condutor obterCondutorLogado(){
        var usuarioLogado = RegistroCondutorService.getUsuarioLogado();
        Optional<Condutor> possivelCondutor = condutorRepository
                .findFirstByLogin(usuarioLogado.getLogin());

        if (possivelCondutor.isEmpty()) {
            throw new DataRetrievalFailureException("Recurso invalido");
        }

        return possivelCondutor.get();

    }

    private Condutor validaUsuarioLogadoERetorna(String id){
        var condutorDeUsuario = this.obterCondutorLogado();
        if (!Objects.equals(condutorDeUsuario.getId(), id)){
            throw new DataRetrievalFailureException("Recurso inválido");
        }
        return condutorDeUsuario;
    }
}
