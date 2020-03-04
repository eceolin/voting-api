package br.com.votingapi.repository.projection;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResumoVotacao {

	private String assunto;
	private Integer pros;
	private Integer contra;
	private Boolean aprovado;

}
