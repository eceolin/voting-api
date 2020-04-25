package br.com.votingapi.infrastructure.service;

import br.com.votingapi.application.CPFService;
import br.com.votingapi.application.SessaoVotacaoService;
import br.com.votingapi.domain.model.SessaoVotacao;
import br.com.votingapi.domain.model.Voto;
import br.com.votingapi.infrastructure.persistence.repository.jpa.SessaoVotacaoRepository;
import br.com.votingapi.infrastructure.persistence.repository.jpa.VotoRepository;
import br.com.votingapi.infrastructure.persistence.repository.jpa.projection.ResumoVotacao;
import br.com.votingapi.infrastructure.service.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

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
    public Flux<SessaoVotacao> listarTodas() {
        return sessaoVotacaoRepository.findAll();
    }

    @Override
    public Mono<SessaoVotacao> salvar(SessaoVotacao sessaoVotacao) {
        log.trace("Salvando uma nova sessão de votação");
        return sessaoVotacaoRepository.findByPautaId(sessaoVotacao.getPauta().getId())
                .flatMap(s -> Mono.error(new SessaoVotacaoJaCadastradaException()))
                .then(Mono.just(sessaoVotacao)
                        .map(this::inicializarDatas)
                        .map(this::verificarDatasValidas)
                        .flatMap(sessaoVotacaoRepository::save));
    }

    @Override
    public Mono<Voto> votar(String idSessao, Voto voto) {
        log.debug("Processando voto para associado {} na sessão {} ", voto.getCpfAssociado(), idSessao);
        return cpfService.verificarSeCPFPodeVotar(voto)
                .then(sessaoVotacaoRepository.findById(idSessao))
                .switchIfEmpty(Mono.error(
                        new EmptyResultDataAccessException("Sessão de Votação não encontrada.", 1)))
                .map(sessaoVotacao -> validarSessao(sessaoVotacao, voto))
                .zipWith(votoRepository.save(voto))
                .flatMap(tuple2 -> {
                    tuple2.getT1().getVotos().add(tuple2.getT2());
                    return sessaoVotacaoRepository.save(tuple2.getT1());
                })
                .thenReturn(voto);
    }

    @Override
    public Mono<ResumoVotacao> apurarResultadoVotacao(String idSessao) {
        log.debug("Apurando resultado da votação da sessão {}", idSessao);
        return buscarSessaoVotacaoPeloId(idSessao)
                .map(this::validarSessaoEncerrada)
                .map(this::montarResumo);
    }

    @Override
    public Mono<SessaoVotacao> buscarSessaoVotacaoPeloId(String idSessao) {
        return this.sessaoVotacaoRepository.findById(idSessao);
    }

    private SessaoVotacao inicializarDatas(SessaoVotacao sessaoVotacao) {
        LocalDateTime dataInicio = sessaoVotacao.getDataInicio();
        LocalDateTime dataFim = sessaoVotacao.getDataFim();

        if (dataInicio == null) {
            dataInicio = LocalDateTime.now();
            sessaoVotacao.setDataInicio(dataInicio);
        }

        if (dataFim == null) {
            dataFim = dataInicio.plus(1, ChronoUnit.MINUTES);
            sessaoVotacao.setDataFim(dataFim);
        }
        return sessaoVotacao;
    }

    private SessaoVotacao validarSessao(SessaoVotacao sessaoVotacao, Voto voto) {
        LocalDateTime now = LocalDateTime.now();
        verificaSeSessaoFoiIniciada(sessaoVotacao, now);
        verificaSeSessaoEstaEncerrada(sessaoVotacao, now);
        verificarSeAssociadoJaVotouNessaPauta(sessaoVotacao, voto);
        return sessaoVotacao;
    }

    private void verificarSeAssociadoJaVotouNessaPauta(SessaoVotacao sessaoVotacao, Voto voto) {
        sessaoVotacao.getVotos().stream()
                .filter(voto1 -> voto1.getCpfAssociado().equals(voto.getCpfAssociado()))
                .findAny()
                .ifPresent(v -> {
                    throw new AssociadoJaVotouException();
                });
    }

    private void verificaSeSessaoEstaEncerrada(SessaoVotacao sessaoVotacao, LocalDateTime now) {
        if (now.isAfter(sessaoVotacao.getDataFim())) {
            throw new SessaoVotacaoEncerradaException();
        }
    }

    private void verificaSeSessaoFoiIniciada(SessaoVotacao sessaoVotacao, LocalDateTime now) {
        if (now.isBefore(sessaoVotacao.getDataInicio())) {
            throw new SessaoVotacaoNaoIniciadaException();
        }
    }

    private SessaoVotacao verificarDatasValidas(SessaoVotacao sessaoVotacao) {
        if (sessaoVotacao.getDataInicio().isAfter(sessaoVotacao.getDataFim())) {
            throw new SessaoVotacaoDataInvalidaException();
        }
        return sessaoVotacao;
    }

    private SessaoVotacao validarSessaoEncerrada(SessaoVotacao sessaoVotacao) {
        if (sessaoVotacao.getDataInicio().isBefore(sessaoVotacao.getDataFim())) {
            throw new SessaoVotacaoNaoEncerradaException();
        }
        return sessaoVotacao;
    }

    private ResumoVotacao montarResumo(SessaoVotacao sessaoVotacao) {
        long qtdPros = sessaoVotacao.getVotos()
                .stream()
                .filter(Voto::getVoto)
                .count();

        long qtdContra = sessaoVotacao.getVotos().size() - qtdPros;

        boolean aprovado = qtdPros > qtdContra; // Maioria simples para aprovar.

        return ResumoVotacao.builder()
                .assunto(sessaoVotacao.getPauta().getAssunto())
                .pros(qtdPros)
                .contra(qtdContra)
                .aprovado(aprovado)
                .build();
    }

}
