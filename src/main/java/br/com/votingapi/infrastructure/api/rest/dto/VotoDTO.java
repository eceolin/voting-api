package br.com.votingapi.infrastructure.api.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.br.CPF;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VotoDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @CPF
    @NotBlank
    @Schema(description = "CPF do associado que está votando.",
            example = "33546206096", required = true)
    private String cpfAssociado;

    @NotNull
    @Schema(description = "Voto do associado. (true para votar a favor e false para votar contra).",
            example = "true", required = true)
    private Boolean voto;

}
