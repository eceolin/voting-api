package br.com.votingapi.application;

import br.com.votingapi.domain.model.SessaoVotacao;
import br.com.votingapi.domain.model.Voto;
import br.com.votingapi.infrastructure.api.rest.dto.VotoDTO;
import br.com.votingapi.infrastructure.persistence.repository.jpa.projection.ResumoVotacao;
import reactor.core.publisher.Mono;

public interface SessaoVotacaoService {
    /**
     * Cria uma nova sessão de votação.
     *
     * @param sessaoVotacao com dados a serem salvos.
     * @return sessao de votacao salva no banco.
     */
    Mono<SessaoVotacao> salvar(SessaoVotacao sessaoVotacao);

    /**
     * Salva o voto do associado no banco de dados.
     *
     * @param idSessao
     * @param votoDTO  contendo os dados do voto.
     * @return voto salvo no banco de dados.
     */
    Mono<Voto> votar(String idSessao, VotoDTO votoDTO);

    /**
     * Apura o resultado da votação.
     *
     * @param idSessao Código da sessão que se deseja apurar os votos.
     * @return Resumo com o resultado.
     */
    Mono<ResumoVotacao> apurarResultado(String idSessao);

    /**
     * Busca uma sessão de votação pelo ID.
     *
     * @param idSessao que se deseja buscar.
     * @return SessaoVotacao do banco de dados.
     */
    Mono<SessaoVotacao> buscarSessaoVotacaoPeloId(String idSessao);
}
