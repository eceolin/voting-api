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

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

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
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Voto implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@EqualsAndHashCode.Include
	private Long codigo;

	/**
	 * Pauta a qual o voto está relacionado.
	 */
	@NotNull
	@ManyToOne
	@JoinColumn(name = "codigo_pauta")
	private Pauta pauta;

	/**
	 * ID do associado que realizou o voto.
	 */
	@NotNull
	@Column(name = "codigoAssociado")
	private Long codigoAssociado;

	/**
	 * Voto do associado. (true = sim e false = não)
	 */
	@NotNull
	@Column(name = "voto")
	private Boolean voto;

}
