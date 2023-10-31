package com.fiap.parquimetroapi.service;

import com.fiap.parquimetroapi.dto.UsoEstacionamentoDTO;
import com.fiap.parquimetroapi.exception.FormaPagamentoAusenteException;
import com.fiap.parquimetroapi.model.Estacionamento;
import com.fiap.parquimetroapi.model.TipoTempoEstacionado;
import com.fiap.parquimetroapi.model.UsoEstacionamento;
import com.fiap.parquimetroapi.repository.EstacionamentoRepository;
import com.fiap.parquimetroapi.repository.UsoEstacionamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;


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
    public UsoEstacionamentoDTO iniciarRegistro(UsoEstacionamentoDTO dto) {
        // Obtém condutor logado e veículo informado no dto
        var condutorLogado = condutorService.obterCondutorLogado();

        // Se não houver definido ainda forma de pagamento, lança exceção
        if (condutorLogado.getFormaPagamento() == null) {
            throw new FormaPagamentoAusenteException("É necessário selecionar forma de pagamento válida antes de estacionar");
        }

        var veiculo = condutorLogado
                .getVeiculos()
                .stream()
                .filter(v -> dto.idVeiculo().equals(v.getId()))
                .findFirst().get();

        // Otém estacionamento - cria novo caso não encontre (solução provisória)
        Estacionamento estacionamento;
        var candidatoEstacionamento = estacionamentoRepository.findByCnpj(dto.cnpjEstacionamento());
        estacionamento = candidatoEstacionamento.orElseGet(() -> new Estacionamento(
                dto.cnpjEstacionamento(),
                100L,
                new BigDecimal("9.99")));

        // preenche campos para registro do inicio do estacionamento
        var uso = new UsoEstacionamento();
        uso.setCondutor(condutorLogado);
        uso.setVeiculo(veiculo);
        uso.setEstacionamento(estacionamento);
        uso.setInicio(dto.inicio());
        uso.setTipoTempoEstacionado(TipoTempoEstacionado.valueOf(dto.tipoTempoEstacionado()));
        uso.setPago(false);

        usoEstacionamentoRepository.save(uso);

        return new UsoEstacionamentoDTO(uso);
    }
}
