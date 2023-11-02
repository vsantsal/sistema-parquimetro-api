package com.fiap.parquimetroapi.service;

import com.fiap.parquimetroapi.dto.UsoComControleTempoDTO;
import com.fiap.parquimetroapi.dto.UsoEstacionamentoDTO;
import com.fiap.parquimetroapi.exception.FormaPagamentoAusenteException;
import com.fiap.parquimetroapi.exception.TipoTempoEstacionadoInvalido;
import com.fiap.parquimetroapi.exception.UsoEstacionamentoPagoException;
import com.fiap.parquimetroapi.exception.VeiculoInexistenteException;
import com.fiap.parquimetroapi.model.Estacionamento;
import com.fiap.parquimetroapi.model.TipoTempoEstacionado;
import com.fiap.parquimetroapi.model.UsoEstacionamento;
import com.fiap.parquimetroapi.repository.EstacionamentoRepository;
import com.fiap.parquimetroapi.repository.UsoEstacionamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;


@Service
public class UsoEstacionamentoService {

    @Autowired
    private EstacionamentoRepository estacionamentoRepository;

    @Autowired
    private CondutorService condutorService;
    @Autowired
    private UsoEstacionamentoRepository usoEstacionamentoRepository;


    /*
     * Implementação provisória para o protótipo: caso não encontre o estacionamento no repositório,
     * cria novo com valor padrão de lotação máxima igual a 100 e valor hora 9.99.
     *
     * TODO: evoluir aplicação para que o cadastro de estacionamentos habilitados seja feito em outro
     *  endpoint e usuários com role específica
     * */
    @Transactional
    public UsoComControleTempoDTO iniciarRegistro(UsoEstacionamentoDTO dto) {
        // Obtém condutor logado e veículo informado no dto
        var condutorLogado = condutorService.obterCondutorLogado();

        // Se não houver definido ainda forma de pagamento, lança exceção
        if (condutorLogado.getFormaPagamento() == null) {
            throw new FormaPagamentoAusenteException("É necessário selecionar forma de pagamento válida antes de estacionar");
        }

        // Se tipo tempo estacionado incoerente com forma de pagamento selecionada, lança exceção
        TipoTempoEstacionado tipo = EnumValidadorService.recupera(
                TipoTempoEstacionado.class, dto.tipoTempoEstacionado(), "tipoTempoEstacionado");
        if (!condutorLogado.getFormaPagamento().getTiposAceitosTempoEstacionado().contains(tipo)){
            throw new TipoTempoEstacionadoInvalido("Período de estacionamento inválido para forma de pagamento");
        }

        // Se tipo tempo estacionado fixo, verificar se duração foi informada
        if (tipo == TipoTempoEstacionado.FIXO && dto.duracao() == null) {
            throw new TipoTempoEstacionadoInvalido("Necessário informar duração para tipoTempoEstacionado 'FIXO'");
        }

        // Obtém veículo a partir de placa informada, validando se está associado ao condutor logado
        var veiculo = condutorLogado
                .getVeiculos()
                .stream()
                .filter(v -> dto.placaVeiculo().equals(v.getPlaca().toString()))
                .findFirst().orElseThrow(
                        () -> new VeiculoInexistenteException(
                                "Não foi possível localizar o veículo correspondente à placa '" +
                                        dto.placaVeiculo() + "'"
                        )
                );

        // Otém estacionamento - cria novo caso não encontre (solução provisória)
        Estacionamento estacionamento;
        var candidatoEstacionamento = estacionamentoRepository.findByCnpj(dto.cnpjEstacionamento());
        estacionamento = candidatoEstacionamento.orElseGet(() -> new Estacionamento(
                dto.cnpjEstacionamento(),
                100L,
                new BigDecimal("9.99")));

        // durações iniciais
        Duration duracaoRealizada = Duration.between(
                dto.inicio(), LocalDateTime.now()
        );
        Duration duracaoEsperada;
        if (dto.duracao() == null){
            duracaoEsperada = duracaoRealizada;
        } else {
            duracaoEsperada = Duration.between(
                    LocalTime.MIN,
                    LocalTime.parse(dto.duracao())
            );
        }

        // preenche campos para registro do inicio do estacionamento
        var uso = new UsoEstacionamento();
        uso.setCondutor(condutorLogado);
        uso.setVeiculo(veiculo);
        uso.setEstacionamento(estacionamento);
        uso.setInicio(dto.inicio());
        uso.setTipoTempoEstacionado(TipoTempoEstacionado.valueOf(dto.tipoTempoEstacionado()));
        uso.setDuracaoEfetiva(duracaoRealizada);
        uso.setDuracaoEsperada(duracaoEsperada);
        uso.setPago(false);

        usoEstacionamentoRepository.save(uso);

        return new UsoComControleTempoDTO(uso);
    }

    @Transactional(readOnly = true)
    public UsoComControleTempoDTO detalhar(String id, LocalDateTime hora) {
        // Mensagem de erro
        String mensagem = "Não foi possível identificar o uso com id + '" + id + "'";
        // Obtém condutor logado
        var condutorLogado = condutorService.obterCondutorLogado();
        // Obtém usoEstacionamentoSolicitado
        var uso = this.usoEstacionamentoRepository.findById(id);
        uso.ifPresentOrElse(
                (valor) -> {
                    if (valor.getCondutor().getId().equals(condutorLogado.getId())) {
                        valor.monitora(hora);
                    } else {
                        throw new DataRetrievalFailureException(mensagem);
                    }
                },
                () -> new DataRetrievalFailureException(mensagem)
        );
        var usoASalvar = uso.get();
        usoEstacionamentoRepository.save(usoASalvar);
        return new UsoComControleTempoDTO(usoASalvar);

    }

    @Transactional(readOnly = true)
    public Page<UsoComControleTempoDTO> listar(Pageable paginacao) {
        // Obtém condutor logado
        var condutorLogado = condutorService.obterCondutorLogado();
        // Retorna histórico
        return usoEstacionamentoRepository
                .findAllByCondutor(condutorLogado, paginacao)
                .map(UsoComControleTempoDTO::new);
    }

    @Transactional
    public UsoComControleTempoDTO pagar(String id) {
        // Mensagem de erro
        String mensagem = "Não foi possível identificar o uso com id + '" + id + "'";
        // Obtém condutor logado
        var condutorLogado = condutorService.obterCondutorLogado();
        var uso = usoEstacionamentoRepository.findById(id);
        if (uso.isEmpty()) {
            throw new DataRetrievalFailureException(mensagem);
        }
        var usoPresente = uso.get();
        if (!usoPresente.getCondutor().getId().equals(condutorLogado.getId())){
            throw new DataRetrievalFailureException(mensagem);
        }
        if (usoPresente.isPago()) {
            throw new UsoEstacionamentoPagoException("Uso já pago anteriormente");
        }
        usoPresente.encerra(LocalDateTime.now());
        usoEstacionamentoRepository.save(usoPresente);
        return new UsoComControleTempoDTO(usoPresente);
    }
}
