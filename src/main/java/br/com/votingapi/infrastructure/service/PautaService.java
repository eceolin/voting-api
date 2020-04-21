package br.com.votingapi.infrastructure.service;

import br.com.votingapi.domain.model.Pauta;
import br.com.votingapi.infrastructure.persistence.repository.jpa.PautaRepository;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class PautaService {

    private final PautaRepository pautaRepository;

    public PautaService(PautaRepository pautaRepository) {
        this.pautaRepository = pautaRepository;
    }

    /**
     * Persiste a entidade no banco.
     *
     * @param pauta a ser persistida.
     * @return a pauta salva.
     */
    public Mono<Pauta> salvar(Pauta pauta) {
        return this.pautaRepository.save(pauta);
    }

    /**
     * Recupera uma pauta pelo ID.
     *
     * @param id da pauta.
     * @return os dados da pauta.
     */
    public Mono<Pauta> buscarPeloId(String id) {
        return this.pautaRepository.findById(id)
                .switchIfEmpty(Mono.error(new EmptyResultDataAccessException(1)));
    }

}
