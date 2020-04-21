package br.com.votingapi.infrastructure.service.exception.handler;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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
     * Captura as mensagens que o Spring não conseguiu ler.
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        // Recupera a mensagem do arquivo de properties. Passa o código da mensagem,
        // null pois não tem tenhum parâmetro adicional e o Locale corrente da
        // aplicação.
        String mensagemUsuario = messageSource.getMessage("mensagem.invalida", null, LocaleContextHolder.getLocale());
        // Adiciona uma mensagem para o desenvolvedor, que está consumindo a API.
        String mensagemDesenvolvedor = Optional.ofNullable(ex.getCause()).orElse(ex).toString();

        List<Erro> erros = Arrays.asList(new Erro(mensagemUsuario, mensagemDesenvolvedor));

        // Peço para o Spring tratar a exceção. Só que eu consigo passar um body.
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erros);
    }

    /**
     * Trata as mensagens quando o argumentos de um método ou request não são
     * válidos. Para ele validar, é necessário estar anotado com o @Valid.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {

        List<Erro> erros = criarListaDeErros(ex.getBindingResult());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erros);
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
        List<Erro> erros = Arrays.asList(new Erro(mensagemUsuario, mensagemDesenvolvedor));

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erros);
    }

    /**
     * Trata para os casos de campos inválidos, pois a validação pode ter falhado em
     * vários campos.
     *
     * @return Lista de erros.
     */
    private List<Erro> criarListaDeErros(BindingResult bindingResult) {
        List<Erro> erros = new ArrayList<>();

        // Percorre todos os atributos que foram validados na entidade.
        bindingResult.getFieldErrors().forEach(fieldError -> {
            // Usa o messageSource passando direto o fieldError, pois lá no arquivo de
            // properties tratamos a mensagem para cada campo da entidade, e assim ele
            // consegue recuperar as mensagens pelo código do @NotNull.
            String mensagemUsuario = messageSource.getMessage(fieldError, LocaleContextHolder.getLocale());
            String mensagemDesenvolvedor = fieldError.toString();
            erros.add(new Erro(mensagemUsuario, mensagemDesenvolvedor));
        });
        return erros;
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
