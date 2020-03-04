package br.com.votingapi.dto;

import java.io.Serializable;

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

	private Long codigoAssociado;

	/**
	 * Código da pauta que o associado está votando.
	 */
	private Long codigoPauta;

	/**
	 * Voto do associado (true = sim e false = não).
	 */
	private Boolean voto;

}
