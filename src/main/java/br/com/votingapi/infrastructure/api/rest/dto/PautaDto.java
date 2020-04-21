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

    @NotBlank
    @Schema(description = "Assunto da pauta que será discutido na votação.",
            example = "Aprovar novo orçamento", required = true)
    private String assunto;

}
