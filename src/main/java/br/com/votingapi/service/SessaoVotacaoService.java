package br.com.votingapi.service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import br.com.votingapi.model.Pauta;
import br.com.votingapi.model.SessaoVotacao;
import br.com.votingapi.model.Voto;
import br.com.votingapi.repository.SessaoVotacaoRepository;
import br.com.votingapi.repository.projection.ResumoVotacao;
import br.com.votingapi.service.exception.SessaoVotacaoDataInvalidaException;
import br.com.votingapi.service.exception.SessaoVotacaoJaCadastradaException;
import br.com.votingapi.service.exception.SessaoVotacaoNaoEncerradaException;
import lombok.extern.slf4j.Slf4j;

/**
 * Service usado para regras relacionadas as sessões de votação.
 *
 * @author rafael.rutsatz
 *
 */
@Slf4j
@Service
public class SessaoVotacaoService {

	@Autowired
	private SessaoVotacaoRepository sessaoVotacaoRepository;

	@Autowired
	private PautaService pautaService;

	/**
	 * Cria uma nova sessão de votação.
	 *
	 * @param sessaoVotacao com dados a serem salvos.
	 * @return sessao de votacao salva no banco.
	 */
	public SessaoVotacao salvar(SessaoVotacao sessaoVotacao) {

		log.trace("Salvando uma nova sessão de votação");

		Pauta pautaSalva = pautaService.buscarPautaPeloCodigo(sessaoVotacao.getPauta().getCodigo());
		// Faz o set para retornar os dados completos da pauta ao front.
		sessaoVotacao.setPauta(pautaSalva);

		SessaoVotacao sessaoVotacaoExistente;
		try {
			// Verifica se já existe sessão cadastrada para essa pauta.
			sessaoVotacaoExistente = buscarSessaoVotacaoPeloCodigoPauta(sessaoVotacao.getPauta().getCodigo());
		} catch (EmptyResultDataAccessException e) {
			// Nesse caso, captura a exceção e não joga ela para o spring, para fazer o
			// tratamento dentro da classe, para garantir a integridade dos dados.
			sessaoVotacaoExistente = null;
		}

		// Evita que sejam cadastradas sessões duplicadas.
		if (sessaoVotacaoExistente != null) {
			throw new SessaoVotacaoJaCadastradaException();
		}

		LocalDateTime dataInicio = sessaoVotacao.getDataInicio();
		if (dataInicio == null) {
			dataInicio = LocalDateTime.now();
			sessaoVotacao.setDataInicio(dataInicio);

		}

		LocalDateTime dataFim = sessaoVotacao.getDataFim();
		if (dataFim == null) {
			dataFim = dataInicio.plus(1, ChronoUnit.MINUTES);
			sessaoVotacao.setDataFim(dataFim);
		}

		// Data inválida. Data inicial maior que a final.
		if (dataInicio.isAfter(dataFim)) {
			throw new SessaoVotacaoDataInvalidaException();
		}

		return this.sessaoVotacaoRepository.save(sessaoVotacao);
	}

	/**
	 * Apura o resultado da votação.
	 *
	 * @param codigoSessao Código da sessão que se deseja apurar os votos.
	 * @return Resumo com o resultado.
	 */
	public ResumoVotacao resultado(Long codigoSessao) {

		LocalDateTime now = LocalDateTime.now();

		log.debug("Apurando resultado da votação da sessão {}", codigoSessao);

		SessaoVotacao sessaoVotacao = buscarSessaoVotacaoPeloCodigo(codigoSessao);

		if (now.isBefore(sessaoVotacao.getDataFim())) {
			throw new SessaoVotacaoNaoEncerradaException();
		}

		int pros = 0;
		int contra = 0;

		List<Voto> votos = sessaoVotacao.getPauta().getVotos();
		for (Voto voto : votos) {
			if (voto.getVoto()) {
				pros++;
			} else {
				contra++;
			}
		}

		// Maioria simples para aprovar.
		boolean aprovado = pros > contra;

		return ResumoVotacao.builder()
				.pros(pros)
				.contra(contra)
				.assunto(sessaoVotacao.getPauta().getAssunto())
				.aprovado(aprovado)
				.build();
	}

	/**
	 * Busca uma sessão de votação pelo código da pauta.
	 *
	 * @param codigoPauta da sessão que se deseja buscar.
	 * @return SessaoVotacao a qual a pauta pertence.
	 */
	public SessaoVotacao buscarSessaoVotacaoPeloCodigoPauta(Long codigoPauta) {
		SessaoVotacao sessaoVotacaoSalva = this.sessaoVotacaoRepository.findByPautaCodigo(codigoPauta)
				// Se não encontrou row, lança a exceção, para manter o tratamento e retornar o
				// Http 404.
				.orElseThrow(() -> new EmptyResultDataAccessException(1));
		return sessaoVotacaoSalva;
	}

	/**
	 * Busca uma sessão de votação pelo ID.
	 *
	 * @param codigoSessao que se deseja buscar.
	 * @return SessaoVotacao do banco de dados.
	 */
	public SessaoVotacao buscarSessaoVotacaoPeloCodigo(Long codigoSessao) {
		SessaoVotacao sessaoVotacaoSalva = this.sessaoVotacaoRepository.findById(codigoSessao)
				// Se não encontrou row, lança a exceção, para manter o tratamento e retornar o
				// Http 404.
				.orElseThrow(() -> new EmptyResultDataAccessException(1));
		return sessaoVotacaoSalva;
	}

}
