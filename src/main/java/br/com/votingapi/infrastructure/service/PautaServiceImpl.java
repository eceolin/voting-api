package br.com.votingapi.infrastructure.service;

import br.com.votingapi.application.PautaService;
import br.com.votingapi.domain.model.Pauta;
import br.com.votingapi.infrastructure.persistence.repository.jpa.PautaRepository;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class PautaServiceImpl implements PautaService {

    private final PautaRepository pautaRepository;

    public PautaServiceImpl(PautaRepository pautaRepository) {
        this.pautaRepository = pautaRepository;
    }

    @Override
    public Flux<Pauta> listarTodas() {
        return pautaRepository.findAll();
    }

    @Override
    public Mono<Pauta> salvar(Pauta pauta) {
        return this.pautaRepository.save(pauta);
    }

    @Override
    public Mono<Pauta> buscarPeloId(String id) {
        return this.pautaRepository.findById(id)
                .switchIfEmpty(Mono.error(new EmptyResultDataAccessException(1)));
    }

}
