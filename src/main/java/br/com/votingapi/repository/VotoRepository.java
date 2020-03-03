package br.com.votingapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.votingapi.model.Voto;

public interface VotoRepository extends JpaRepository<Voto, Long> {
}
