package com.fiap.parquimetroapi.service;

import com.fiap.parquimetroapi.dto.FormaPagamentoDTO;
import com.fiap.parquimetroapi.dto.VeiculoDTO;
import com.fiap.parquimetroapi.exception.VeiculoExistenteException;
import com.fiap.parquimetroapi.model.Condutor;
import com.fiap.parquimetroapi.repository.CondutorRepository;
import com.fiap.parquimetroapi.dto.CondutorDTO;
import com.fiap.parquimetroapi.repository.VeiculoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


@Service
public class CondutorService {

    @Autowired
    private CondutorRepository condutorRepository;

    @Autowired
    private VeiculoRepository veiculoRepository;

    @Transactional(readOnly = true)
    public CondutorDTO detalhar(String id) {
        var condutorDeUsuario = validaUsuarioLogadoERetorna(id);
        return new CondutorDTO(condutorDeUsuario);
    }

    @Transactional
    public void descadastra(String id) {
        var condutorDeUsuario = validaUsuarioLogadoERetorna(id);
        var usuario = condutorDeUsuario.getUsuario();
        usuario.setAtivo(false);
        usuario.setAtualizadoEm(LocalDateTime.now());
        condutorDeUsuario.setUsuario(usuario);
        condutorRepository.save(condutorDeUsuario);
    }

    @Transactional
    public CondutorDTO atualizar(String id, CondutorDTO dto) {
        var condutorDeUsuario = validaUsuarioLogadoERetorna(id);
        condutorDeUsuario.setNome(dto.nome());
        condutorDeUsuario.setEndereco(dto.endereco());
        condutorDeUsuario.setTelefone(dto.telefone());
        condutorDeUsuario.setEmail(dto.email());
        condutorRepository.save(condutorDeUsuario);
        return new CondutorDTO(condutorDeUsuario);

    }

    @Transactional
    public FormaPagamentoDTO registrarFormaPagamento(FormaPagamentoDTO dto) {
        Condutor condutor = this.obterCondutorLogado();
        var formaPagamento = dto.toModel();

        condutor.setFormaPagamento(formaPagamento);
        condutorRepository.save(condutor);

        return  new FormaPagamentoDTO(condutor);


    }

    @Transactional(readOnly = true)
    public FormaPagamentoDTO consultarFormaPagamento() {
        Condutor condutor = this.obterCondutorLogado();
        try {
            return new FormaPagamentoDTO(condutor);
        } catch (NullPointerException ex) {
            throw new DataRetrievalFailureException("Forma de pagamento preferida não registrada");
        }
    }

    @Transactional(readOnly = true)
    public Condutor obterCondutorLogado(){
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

    @Transactional
    public VeiculoDTO registrarVeiculo(VeiculoDTO dto) {
        //
        var condutorDeUsuario = this.obterCondutorLogado();
        var veiculoInformado = dto.toModel();

        //
        if (veiculoRepository.existsByPlaca(veiculoInformado.getPlaca()))
            throw new VeiculoExistenteException("Veículo já em uso na plataforma");

        //
        veiculoInformado.setCondutor(condutorDeUsuario);
        var veiculoSalvo = veiculoRepository.save(veiculoInformado);
        condutorDeUsuario.associa(veiculoSalvo);
        condutorRepository.save(condutorDeUsuario);
        return new VeiculoDTO(veiculoSalvo);
    }

    @Transactional
    public void desassociaVeiculo(String id) {
        var condutorDeUsuario = this.obterCondutorLogado();
        var veiculoPesquisado = veiculoRepository.findById(id);
        if (veiculoPesquisado.isPresent() &&
                veiculoPesquisado.get().getCondutor().getId().equals(condutorDeUsuario.getId())) {
            veiculoPesquisado.get().inativa();
            veiculoRepository.save(veiculoPesquisado.get());
            return;
        }

        throw new DataRetrievalFailureException(
                "Não foi possível identificar veículo com o id '" + id + "'");
    }

    @Transactional(readOnly = true)
    public List<VeiculoDTO> listarVeiculos() {
        var condutorDeUsuario = this.obterCondutorLogado();
        var veiculos = veiculoRepository
                .findByCondutor(condutorDeUsuario)
                .stream()
                .map(VeiculoDTO::new)
                .toList();

        if (veiculos.isEmpty()) {
            throw new DataRetrievalFailureException("Recurso não encontrado");
        }

        return veiculos;
    }

    @Transactional(readOnly = true)
    public VeiculoDTO detalharVeiculo(String id) {
        var condutorDeUsuario = this.obterCondutorLogado();
        var veiculoObtido = veiculoRepository
                .findByIdAndCondutor(id, condutorDeUsuario)
                .orElseThrow(
                        () -> new DataRetrievalFailureException("Recurso não encontrado")
                );
        return new VeiculoDTO(veiculoObtido);
    }
}
