package br.com.votingapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.votingapi.model.Pauta;

public interface PautaRepository extends JpaRepository<Pauta, Long> {
}
