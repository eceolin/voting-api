package br.com.votingapi.infrastructure.service;

import br.com.votingapi.infrastructure.api.rest.dto.VotoDTO;
import br.com.votingapi.infrastructure.service.exception.AssociadoSemPermissaoParaVotarException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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
public class CPFService {

    private static final String CPF_API_URL = "http://user-info.herokuapp.com/users/{cpf}";

    /**
     * Verifica se o associado pode votar, usando uma API externa. Caso a API
     * estiver fora, por padrão, o acesso é negado.
     *
     * @param votoDTO do associado.
     * @return true se o associado pode votar.
     */
    public Mono<VotoDTO> verificarSeCPFPodeVotar(VotoDTO votoDTO) {
        return WebClient.create().get().uri(CPF_API_URL, votoDTO.getCpfAssociado())
                .retrieve()
                .bodyToMono(CpfResponse.class)
                .filter(this::podeVotar)
                .map(cpfResponse -> votoDTO)
                .switchIfEmpty(Mono.error(new AssociadoSemPermissaoParaVotarException()));
    }

    public Boolean podeVotar(CpfResponse cpfResponse) {
        return cpfResponse.getStatus() == Status.ABLE_TO_VOTE;
    }
}

/**
 * Classe auxiliar para ler a resposta do servidor.
 *
 * @author rafael.rutsatz
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
class CpfResponse {
    private Status status;
}

enum Status {
    ABLE_TO_VOTE, UNABLE_TO_VOTE
}

