package br.com.votingapi.application;

import br.com.votingapi.domain.model.CpfResponse;
import br.com.votingapi.infrastructure.api.rest.dto.VotoDTO;
import reactor.core.publisher.Mono;

public interface CPFService {
    /**
     * Verifica se o associado pode votar, usando uma API externa. Caso a API
     * estiver fora, por padrão, o acesso é negado.
     *
     * @param votoDTO do associado.
     * @return true se o associado pode votar.
     */
    Mono<VotoDTO> verificarSeCPFPodeVotar(VotoDTO votoDTO);

    Boolean podeVotar(CpfResponse cpfResponse);
}
