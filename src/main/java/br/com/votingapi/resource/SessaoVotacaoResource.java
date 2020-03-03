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

import br.com.votingapi.model.SessaoVotacao;
import br.com.votingapi.service.SessaoVotacaoService;

@RestController
@RequestMapping("/v1/sessoes")
public class SessaoVotacaoResource {

	@Autowired
	private SessaoVotacaoService sessaoVotacaoService;

	@Autowired
	private MessageSource messageSource;

	@PostMapping
	public ResponseEntity<SessaoVotacao> criar(@Valid @RequestBody SessaoVotacao sessaoVotacao, HttpServletResponse response) {
		SessaoVotacao sessaoVotacaoSalva = sessaoVotacaoService.salvar(sessaoVotacao);
		return ResponseEntity.status(HttpStatus.CREATED).body(sessaoVotacaoSalva);
	}

}
