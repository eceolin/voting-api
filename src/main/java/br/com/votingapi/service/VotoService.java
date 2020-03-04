package br.com.votingapi.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.votingapi.dto.VotoDTO;
import br.com.votingapi.model.Pauta;
import br.com.votingapi.model.SessaoVotacao;
import br.com.votingapi.model.Voto;
import br.com.votingapi.repository.SessaoVotacaoRepository;
import br.com.votingapi.repository.VotoRepository;
import br.com.votingapi.service.exception.AssociadoJaVotouException;
import br.com.votingapi.service.exception.SessaoVotacaoEncerradaException;
import br.com.votingapi.service.exception.SessaoVotacaoNaoIniciadaException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class VotoService {

	@Autowired
	private VotoRepository votoRepository;

	@Autowired
	private PautaService pautaService;

	@Autowired
	private SessaoVotacaoRepository sessaoVotacaoRepository;

	/**
	 * Salva o voto do associado no banco de dados.
	 *
	 * @param votoDTO contendo os dados do voto.
	 * @return voto salvo no banco de dados.
	 */
	public Voto salvar(VotoDTO votoDTO) {

		// Salva a data atual.
		LocalDateTime now = LocalDateTime.now();
		SessaoVotacao sessaoVotacao = null;

		log.debug("Processando voto para associado {} na pauta {} ", votoDTO.getCodigoAssociado(),
				votoDTO.getCodigoPauta());

		Pauta pauta = pautaService.buscarPautaPeloCodigo(votoDTO.getCodigoPauta());

		Optional<SessaoVotacao> sessaoVotacaoOptional = sessaoVotacaoRepository.findByPautaCodigo(pauta.getCodigo());
		if (sessaoVotacaoOptional.isPresent()) {
			sessaoVotacao = sessaoVotacaoOptional.get();
		}

		// Verifica se a sessão já foi criada e se já iniciou.
		if (sessaoVotacao == null || now.isBefore(sessaoVotacao.getDataInicio())) {
			throw new SessaoVotacaoNaoIniciadaException();
		}

		if (now.isAfter(sessaoVotacao.getDataFim())) {
			throw new SessaoVotacaoEncerradaException();
		}

		// verificar se o associado já votou nessa pauta.
		Optional<Voto> votoOptional = buscarVotoPeloCodigoAssociadoECodigoPauta(votoDTO.getCodigoAssociado(),
				pauta.getCodigo());
		if (votoOptional.isPresent()) {
			throw new AssociadoJaVotouException();
		}

		Voto voto = Voto.builder()
				.codigoAssociado(votoDTO.getCodigoAssociado())
				.pauta(pauta)
				.voto(votoDTO.getVoto())
				.build();
		return this.votoRepository.save(voto);
	}

	/**
	 * Busca o voto pelo código do associado e pelo código da pauta.
	 *
	 * @param codigoAssociado
	 * @param codigoPauta
	 * @return voto salvo no banco de dados.
	 */
	public Optional<Voto> buscarVotoPeloCodigoAssociadoECodigoPauta(Long codigoAssociado, Long codigoPauta) {
		return this.votoRepository.findByCodigoAssociadoAndPautaCodigo(codigoAssociado, codigoPauta);
	}

}
