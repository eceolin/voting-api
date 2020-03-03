package br.com.votingapi.resource;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.votingapi.model.Pauta;
import br.com.votingapi.repository.PautaRepository;

@RestController
@RequestMapping("/v1/pautas")
public class PautaResource {

	@Autowired
	private PautaRepository pautaRepository;

	@Autowired
	private MessageSource messageSource;

	@PostMapping
	public ResponseEntity<Pauta> criar(@Valid @RequestBody Pauta pauta, HttpServletResponse response) {
		Pauta pautaSalva = pautaRepository.save(pauta);
		return ResponseEntity.status(HttpStatus.CREATED).body(pautaSalva);
	}

}
