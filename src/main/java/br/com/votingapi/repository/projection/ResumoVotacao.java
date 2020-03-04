package br.com.votingapi.repository.projection;

import lombok.Builder;
import lombok.Data;

/**
 * Dados do resumidos da votação, contendo o resultado após a apuração dos
 * votos.
 *
 * @author rafael.rutsatz
 *
 */
@Data
@Builder
public class ResumoVotacao {

	/**
	 * Descrição do assunto discutido na pauta.
	 */
	private String assunto;

	/**
	 * Quantidade de votos a favor da pauta.
	 */
	private Integer pros;

	/**
	 * Quantidade de votos contra a pauta.
	 */
	private Integer contra;

	/**
	 * Indica se a pauta foi aprovada ou não.
	 */
	private Boolean aprovado;

}
