package br.com.votingapi.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Pauta a ser discutida numa sessão.
 *
 * @author rafael.rutsatz
 *
 */
@Entity
@Table(name = "pauta")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Pauta implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@EqualsAndHashCode.Include
	private Long codigo;

	/**
	 * Descrição do assunto da pauta.
	 */
	@NotBlank
	@Column(name = "assunto")
	private String assunto;

	/**
	 * Lista de votos da pauta. Como a lista não precisa ser enviada ao front, está
	 * com o @JsonIgnore.
	 */
	@JsonIgnore
	@OneToMany(mappedBy = "pauta")
	private List<Voto> votos;
}
