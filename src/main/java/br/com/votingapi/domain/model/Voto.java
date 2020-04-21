package br.com.votingapi.domain.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Representa o voto do associado numa sessão.
 *
 * @author rafael.rutsatz
 */
@Document(collection = "votos")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Voto implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @EqualsAndHashCode.Include
    private String id;

    /**
     * CPF do associado que realizou o voto.
     */
    @NotNull
    private String cpfAssociado;

    /**
     * Voto do associado. (true = sim e false = não)
     */
    @NotNull
    private Boolean voto;

}
