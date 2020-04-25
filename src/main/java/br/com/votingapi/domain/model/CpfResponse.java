package br.com.votingapi.domain.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Classe auxiliar para ler a resposta da API de autorização para votar.
 *
 * @author rafael.rutsatz
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CpfResponse {
    @JsonAlias("status")
    private AssociateVotePermission associateVotePermission;
}
