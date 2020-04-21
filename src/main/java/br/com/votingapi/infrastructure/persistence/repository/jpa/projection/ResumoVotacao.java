package br.com.votingapi.infrastructure.persistence.repository.jpa.projection;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Dados do resumidos da votação, contendo o resultado após a apuração dos
 * votos.
 *
 * @author rafael.rutsatz
 */
@Builder
@Getter
@RequiredArgsConstructor
@lombok.Value
public class ResumoVotacao {

    @Schema(description = "Descrição do assunto discutido na pauta.",
            example = "Aprovar novo orçamento")
    String assunto;

    @Schema(description = "Quantidade de votos a favor da pauta.", example = "80")
    Long pros;

    @Schema(description = "Quantidade de votos contra a pauta.", example = "20")
    Long contra;

    @Schema(description = "Indica se a pauta foi aprovada ou não.", example = "true")
    Boolean aprovado;

}
