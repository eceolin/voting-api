package br.com.votingapi.application;

import br.com.votingapi.domain.model.Pauta;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PautaService {

    Flux<Pauta> listarTodas();

    Mono<Pauta> salvar(Pauta pauta);

    Mono<Pauta> buscarPeloId(String id);
}
