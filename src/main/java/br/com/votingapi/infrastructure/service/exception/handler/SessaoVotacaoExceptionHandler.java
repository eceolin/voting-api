package br.com.votingapi.infrastructure.service.exception.handler;

import br.com.votingapi.infrastructure.service.exception.*;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Collections;
import java.util.List;

@ControllerAdvice
public class SessaoVotacaoExceptionHandler {

    private final MessageSource messageSource;

    public SessaoVotacaoExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler({SessaoVotacaoJaCadastradaException.class})
    public ResponseEntity<Object> handleSessaoVotacaoJaCadastradaException(SessaoVotacaoJaCadastradaException ex) {
        String mensagemUsuario = messageSource.getMessage("sessaoVotacao.ja-cadastrada", null,
                LocaleContextHolder.getLocale());
        String mensagemDesenvolvedor = ex.toString();
        List<VotingApiExceptionHandler.Erro> erros = Collections.singletonList(
                new VotingApiExceptionHandler.Erro(mensagemUsuario, mensagemDesenvolvedor));
        return ResponseEntity.badRequest().body(erros);
    }

    @ExceptionHandler({SessaoVotacaoDataInvalidaException.class})
    public ResponseEntity<Object> handleSessaoVotacaoDataInvalidaException(SessaoVotacaoDataInvalidaException ex) {
        String mensagemUsuario = messageSource.getMessage("sessaoVotacao.data-invalida", null,
                LocaleContextHolder.getLocale());
        String mensagemDesenvolvedor = ex.toString();
        List<VotingApiExceptionHandler.Erro> erros = Collections.singletonList(
                new VotingApiExceptionHandler.Erro(mensagemUsuario, mensagemDesenvolvedor));
        return ResponseEntity.badRequest().body(erros);
    }

    @ExceptionHandler({SessaoVotacaoNaoEncerradaException.class})
    public ResponseEntity<Object> handleSessaoVotacaoNaoEncerradaException(SessaoVotacaoNaoEncerradaException ex) {
        String mensagemUsuario = messageSource.getMessage("sessaoVotacao.nao-encerrada", null,
                LocaleContextHolder.getLocale());
        String mensagemDesenvolvedor = ex.toString();
        List<VotingApiExceptionHandler.Erro> erros = Collections.singletonList(
                new VotingApiExceptionHandler.Erro(mensagemUsuario, mensagemDesenvolvedor));
        return ResponseEntity.badRequest().body(erros);
    }

    @ExceptionHandler({AssociadoSemPermissaoParaVotarException.class})
    public ResponseEntity<Object> handleAssociadoSemPermissaoParaVotarException(
            AssociadoSemPermissaoParaVotarException ex) {
        String mensagemUsuario = messageSource.getMessage("voto.associado-sem-permissao", null,
                LocaleContextHolder.getLocale());
        String mensagemDesenvolvedor = ex.toString();
        List<VotingApiExceptionHandler.Erro> erros = Collections.singletonList(
                new VotingApiExceptionHandler.Erro(mensagemUsuario, mensagemDesenvolvedor));
        return ResponseEntity.badRequest().body(erros);
    }

    @ExceptionHandler({SessaoVotacaoNaoIniciadaException.class})
    public ResponseEntity<Object> handleSessaoVotacaoNaoIniciadaException(SessaoVotacaoNaoIniciadaException ex) {
        String mensagemUsuario = messageSource.getMessage("sessaoVotacao.nao-iniciada", null,
                LocaleContextHolder.getLocale());
        String mensagemDesenvolvedor = ex.toString();
        List<VotingApiExceptionHandler.Erro> erros = Collections.singletonList(
                new VotingApiExceptionHandler.Erro(mensagemUsuario, mensagemDesenvolvedor));
        return ResponseEntity.badRequest().body(erros);
    }

    @ExceptionHandler({SessaoVotacaoEncerradaException.class})
    public ResponseEntity<Object> handleSessaoVotacaoEncerradaException(SessaoVotacaoEncerradaException ex) {
        String mensagemUsuario = messageSource.getMessage("sessaoVotacao.encerrada", null,
                LocaleContextHolder.getLocale());
        String mensagemDesenvolvedor = ex.toString();
        List<VotingApiExceptionHandler.Erro> erros = Collections.singletonList(
                new VotingApiExceptionHandler.Erro(mensagemUsuario, mensagemDesenvolvedor));
        return ResponseEntity.badRequest().body(erros);
    }

    @ExceptionHandler({AssociadoJaVotouException.class})
    public ResponseEntity<Object> handleAssociadoJaVotouException(AssociadoJaVotouException ex) {
        String mensagemUsuario = messageSource.getMessage("voto.associado-ja-votou", null,
                LocaleContextHolder.getLocale());
        String mensagemDesenvolvedor = ex.toString();
        List<VotingApiExceptionHandler.Erro> erros = Collections.singletonList(
                new VotingApiExceptionHandler.Erro(mensagemUsuario, mensagemDesenvolvedor));
        return ResponseEntity.status(HttpStatus.CONFLICT).body(erros);
    }

}
