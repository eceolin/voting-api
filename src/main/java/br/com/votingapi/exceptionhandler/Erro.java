package br.com.votingapi.exceptionhandler;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Representa o erro que é retornado ao usuário.
 *
 * @author rafael.rutsatz
 *
 */
@Data
@AllArgsConstructor
public class Erro {

	/**
	 * Mensagem mais intuitiva, para ser apresentada ao usuário.
	 */
	private String mensagemUsuario;

	/**
	 * Mensagem para o desenvolver, para ajudá-lo a encontrar o problema.
	 */
	private String mensagemDesenvolvedor;

}
