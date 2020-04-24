package br.com.votingapi.infrastructure.api.rest;

import br.com.votingapi.domain.model.Pauta;
import br.com.votingapi.infrastructure.api.rest.dto.PautaDto;
import br.com.votingapi.infrastructure.persistence.repository.jpa.PautaRepository;
import br.com.votingapi.infrastructure.service.PautaService;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@RestController
@Slf4j
@RequestMapping(value = "/api/v1/pautas")
@Tag(name = "pauta", description = "API de acesso a pauta")
public class PautaController {

    private final PautaRepository pautaRepository;
    private final PautaService pautaService;
    private final ModelMapper modelMapper;

    public PautaController(PautaRepository pautaRepository, PautaService pautaService,
                           ModelMapper modelMapper) {
        this.pautaRepository = pautaRepository;
        this.pautaService = pautaService;
        this.modelMapper = modelMapper;
    }

    @GetMapping
    @Operation(summary = "Listar pautas", description = "Lista todas as pautas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operação realizada com sucesso",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = PautaDto.class)))),
            @ApiResponse(responseCode = "400", description = "Requisição inválida."),
            @ApiResponse(responseCode = "500", description = "Erro interno.")})
    public Flux<PautaDto> listar() {
        log.trace("Listando todas as pautas.");
        return pautaRepository
                .findAll()
                .map(pauta -> modelMapper.map(pauta, PautaDto.class));
    }

    @PostMapping
    @Operation(summary = "Cadastrar pauta", description = "Cria uma nova pauta")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Operação realizada com sucesso",
                    content = @Content(schema = @Schema(implementation = PautaDto.class))),
            @ApiResponse(responseCode = "400", description = "Requisição inválida."),
            @ApiResponse(responseCode = "404", description = "Recurso não encontrado."),
            @ApiResponse(responseCode = "500", description = "Erro interno.")})
    public Mono<ResponseEntity<PautaDto>> criar(@Valid @RequestBody PautaDto pautaDto) {
        return pautaService
                .salvar(modelMapper.map(pautaDto, Pauta.class))
                .map(pautaSalva -> modelMapper.map(pautaSalva, PautaDto.class))
                .map(pautaResponse -> ResponseEntity
                        .status(HttpStatus.CREATED)
                        .body(pautaResponse));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar pauta por id", description = "Busca a pauta pelo id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operação realizada com sucesso",
                    content = @Content(schema = @Schema(implementation = PautaDto.class))),
            @ApiResponse(responseCode = "400", description = "Requisição inválida."),
            @ApiResponse(responseCode = "404", description = "Recurso não encontrado."),
            @ApiResponse(responseCode = "500", description = "Erro interno.")})
    public Mono<ResponseEntity<PautaDto>> buscarPeloId(@PathVariable String id) {
        return this.pautaService.buscarPeloId(id)
                .map(pautaSalva -> modelMapper.map(pautaSalva, PautaDto.class))
                .map(ResponseEntity::ok);
    }

}
