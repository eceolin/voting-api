package br.com.votingapi.service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.votingapi.model.SessaoVotacao;
import br.com.votingapi.repository.SessaoVotacaoRepository;

@Service
public class SessaoVotacaoService {

	@Autowired
	private SessaoVotacaoRepository sessaoVotacaoRepository;

	public SessaoVotacao salvar(SessaoVotacao sessaoVotacao) {

		LocalDateTime dataInicio = sessaoVotacao.getDataInicio();
		if (dataInicio == null) {
			dataInicio = LocalDateTime.now();
		}

		LocalDateTime dataFim = sessaoVotacao.getDataFim();
		if (dataFim == null) {
			dataFim = dataInicio.plus(1, ChronoUnit.MINUTES);
		}

		if (dataInicio.isAfter(dataFim)) {
			// Data inválida. Data inicial maior que a final.
		}

		return this.sessaoVotacaoRepository.save(sessaoVotacao);
	}

}
