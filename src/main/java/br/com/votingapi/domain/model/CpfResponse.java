package br.com.votingapi.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Classe auxiliar para ler a resposta do servidor.
 *
 * @author rafael.rutsatz
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public
class CpfResponse {
    private Status status;
}
