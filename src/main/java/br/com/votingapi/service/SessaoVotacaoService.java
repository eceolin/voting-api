package br.com.votingapi.service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import br.com.votingapi.model.Pauta;
import br.com.votingapi.model.SessaoVotacao;
import br.com.votingapi.repository.SessaoVotacaoRepository;
import br.com.votingapi.service.exception.SessaoVotacaoDataInvalidaException;
import br.com.votingapi.service.exception.SessaoVotacaoJaCadastradaException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SessaoVotacaoService {

	@Autowired
	private SessaoVotacaoRepository sessaoVotacaoRepository;

	@Autowired
	private PautaService pautaService;

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
		}

		LocalDateTime dataFim = sessaoVotacao.getDataFim();
		if (dataFim == null) {
			dataFim = dataInicio.plus(1, ChronoUnit.MINUTES);
		}

		// Data inválida. Data inicial maior que a final.
		if (dataInicio.isAfter(dataFim)) {
			throw new SessaoVotacaoDataInvalidaException();
		}

		return this.sessaoVotacaoRepository.save(sessaoVotacao);
	}

	public SessaoVotacao buscarSessaoVotacaoPeloCodigoPauta(Long codigoPauta) {
		SessaoVotacao sessaoVotacaoSalva = this.sessaoVotacaoRepository.findByPautaCodigo(codigoPauta)
				// Se não encontrou row, lança a exceção, para manter o tratamento e retornar o
				// Http 404.
				.orElseThrow(() -> new EmptyResultDataAccessException(1));
		return sessaoVotacaoSalva;
	}

}
