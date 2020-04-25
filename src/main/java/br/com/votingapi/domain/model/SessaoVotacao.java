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

    @DBRef
    @NotNull
    private Pauta pauta;

    private LocalDateTime dataInicio;

    private LocalDateTime dataFim;

    @DBRef
    private List<Voto> votos;

}
