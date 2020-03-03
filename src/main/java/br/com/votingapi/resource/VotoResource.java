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

import br.com.votingapi.dto.VotoDTO;
import br.com.votingapi.model.Voto;
import br.com.votingapi.service.VotoService;

@RestController
@RequestMapping("/v1/votos")
public class VotoResource {

	@Autowired
	private VotoService votoService;

	@Autowired
	private MessageSource messageSource;

	@PostMapping
	public ResponseEntity<Voto> criar(@Valid @RequestBody VotoDTO votoDTO, HttpServletResponse response) {
		Voto votoSalvo = votoService.salvar(votoDTO);
		return ResponseEntity.status(HttpStatus.CREATED).body(votoSalvo);
	}

}
