package br.com.votingapi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.TimeZone;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import br.com.votingapi.model.Pauta;
import br.com.votingapi.model.SessaoVotacao;
import br.com.votingapi.repository.SessaoVotacaoRepository;
import br.com.votingapi.service.PautaService;
import br.com.votingapi.service.SessaoVotacaoService;
import br.com.votingapi.service.exception.SessaoVotacaoDataInvalidaException;
import br.com.votingapi.service.exception.SessaoVotacaoJaCadastradaException;

/**
 * Classe para testar os métodos do de criação das sessões de votação.
 *
 * @author rafael.rutsatz
 *
 */
@SpringBootTest
public class SessaoVotacaoServiceTests {

	@InjectMocks
	private SessaoVotacaoService sessaoVotacaoService = new SessaoVotacaoService();

	@Mock
	private PautaService pautaService;

	@Mock
	private SessaoVotacaoRepository sessaoVotacaoRepository;

	@Mock
	private static EntityManager em;

	@Test
	public void shouldCreateVotingSessionWhenNotExists() {

		Long codigoPauta = 1l;

		Pauta pauta = new Pauta();
		pauta.setCodigo(codigoPauta);

		Long codigoSessao = 1l;
		SessaoVotacao sessaoVotacao = new SessaoVotacao();
		sessaoVotacao.setCodigo(codigoSessao);
		sessaoVotacao.setPauta(pauta);

		when(pautaService.buscarPautaPeloCodigo(codigoPauta)).thenReturn(pauta);
		when(sessaoVotacaoRepository.findByPautaCodigo(codigoPauta)).thenReturn(Optional.empty());
		when(sessaoVotacaoRepository.save(sessaoVotacao)).thenReturn(sessaoVotacao);

		SessaoVotacao sessaoVotacaoSalva = sessaoVotacaoService.salvar(sessaoVotacao);

		assertEquals(sessaoVotacao, sessaoVotacaoSalva, "Erro ao salvar sessão de votação.");
	}

	@Test
	public void shouldNotCreateVotingSessionWhenAlreadyExists() {

		Long codigoPauta = 1l;
		Pauta pauta = new Pauta();
		pauta.setCodigo(codigoPauta);

		Long codigoSessao = 1l;
		SessaoVotacao sessaoVotacao = new SessaoVotacao();
		sessaoVotacao.setCodigo(codigoSessao);
		sessaoVotacao.setPauta(pauta);

		when(pautaService.buscarPautaPeloCodigo(codigoPauta)).thenReturn(pauta);
		when(sessaoVotacaoRepository.findByPautaCodigo(codigoPauta)).thenReturn(Optional.of(sessaoVotacao));
		when(sessaoVotacaoRepository.save(sessaoVotacao)).thenReturn(sessaoVotacao);

		assertThrows(SessaoVotacaoJaCadastradaException.class, () -> sessaoVotacaoService.salvar(sessaoVotacao),
				"Permitiu salvar uma sessão de votação duplicada.");
	}

	@Test
	public void shouldNotCreateVotingSessionWhitInvalidDates() {

		Long codigoPauta = 1l;
		Pauta pauta = new Pauta();
		pauta.setCodigo(codigoPauta);

		Long codigoSessao = 1l;
		SessaoVotacao sessaoVotacao = new SessaoVotacao();
		sessaoVotacao.setCodigo(codigoSessao);
		sessaoVotacao.setPauta(pauta);

		LocalDateTime now = LocalDateTime.now();
		sessaoVotacao.setDataInicio(now);
		sessaoVotacao.setDataFim(now.minus(1, ChronoUnit.MINUTES));

		when(pautaService.buscarPautaPeloCodigo(codigoPauta)).thenReturn(pauta);
		when(sessaoVotacaoRepository.findByPautaCodigo(codigoPauta)).thenReturn(Optional.empty());
		when(sessaoVotacaoRepository.save(sessaoVotacao)).thenReturn(sessaoVotacao);

		assertThrows(SessaoVotacaoDataInvalidaException.class, () -> sessaoVotacaoService.salvar(sessaoVotacao),
				"Permitiu salvar uma sessão de votação com datas inválidas.");
	}

}
