package br.com.votingapi.infrastructure.persistence.repository.jpa.projection;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ResumoVotacao {

    @Schema(description = "Descrição do assunto discutido na pauta.",
            example = "Aprovar novo orçamento")
    private String assunto;

    @Schema(description = "Quantidade de votos a favor da pauta.", example = "80")
    private Long pros;

    @Schema(description = "Quantidade de votos contra a pauta.", example = "20")
    private Long contra;

    @Schema(description = "Indica se a pauta foi aprovada ou não.", example = "true")
    private Boolean aprovado;

}
