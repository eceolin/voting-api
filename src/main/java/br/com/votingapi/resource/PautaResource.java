package br.com.votingapi.resource;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.votingapi.model.Pauta;
import br.com.votingapi.repository.PautaRepository;
import br.com.votingapi.service.PautaService;
import lombok.extern.slf4j.Slf4j;

/**
 * Controller responsável por atender as requisições relacionadas as pautas.
 *
 * @author rafael.rutsatz
 *
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/pautas")
public class PautaResource {

	@Autowired
	private PautaRepository pautaRepository;

	@Autowired
	private PautaService pautaService;

	/**
	 * Lista todas as pautas.
	 *
	 * @return Lista de pautas.
	 */
	@GetMapping
	public List<Pauta> listar() {
		log.trace("Listando todas as pautas.");
		return pautaRepository.findAll();
	}

	/**
	 * Cria uma nova pauta. É utilizado o próprio model, pois não existem campos que
	 * precisam ser escondidos do usuário.
	 *
	 * @param pauta a ser criada.
	 * @return Nova pauta criada.
	 */
	@PostMapping
	public ResponseEntity<Pauta> criar(@Valid @RequestBody Pauta pauta) {
		Pauta pautaSalva = pautaService.salvar(pauta);
		return ResponseEntity.status(HttpStatus.CREATED).body(pautaSalva);
	}

	/**
	 * Recupera uma pauta pelo id.
	 *
	 * @param codigo da pauta
	 * @return A pauta encontrada
	 */
	@GetMapping("/{codigo}")
	public ResponseEntity<?> buscarPeloCodigo(@PathVariable Long codigo) {
		return this.pautaRepository.findById(codigo).map(pauta -> ResponseEntity.ok(pauta))
				.orElse(ResponseEntity.notFound().build());
	}

}
