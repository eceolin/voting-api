package br.com.votingapi.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Entidade que representa uma sessão de votação.
 *
 * @author rafael.rutsatz
 *
 */
@Entity
@Table(name = "sessao")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class SessaoVotacao implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@EqualsAndHashCode.Include
	private Long codigo;

	/**
	 * Pauta que será discutida nessa sessão.
	 */
	@NotNull
	@OneToOne
	@JoinColumn(name = "codigo_pauta")
	private Pauta pauta;

	/**
	 * Data de inicio da sessão.
	 */
	@Column(name = "data_inicio")
	private LocalDateTime dataInicio;

	/**
	 * Data de término da sessão.
	 */
	@Column(name = "data_fim")
	private LocalDateTime dataFim;

}
