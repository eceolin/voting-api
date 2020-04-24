package br.com.votingapi.infrastructure.service.exception.handler;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Collections;
import java.util.List;

/**
 * ResponseEntityExceptionHandler captura exceções de resposta de entidades.
 * Adiciono o @ControllerAdvice, que faz com que a classe escute toda a
 * aplicação.
 *
 * @author rafael.rutsatz
 */
@ControllerAdvice
public class VotingApiExceptionHandler {

    /**
     * Busca as mensagens lá do messages.properties.
     */
    private final MessageSource messageSource;

    public VotingApiExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    /**
     * Não existe um método para sobrescrever que trate a
     * EmptyResultDataAccessException. Então crio meu próprio método e digo que ele
     * irá tratar essa Exceção.
     */
    @ExceptionHandler({EmptyResultDataAccessException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Object> handleEmptyResultDataAccessException(EmptyResultDataAccessException ex) {
        String mensagemUsuario = messageSource.getMessage("recurso.nao-encontrado", null,
                LocaleContextHolder.getLocale());
        String mensagemDesenvolvedor = ex.toString();
        List<Erro> erros = Collections.singletonList(new Erro(mensagemUsuario, mensagemDesenvolvedor));

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erros);
    }

    /**
     * Representa o erro que é retornado ao usuário.
     *
     * @author rafael.rutsatz
     */
    @Data
    @AllArgsConstructor
    public static class Erro {
        /**
         * Mensagem mais intuitiva, para ser apresentada ao usuário.
         */
        private String mensagemUsuario;

        /**
         * Mensagem para o desenvolver, para ajudá-lo a encontrar o problema.
         */
        private String mensagemDesenvolvedor;
    }
}
