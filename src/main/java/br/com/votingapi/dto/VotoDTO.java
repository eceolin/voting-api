package br.com.votingapi.dto;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;

import org.hibernate.validator.constraints.br.CPF;

import lombok.Data;

/**
 * DTO utilizado para receber o voto de algum associado.
 *
 * @author rafael.rutsatz
 *
 */
@Data
public class VotoDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Código do associado que está votando.
	 */
	private Long codigoAssociado;

	/**
	 * CPF do associado que estávotando.
	 */
	@NotBlank
	@CPF
	private String cpfAssociado;

	/**
	 * Código da pauta que o associado está votando.
	 */
	private Long codigoPauta;

	/**
	 * Voto do associado (true = sim e false = não).
	 */
	private Boolean voto;

}
