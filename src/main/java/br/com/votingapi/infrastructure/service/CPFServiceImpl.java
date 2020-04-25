package br.com.votingapi.infrastructure.service;

import br.com.votingapi.application.CPFService;
import br.com.votingapi.domain.model.AssociateVotePermission;
import br.com.votingapi.domain.model.CpfResponse;
import br.com.votingapi.domain.model.Voto;
import br.com.votingapi.infrastructure.service.exception.AssociadoSemPermissaoParaVotarException;
import org.springframework.beans.factory.annotation.Value;
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

    private final String cpfApiUrl;

    public CPFServiceImpl(@Value("${cpf-api-url}") String cpfApiUrl) {
        this.cpfApiUrl = cpfApiUrl;
    }

    @Override
    public Mono<Voto> verificarSeCPFPodeVotar(Voto voto) {
        return WebClient.create().get().uri(cpfApiUrl, voto.getCpfAssociado())
                .retrieve()
                .bodyToMono(CpfResponse.class)
                .filter(this::podeVotar)
                .map(cpfResponse -> voto)
                .switchIfEmpty(Mono.error(new AssociadoSemPermissaoParaVotarException()));
    }

    @Override
    public Boolean podeVotar(CpfResponse cpfResponse) {
        return cpfResponse.getAssociateVotePermission() == AssociateVotePermission.ABLE_TO_VOTE;
    }
}

