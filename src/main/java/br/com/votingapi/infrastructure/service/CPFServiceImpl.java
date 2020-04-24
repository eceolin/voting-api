package br.com.votingapi.infrastructure.service;

import br.com.votingapi.application.CPFService;
import br.com.votingapi.domain.model.CpfResponse;
import br.com.votingapi.domain.model.Status;
import br.com.votingapi.infrastructure.api.rest.dto.VotoDTO;
import br.com.votingapi.infrastructure.service.exception.AssociadoSemPermissaoParaVotarException;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * Service responsável por consumir a API de CPF e verificar se é válido para
 * votar.
 *
 * @author rafael.rutsatz
 */
@Component
public class CPFServiceImpl implements CPFService {

    private static final String CPF_API_URL = "http://user-info.herokuapp.com/users/{cpf}";

    @Override
    public Mono<VotoDTO> verificarSeCPFPodeVotar(VotoDTO votoDTO) {
        return WebClient.create().get().uri(CPF_API_URL, votoDTO.getCpfAssociado())
                .retrieve()
                .bodyToMono(CpfResponse.class)
                .filter(this::podeVotar)
                .map(cpfResponse -> votoDTO)
                .switchIfEmpty(Mono.error(new AssociadoSemPermissaoParaVotarException()));
    }

    @Override
    public Boolean podeVotar(CpfResponse cpfResponse) {
        return cpfResponse.getStatus() == Status.ABLE_TO_VOTE;
    }
}

