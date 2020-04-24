package br.com.votingapi.application;

import br.com.votingapi.domain.model.Pauta;
import reactor.core.publisher.Mono;

public interface PautaService {
    /**
     * Persiste a entidade no banco.
     *
     * @param pauta a ser persistida.
     * @return a pauta salva.
     */
    Mono<Pauta> salvar(Pauta pauta);

    /**
     * Recupera uma pauta pelo ID.
     *
     * @param id da pauta.
     * @return os dados da pauta.
     */
    Mono<Pauta> buscarPeloId(String id);
}
