package br.com.votingapi.repository.projection;

import lombok.Data;

@Data
public class ResumoVotacao {

	private String assunto;
	private Integer pros;
	private Integer contra;
	private Boolean aprovado;

}
