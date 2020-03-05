package br.com.votingapi.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import br.com.votingapi.service.exception.AssociadoSemPermissaoParaVotarException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * Service responsável por consumir a API de CPF e verificar se é válido para
 * votar.
 *
 * @author rafael.rutsatz
 *
 */
@Slf4j
@Component
public class CPFService {

	private static final String CPF_API_URL = "http://user-info.herokuapp.com/users/";

	/**
	 * Verifica se o associado pode votar, usando uma API externa. Caso a API
	 * estiver fora, por padrão, o acesso é negado.
	 *
	 * @param cpf do associado.
	 * @return true se o associado pode votar.
	 */
	public boolean verificarSeCPFPodeVotar(String cpf) {
		RestTemplate restTemplate = new RestTemplate();
		try {
			ResponseEntity<CpfResponse> response = restTemplate.getForEntity(CPF_API_URL + cpf, CpfResponse.class);
			return response.getBody().getStatus() == Status.ABLE_TO_VOTE;
		} catch (RestClientException e) {
			log.error("Erro ao consumir a API de CPF.", e);
		}
		log.debug("Associado com CPF {} sem permissão para votar", cpf);
		throw new AssociadoSemPermissaoParaVotarException();
	}
}

/**
 * Classe auxiliar para ler a resposta do servidor.
 *
 * @author rafael.rutsatz
 *
 */
@Data
class CpfResponse {
	private Status status;
}

enum Status {
	ABLE_TO_VOTE, UNABLE_TO_VOTE;
}
