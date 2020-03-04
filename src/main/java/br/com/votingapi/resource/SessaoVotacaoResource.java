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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.votingapi.exceptionhandler.Erro;
import br.com.votingapi.model.SessaoVotacao;
import br.com.votingapi.repository.SessaoVotacaoRepository;
import br.com.votingapi.repository.projection.ResumoVotacao;
import br.com.votingapi.service.SessaoVotacaoService;
import br.com.votingapi.service.exception.SessaoVotacaoDataInvalidaException;
import br.com.votingapi.service.exception.SessaoVotacaoJaCadastradaException;
import br.com.votingapi.service.exception.SessaoVotacaoNaoEncerradaException;

/**
 * Controller responsável por tratar as requisições relacionadas as sessões de
 * votação.
 *
 * @author rafael.rutsatz
 *
 */
@RestController
@RequestMapping("/v1/sessoes")
public class SessaoVotacaoResource {

	@Autowired
	private SessaoVotacaoRepository sessaoVotacaoRepository;

	@Autowired
	private SessaoVotacaoService sessaoVotacaoService;

	@Autowired
	private MessageSource messageSource;

	/**
	 * Lista todas as sessões.
	 *
	 * @return Lista de sessões.
	 */
	@GetMapping
	public List<SessaoVotacao> listar() {
		return sessaoVotacaoRepository.findAll();
	}

	/**
	 * Cria uma nova sessão de votação. É utilizado o próprio model pois não existem
	 * campos que precisam ser escondidos do usuário.
	 *
	 * @param sessaoVotacao com os dados a serem cadastrados.
	 * @return nova sessão que foi cadastrada.
	 */
	@PostMapping
	public ResponseEntity<SessaoVotacao> criar(@Valid @RequestBody SessaoVotacao sessaoVotacao) {
		SessaoVotacao sessaoVotacaoSalva = sessaoVotacaoService.salvar(sessaoVotacao);
		return ResponseEntity.status(HttpStatus.CREATED).body(sessaoVotacaoSalva);
	}

	/**
	 * Recupera uma sessao de votação pelo id.
	 *
	 * @param codigo da sessao de votacao
	 * @return A sessao de votacao encontrada
	 */
	@GetMapping("/{codigo}")
	public ResponseEntity<?> buscarPeloCodigo(@PathVariable Long codigo) {
		return this.sessaoVotacaoRepository.findById(codigo).map(sessaoVotacao -> ResponseEntity.ok(sessaoVotacao))
				.orElse(ResponseEntity.notFound().build());
	}

	/**
	 * Contibiliza os votos e retorna o resultado.
	 *
	 * @param lancamentoFilter
	 * @param pageable
	 * @return
	 */
	@GetMapping("/{codigo}/resultado")
	public ResumoVotacao apurarVotos(@PathVariable Long codigo) {
		return sessaoVotacaoService.resultado(codigo);
	}

	@ExceptionHandler({ SessaoVotacaoJaCadastradaException.class })
	public ResponseEntity<Object> handleSessaoVotacaoJaCadastradaException(SessaoVotacaoJaCadastradaException ex) {
		String mensagemUsuario = messageSource.getMessage("sessaoVotacao.ja-cadastrada", null,
				LocaleContextHolder.getLocale());
		String mensagemDesenvolvedor = ex.toString();
		List<Erro> erros = Arrays.asList(new Erro(mensagemUsuario, mensagemDesenvolvedor));
		return ResponseEntity.badRequest().body(erros);
	}

	@ExceptionHandler({ SessaoVotacaoDataInvalidaException.class })
	public ResponseEntity<Object> handleSessaoVotacaoDataInvalidaException(SessaoVotacaoDataInvalidaException ex) {
		String mensagemUsuario = messageSource.getMessage("sessaoVotacao.data-invalida", null,
				LocaleContextHolder.getLocale());
		String mensagemDesenvolvedor = ex.toString();
		List<Erro> erros = Arrays.asList(new Erro(mensagemUsuario, mensagemDesenvolvedor));
		return ResponseEntity.badRequest().body(erros);
	}

	@ExceptionHandler({ SessaoVotacaoNaoEncerradaException.class })
	public ResponseEntity<Object> handleSessaoVotacaoNaoEncerradaException(SessaoVotacaoNaoEncerradaException ex) {
		String mensagemUsuario = messageSource.getMessage("sessaoVotacao.nao-encerrada", null,
				LocaleContextHolder.getLocale());
		String mensagemDesenvolvedor = ex.toString();
		List<Erro> erros = Arrays.asList(new Erro(mensagemUsuario, mensagemDesenvolvedor));
		return ResponseEntity.badRequest().body(erros);
	}

}
