package br.com.votingapi.exceptionhandler;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Representa o erro que é retornado.
 *
 * @author rafael.rutsatz
 *
 */
@Data
@AllArgsConstructor
public class Erro {

	private String mensagemUsuario;
	private String mensagemDesenvolvedor;

}
