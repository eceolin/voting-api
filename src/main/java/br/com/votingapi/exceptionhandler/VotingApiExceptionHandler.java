package br.com.votingapi.exceptionhandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * ResponseEntityExceptionHandler captura exceções de resposta de entidades.
 * Adiciono o @ControllerAdvice, que faz com que a classe escute toda a
 * aplicação.
 * 
 * @author rafael.rutsatz
 *
 */
@ControllerAdvice
public class VotingApiExceptionHandler extends ResponseEntityExceptionHandler {

	/**
	 * Busca as mensagens lá do messages.properties.
	 */
	@Autowired
	private MessageSource messageSource;

}
