package br.com.votingapi.application;

import br.com.votingapi.domain.model.SessaoVotacao;
import br.com.votingapi.domain.model.Voto;
import br.com.votingapi.infrastructure.persistence.repository.jpa.projection.ResumoVotacao;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface SessaoVotacaoService {

    Flux<SessaoVotacao> listarTodas();

    Mono<SessaoVotacao> salvar(SessaoVotacao sessaoVotacao);

    Mono<Voto> votar(String idSessao, Voto voto);

    Mono<ResumoVotacao> apurarResultadoVotacao(String idSessao);

    Mono<SessaoVotacao> buscarSessaoVotacaoPeloId(String idSessao);

}
