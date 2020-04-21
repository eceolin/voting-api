package br.com.votingapi.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidade que representa uma sessão de votação.
 *
 * @author rafael.rutsatz
 */
@Document(collection = "sessoes")
@Data
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
public class SessaoVotacao implements Serializable {

    private static final long serialVersionUID = 1L;

    public SessaoVotacao() {
        this.votos = new ArrayList<>();
    }

    @Id
    @EqualsAndHashCode.Include
    private String id;

    /**
     * Pauta que será discutida nessa sessão.
     */
    @DBRef
    @NotNull
    private Pauta pauta;

    /**
     * Data de inicio da sessão.
     */
    private LocalDateTime dataInicio;

    /**
     * Data de término da sessão.
     */
    private LocalDateTime dataFim;

    @DBRef
    private List<Voto> votos;

}
