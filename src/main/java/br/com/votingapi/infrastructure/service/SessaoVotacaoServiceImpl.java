package br.com.votingapi.infrastructure.service;

import br.com.votingapi.application.CPFService;
import br.com.votingapi.application.SessaoVotacaoService;
import br.com.votingapi.domain.model.SessaoVotacao;
import br.com.votingapi.domain.model.Voto;
import br.com.votingapi.infrastructure.api.rest.dto.VotoDTO;
import br.com.votingapi.infrastructure.persistence.repository.jpa.SessaoVotacaoRepository;
import br.com.votingapi.infrastructure.persistence.repository.jpa.VotoRepository;
import br.com.votingapi.infrastructure.persistence.repository.jpa.projection.ResumoVotacao;
import br.com.votingapi.infrastructure.service.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Service usado para regras relacionadas as sessões de votação.
 *
 * @author rafael.rutsatz
 */
@Slf4j
@Service
public class SessaoVotacaoServiceImpl implements SessaoVotacaoService {

    private final SessaoVotacaoRepository sessaoVotacaoRepository;
    private final VotoRepository votoRepository;
    private final CPFService cpfService;

    public SessaoVotacaoServiceImpl(SessaoVotacaoRepository sessaoVotacaoRepository,
                                    VotoRepository votoRepository, CPFService cpfService) {
        this.sessaoVotacaoRepository = sessaoVotacaoRepository;
        this.votoRepository = votoRepository;
        this.cpfService = cpfService;
    }

    @Override
    public Mono<SessaoVotacao> salvar(SessaoVotacao sessaoVotacao) {
        log.trace("Salvando uma nova sessão de votação");
        return sessaoVotacaoRepository.findByPautaId(sessaoVotacao.getPauta().getId())
                .flatMap(s -> Mono.error(new SessaoVotacaoJaCadastradaException()))
                .then(Mono.just(validar.apply(sessaoVotacao))
                        .flatMap(sessaoVotacaoRepository::save));
    }

    public final Function<SessaoVotacao, SessaoVotacao> validar = sessaoVotacao -> {
        LocalDateTime dataInicio = sessaoVotacao.getDataInicio();

        if (dataInicio == null) {
            dataInicio = LocalDateTime.now();
            sessaoVotacao.setDataInicio(dataInicio);
        }

        LocalDateTime dataFim = sessaoVotacao.getDataFim();
        if (dataFim == null) {
            dataFim = dataInicio.plus(1, ChronoUnit.MINUTES);
            sessaoVotacao.setDataFim(dataFim);
        }

        // Data inválida. Data inicial maior que a final.
        if (dataInicio.isAfter(dataFim)) {
            throw new SessaoVotacaoDataInvalidaException();
        }
        return sessaoVotacao;
    };

    @Override
    public Mono<Voto> votar(String idSessao, VotoDTO votoDTO) {
        log.debug("Processando voto para associado {} na sessão {} ", votoDTO.getCpfAssociado(), idSessao);

        Voto voto = Voto.builder()
                .cpfAssociado(votoDTO.getCpfAssociado())
                .voto(votoDTO.getVoto())
                .build();

        return cpfService.verificarSeCPFPodeVotar(votoDTO)
                .then(sessaoVotacaoRepository.findById(idSessao))
                .switchIfEmpty(Mono.error(
                        new EmptyResultDataAccessException("Sessão de Votação não encontrada.", 1)))
                .map(sessaoVotacao -> validarSessao.apply(sessaoVotacao, votoDTO))
                .zipWith(votoRepository.save(voto))
                .flatMap(tuple2 -> {
                    tuple2.getT1().getVotos().add(tuple2.getT2());
                    return sessaoVotacaoRepository.save(tuple2.getT1());
                })
                .thenReturn(voto);
    }

    private final Function<SessaoVotacao, SessaoVotacao> validarDatas = sessaoVotacao -> {
        LocalDateTime dataInicio = sessaoVotacao.getDataInicio();
        LocalDateTime dataFim = sessaoVotacao.getDataFim();
        // Data inválida. Data inicial maior que a final.
        if (dataInicio.isBefore(dataFim)) {
            throw new SessaoVotacaoNaoEncerradaException();
        }
        return sessaoVotacao;
    };

    public final Function<SessaoVotacao, ResumoVotacao> montarResumo = sessaoVotacao -> {
        Long pros = sessaoVotacao.getVotos().stream()
                .filter(Voto::getVoto)
                .count();
        Long contra = sessaoVotacao.getVotos().size() - pros;
        return ResumoVotacao.builder()
                .pros(pros)
                .contra(contra)
                .assunto(sessaoVotacao.getPauta().getAssunto())
                .aprovado(pros > contra) // Maioria simples para aprovar.
                .build();
    };

    @Override
    public Mono<ResumoVotacao> apurarResultado(String idSessao) {
        log.debug("Apurando resultado da votação da sessão {}", idSessao);
        return buscarSessaoVotacaoPeloId(idSessao)
                .map(validarDatas)
                .map(montarResumo);
    }

    @Override
    public Mono<SessaoVotacao> buscarSessaoVotacaoPeloId(String idSessao) {
        return this.sessaoVotacaoRepository.findById(idSessao);
    }

    public BiFunction<SessaoVotacao, VotoDTO, SessaoVotacao> validarSessao = (sessaoVotacao, votoDTO) -> {
        LocalDateTime now = LocalDateTime.now();
        // Verifica se a sessão já foi criada e se já iniciou.
        if (now.isBefore(sessaoVotacao.getDataInicio())) {
            throw new SessaoVotacaoNaoIniciadaException();
        }
        if (now.isAfter(sessaoVotacao.getDataFim())) {
            throw new SessaoVotacaoEncerradaException();
        }
        // verificar se o associado já votou nessa pauta.
        sessaoVotacao.getVotos().stream()
                .filter(voto -> voto.getCpfAssociado().equals(votoDTO.getCpfAssociado()))
                .findAny()
                .ifPresent(v -> {
                    throw new AssociadoJaVotouException();
                });
        return sessaoVotacao;
    };

}
