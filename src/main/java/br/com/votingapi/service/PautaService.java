package br.com.votingapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import br.com.votingapi.model.Pauta;
import br.com.votingapi.repository.PautaRepository;

@Service
public class PautaService {

	@Autowired
	private PautaRepository pautaRepository;

	/**
	 * Persiste a entidade no banco.
	 *
	 * @param pauta a ser persistida.
	 * @return a pauta salva.
	 */
	public Pauta salvar(Pauta pauta) {
		return this.pautaRepository.save(pauta);
	}

	/**
	 * Recupera uma pauta pelo ID.
	 *
	 * @param codigo da pauta.
	 * @return os dados da pauta.
	 */
	public Pauta buscarPautaPeloCodigo(Long codigo) {
		Pauta pautaSalva = this.pautaRepository.findById(codigo)
				// Se não encontrou row, lança a exceção, para manter o tratamento e retornar o
				// Http 404.
				.orElseThrow(() -> new EmptyResultDataAccessException(1));
		return pautaSalva;
	}

}
