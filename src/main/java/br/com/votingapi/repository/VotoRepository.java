package br.com.votingapi.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.votingapi.model.Voto;

public interface VotoRepository extends JpaRepository<Voto, Long> {

	public Optional<Voto> findByCodigoAssociadoAndPautaCodigo(Long codigoAssociado, Long codigoPauta);

}
