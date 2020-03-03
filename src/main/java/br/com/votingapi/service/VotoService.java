package br.com.votingapi.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.votingapi.dto.VotoDTO;
import br.com.votingapi.model.Pauta;
import br.com.votingapi.model.SessaoVotacao;
import br.com.votingapi.model.Voto;
import br.com.votingapi.repository.PautaRepository;
import br.com.votingapi.repository.SessaoVotacaoRepository;
import br.com.votingapi.repository.VotoRepository;

@Service
public class VotoService {

	@Autowired
	private VotoRepository votoRepository;

	@Autowired
	private PautaRepository pautaRepository;

	@Autowired
	private SessaoVotacaoRepository sessaoVotacaoRepository;

	public Voto salvar(VotoDTO votoDTO) {
		Pauta pauta = null;

		Optional<Pauta> pautaOptional = pautaRepository.findById(votoDTO.getCodigoPauta());
		if (pautaOptional.isPresent()) {
			pauta = pautaOptional.get();
		}

		SessaoVotacao sessaoVotacao = null;

		Optional<SessaoVotacao> sessaoVotacaoOptional = sessaoVotacaoRepository.findByPautaCodigo(pauta.getCodigo());
		if (sessaoVotacaoOptional.isPresent()) {
			sessaoVotacao = sessaoVotacaoOptional.get();
		}

		LocalDateTime now = LocalDateTime.now();
		if (now.isBefore(sessaoVotacao.getDataInicio())) {
			// Votação não está aberta.
		}

		if (now.isAfter(sessaoVotacao.getDataFim())) {
			// Votação encerrada.
		}

		Voto voto = new Voto();
		voto.setCodigoAssociado(votoDTO.getCodigoAssociado());
		voto.setPauta(pauta);
		voto.setVoto(votoDTO.getVoto());

		return this.votoRepository.save(voto);
	}

}
