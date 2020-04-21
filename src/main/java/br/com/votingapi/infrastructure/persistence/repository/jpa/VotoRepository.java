package br.com.votingapi.infrastructure.persistence.repository.jpa;

import br.com.votingapi.domain.model.Voto;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface VotoRepository extends ReactiveMongoRepository<Voto, String> {
}
