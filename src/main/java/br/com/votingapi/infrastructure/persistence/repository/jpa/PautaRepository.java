package br.com.votingapi.infrastructure.persistence.repository.jpa;

import br.com.votingapi.domain.model.Pauta;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface PautaRepository extends ReactiveMongoRepository<Pauta, String> {
}
