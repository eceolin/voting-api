package br.com.votingapi.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Assembleia de uma votação.
 *
 * @author rafael.rutsatz
 *
 */
@Entity
@Table(name = "assembleia")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Assembleia implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@EqualsAndHashCode.Include
	private Long codigo;

	@Column(name = "data")
	private LocalDateTime data;
}
