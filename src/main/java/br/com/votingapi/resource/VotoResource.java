package br.com.votingapi.resource;

import java.util.Arrays;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.votingapi.dto.VotoDTO;
import br.com.votingapi.exceptionhandler.Erro;
import br.com.votingapi.model.Voto;
import br.com.votingapi.service.VotoService;
import br.com.votingapi.service.exception.AssociadoJaVotouException;
import br.com.votingapi.service.exception.AssociadoSemPermissaoParaVotarException;
import br.com.votingapi.service.exception.SessaoVotacaoEncerradaException;
import br.com.votingapi.service.exception.SessaoVotacaoNaoIniciadaException;

/**
 * Controller responsável por tratar as requisições relacionadas aos votos
 * feitos pelos associados.
 *
 * @author rafael.rutsatz
 *
 */
@RestController
@RequestMapping("/api/v1/votos")
public class VotoResource {

	@Autowired
	private VotoService votoService;

	@Autowired
	private MessageSource messageSource;

	/**
	 * Processa o voto de um associado.
	 *
	 * @param votoDTO DTO contendo os dados de votação.
	 * @return ResponseEntity enviado ao usuário.
	 */
	@PostMapping
	public ResponseEntity<Voto> criar(@Valid @RequestBody VotoDTO votoDTO) {
		Voto votoSalvo = votoService.salvar(votoDTO);
		return ResponseEntity.status(HttpStatus.CREATED).body(votoSalvo);
	}

	/**
	 * Como essa exception é exclusiva do voto, eu não coloco no ControllerAdvice,
	 * eu posso deixar somente nessa classe.
	 *
	 * @param ex Exception que foi lançada.
	 * @return ResponseEntity enviado ao usuário.
	 */
	@ExceptionHandler({ SessaoVotacaoNaoIniciadaException.class })
	public ResponseEntity<Object> handleSessaoVotacaoNaoIniciadaException(SessaoVotacaoNaoIniciadaException ex) {
		String mensagemUsuario = messageSource.getMessage("sessaoVotacao.nao-iniciada", null,
				LocaleContextHolder.getLocale());
		String mensagemDesenvolvedor = ex.toString();
		List<Erro> erros = Arrays.asList(new Erro(mensagemUsuario, mensagemDesenvolvedor));
		return ResponseEntity.badRequest().body(erros);
	}

	/**
	 * Método que trata a exception quando é feita uma tentativa de voto após o
	 * término da sessão.
	 *
	 * @param ex Exception que foi lançada.
	 * @return ResponseEntity enviado ao usuário.
	 */
	@ExceptionHandler({ SessaoVotacaoEncerradaException.class })
	public ResponseEntity<Object> handleSessaoVotacaoEncerradaException(SessaoVotacaoEncerradaException ex) {
		String mensagemUsuario = messageSource.getMessage("sessaoVotacao.encerrada", null,
				LocaleContextHolder.getLocale());
		String mensagemDesenvolvedor = ex.toString();
		List<Erro> erros = Arrays.asList(new Erro(mensagemUsuario, mensagemDesenvolvedor));
		return ResponseEntity.badRequest().body(erros);
	}

	/**
	 * Método que trata a exception quando o associado já votou na pauta.
	 *
	 * @param ex Exception que foi lançada.
	 * @return ResponseEntity enviado ao usuário.
	 */
	@ExceptionHandler({ AssociadoJaVotouException.class })
	public ResponseEntity<Object> handleAssociadoJaVotouException(AssociadoJaVotouException ex) {
		String mensagemUsuario = messageSource.getMessage("voto.associado-ja-votou", null,
				LocaleContextHolder.getLocale());
		String mensagemDesenvolvedor = ex.toString();
		List<Erro> erros = Arrays.asList(new Erro(mensagemUsuario, mensagemDesenvolvedor));
		return ResponseEntity.badRequest().body(erros);
	}

	/**
	 * Método que trata a exception quando o associado não tem permissão para votar.
	 *
	 * @param ex Exception que foi lançada.
	 * @return ResponseEntity enviado ao usuário.
	 */
	@ExceptionHandler({ AssociadoSemPermissaoParaVotarException.class })
	public ResponseEntity<Object> handleAssociadoSemPermissaoParaVotarException(
			AssociadoSemPermissaoParaVotarException ex) {
		String mensagemUsuario = messageSource.getMessage("voto.associado-sem-permissao", null,
				LocaleContextHolder.getLocale());
		String mensagemDesenvolvedor = ex.toString();
		List<Erro> erros = Arrays.asList(new Erro(mensagemUsuario, mensagemDesenvolvedor));
		return ResponseEntity.badRequest().body(erros);
	}

}
