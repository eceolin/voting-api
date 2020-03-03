package br.com.votingapi.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import br.com.votingapi.dto.VotoDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Representa o voto do associado numa sessão.
 *
 * @author rafael.rutsatz
 *
 */
@Entity
@Table(name = "voto")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Voto implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@EqualsAndHashCode.Include
	private Long codigo;

	@NotNull(message = "A pauta é obrigatória")
	@ManyToOne
	@JoinColumn(name = "codigo_pauta")
	private Pauta pauta;

	@Column(name = "codigoAssociado")
	private Long codigoAssociado;

	/**
	 * Voto do associado. (true = sim e false = não)
	 */
	@Column(name = "voto")
	private Boolean voto;

}
