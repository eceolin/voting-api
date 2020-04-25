package br.com.votingapi.application;

import br.com.votingapi.domain.model.CpfResponse;
import br.com.votingapi.domain.model.Voto;
import reactor.core.publisher.Mono;

public interface CPFService {

    Mono<Voto> verificarSeCPFPodeVotar(Voto voto);

    Boolean podeVotar(CpfResponse cpfResponse);
}
