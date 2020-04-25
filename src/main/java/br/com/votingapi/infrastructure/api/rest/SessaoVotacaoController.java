package br.com.votingapi.infrastructure.api.rest;

import br.com.votingapi.application.SessaoVotacaoService;
import br.com.votingapi.domain.model.SessaoVotacao;
import br.com.votingapi.domain.model.Voto;
import br.com.votingapi.infrastructure.api.rest.dto.SessaoVotacaoDto;
import br.com.votingapi.infrastructure.api.rest.dto.VotoDTO;
import br.com.votingapi.infrastructure.persistence.repository.jpa.projection.ResumoVotacao;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@RestController
@Slf4j
@Validated
@RequestMapping(value = "/api/v1/sessoes")
@Tag(name = "sessão de votação", description = "API de acesso a sessão de votação")
public class SessaoVotacaoController {

    private final SessaoVotacaoService sessaoVotacaoService;
    private final ModelMapper modelMapper;

    public SessaoVotacaoController(SessaoVotacaoService sessaoVotacaoService, ModelMapper modelMapper) {
        this.sessaoVotacaoService = sessaoVotacaoService;
        this.modelMapper = modelMapper;
    }

    @GetMapping
    @Operation(summary = "Listar sessões de votação", description = "Lista todas as sessões de votação")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operação realizada com sucesso",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = SessaoVotacaoDto.class)))),
            @ApiResponse(responseCode = "400", description = "Requisição inválida."),
            @ApiResponse(responseCode = "500", description = "Erro interno.")})
    public Flux<SessaoVotacaoDto> listar() {
        return sessaoVotacaoService.listarTodas()
                .map(sessaoVotacao -> modelMapper.map(sessaoVotacao, SessaoVotacaoDto.class));
    }

    @PostMapping
    @Operation(summary = "Cadastrar sessão de votação", description = "Cria uma nova sessão de votação")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Operação realizada com sucesso",
                    content = @Content(schema = @Schema(implementation = SessaoVotacaoDto.class))),
            @ApiResponse(responseCode = "400", description = "Requisição inválida."),
            @ApiResponse(responseCode = "404", description = "Recurso não encontrado."),
            @ApiResponse(responseCode = "500", description = "Erro interno.")})
    public Mono<ResponseEntity<SessaoVotacaoDto>> criar(@Valid @RequestBody SessaoVotacaoDto sessaoVotacaoDto) {
        return sessaoVotacaoService.salvar(modelMapper.map(sessaoVotacaoDto, SessaoVotacao.class))
                .map(sessaoSalva -> modelMapper.map(sessaoSalva, SessaoVotacaoDto.class))
                .map(sessaoDto -> ResponseEntity
                        .status(HttpStatus.CREATED)
                        .body(sessaoDto));
    }

    @GetMapping("/{idSessao}")
    @Operation(summary = "Buscar sessão de votação por ID", description = "Busca a sessão de votação pelo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operação realizada com sucesso",
                    content = @Content(schema = @Schema(implementation = SessaoVotacaoDto.class))),
            @ApiResponse(responseCode = "400", description = "Requisição inválida."),
            @ApiResponse(responseCode = "404", description = "Recurso não encontrado."),
            @ApiResponse(responseCode = "500", description = "Erro interno.")})
    public Mono<ResponseEntity<SessaoVotacaoDto>> buscarPeloId(@PathVariable String idSessao) {
        return this.sessaoVotacaoService.buscarSessaoVotacaoPeloId(idSessao)
                .map(sessaoVotacao -> modelMapper.map(sessaoVotacao, SessaoVotacaoDto.class))
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping(value = "/{idSessao}/votar", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Votar", description = "Realiza o voto na pauta")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Operação realizada com sucesso",
                    content = @Content(schema = @Schema(implementation = VotoDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "409", description = "Associado já votou")})
    public Mono<ResponseEntity<VotoDTO>> votar(@PathVariable String idSessao, @Valid @RequestBody VotoDTO votoDTO) {
        return sessaoVotacaoService.votar(idSessao, modelMapper.map(votoDTO, Voto.class))
                .map(votoSalvo -> modelMapper.map(votoSalvo, VotoDTO.class))
                .map(votoDto -> ResponseEntity.status(HttpStatus.CREATED).body(votoDto));
    }

    @GetMapping("/{idSessao}/resultado")
    @Operation(summary = "Apurar resultado", description = "Apura o resultado da votação")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operação realizada com sucesso",
                    content = @Content(schema = @Schema(implementation = ResumoVotacao.class))),
            @ApiResponse(responseCode = "400", description = "Requisição inválida."),
            @ApiResponse(responseCode = "404", description = "Recurso não encontrado."),
            @ApiResponse(responseCode = "500", description = "Erro interno.")})
    public Mono<ResumoVotacao> apurarResultado(@PathVariable String idSessao) {
        return sessaoVotacaoService.apurarResultadoVotacao(idSessao);
    }
}
