package br.com.votingapi.infrastructure.api.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SessaoVotacaoDto {

    @NotBlank
    @Schema(description = "Id da pauta que será discutida nessa sessão.",
            example = "5ea086a6300c8a110e1de02f", required = true)
    private String pauta;

    @Schema(description = "Data de inicio da sessão.", defaultValue = "Data Atual",
            example = "2020-04-21T17:03:00")
    private LocalDateTime dataInicio;

    @Schema(description = "Data de término da sessão.", defaultValue = "Data de inicio acrescido de um minuto",
            example = "2020-04-21T17:04:00")
    private LocalDateTime dataFim;

}
