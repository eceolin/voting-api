package br.com.votingapi.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.votingapi.model.SessaoVotacao;

public interface SessaoVotacaoRepository extends JpaRepository<SessaoVotacao, Long> {

	public Optional<SessaoVotacao> findByPautaCodigo(Long codigoPauta);

}
