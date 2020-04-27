package br.com.votingapi.infrastructure.api.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PautaDto {

    @Schema(description = "ID da pauta que foi criada.",
            example = "5ea1ee7c49d9501eec55c12f")
    private String id;

    @NotBlank
    @Schema(description = "Assunto da pauta que será discutido na votação.",
            example = "Aprovar novo orçamento", required = true)
    private String assunto;

}
