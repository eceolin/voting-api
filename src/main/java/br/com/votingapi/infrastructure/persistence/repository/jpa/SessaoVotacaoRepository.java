package br.com.votingapi.infrastructure.persistence.repository.jpa;

import br.com.votingapi.domain.model.SessaoVotacao;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface SessaoVotacaoRepository extends ReactiveMongoRepository<SessaoVotacao, String> {

    Mono<SessaoVotacao> findByPautaId(String idPauta);

}
