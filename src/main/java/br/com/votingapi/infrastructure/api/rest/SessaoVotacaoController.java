package br.com.votingapi.infrastructure.api.rest;

import br.com.votingapi.domain.model.SessaoVotacao;
import br.com.votingapi.infrastructure.api.rest.dto.SessaoVotacaoDto;
import br.com.votingapi.infrastructure.api.rest.dto.VotoDTO;
import br.com.votingapi.infrastructure.persistence.repository.jpa.SessaoVotacaoRepository;
import br.com.votingapi.infrastructure.persistence.repository.jpa.projection.ResumoVotacao;
import br.com.votingapi.application.SessaoVotacaoService;
import br.com.votingapi.infrastructure.service.exception.*;
import br.com.votingapi.infrastructure.service.exception.handler.VotingApiExceptionHandler.Erro;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;

@RestController
@Slf4j
@Validated
@RequestMapping(value = "/api/v1/sessoes")
@Tag(name = "sessão de votação", description = "API de acesso a sessão de votação")
public class SessaoVotacaoController {

    private final SessaoVotacaoRepository sessaoVotacaoRepository;
    private final SessaoVotacaoService sessaoVotacaoService;
    private final MessageSource messageSource;
    private final ModelMapper modelMapper;

    public SessaoVotacaoController(SessaoVotacaoRepository sessaoVotacaoRepository,
                                   SessaoVotacaoService sessaoVotacaoService,
                                   MessageSource messageSource, ModelMapper modelMapper) {
        this.sessaoVotacaoRepository = sessaoVotacaoRepository;
        this.sessaoVotacaoService = sessaoVotacaoService;
        this.messageSource = messageSource;
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
        return sessaoVotacaoRepository.findAll()
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
    public Mono<ResponseEntity<SessaoVotacaoDto>> criar(
            @Valid @RequestBody SessaoVotacaoDto sessaoVotacao) {
        return sessaoVotacaoService.salvar(modelMapper.map(sessaoVotacao, SessaoVotacao.class))
                .map(sessaoSalva -> modelMapper.map(sessaoSalva, SessaoVotacaoDto.class))
                .map(sessaoDto -> ResponseEntity
                        .status(HttpStatus.CREATED)
                        .body(sessaoDto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar sessão de votação por id", description = "Busca a sessão de votação pelo id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operação realizada com sucesso",
                    content = @Content(schema = @Schema(implementation = SessaoVotacaoDto.class))),
            @ApiResponse(responseCode = "400", description = "Requisição inválida."),
            @ApiResponse(responseCode = "404", description = "Recurso não encontrado."),
            @ApiResponse(responseCode = "500", description = "Erro interno.")})
    public Mono<ResponseEntity<SessaoVotacaoDto>> buscarPeloId(@PathVariable String id) {
        return this.sessaoVotacaoRepository.findById(id)
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
    public Mono<ResponseEntity<VotoDTO>> votar(@PathVariable String idSessao,
                                               @Valid @RequestBody VotoDTO votoDTO) {
        return sessaoVotacaoService.votar(idSessao, votoDTO)
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
        return sessaoVotacaoService.apurarResultado(idSessao);
    }

    /**
     * Método que trata a exception quando já existe uma sessão de votação
     * cadastrada.
     *
     * @param ex Exception que foi lançada.
     * @return ResponseEntity enviado ao usuário.
     */
    @ExceptionHandler({SessaoVotacaoJaCadastradaException.class})
    public ResponseEntity<Object> handleSessaoVotacaoJaCadastradaException(SessaoVotacaoJaCadastradaException ex) {
        String mensagemUsuario = messageSource.getMessage("sessaoVotacao.ja-cadastrada", null,
                LocaleContextHolder.getLocale());
        String mensagemDesenvolvedor = ex.toString();
        List<Erro> erros = Collections.singletonList(new Erro(mensagemUsuario, mensagemDesenvolvedor));
        return ResponseEntity.badRequest().body(erros);
    }

    /**
     * Método que trata a exception quando é enviada uma data inválida para a
     * abertura de uma sessão de votação.
     *
     * @param ex Exception que foi lançada.
     * @return ResponseEntity enviado ao usuário.
     */
    @ExceptionHandler({SessaoVotacaoDataInvalidaException.class})
    public ResponseEntity<Object> handleSessaoVotacaoDataInvalidaException(SessaoVotacaoDataInvalidaException ex) {
        String mensagemUsuario = messageSource.getMessage("sessaoVotacao.data-invalida", null,
                LocaleContextHolder.getLocale());
        String mensagemDesenvolvedor = ex.toString();
        List<Erro> erros = Collections.singletonList(new Erro(mensagemUsuario, mensagemDesenvolvedor));
        return ResponseEntity.badRequest().body(erros);
    }

    /**
     * Método que trata a exception quando é solicitado a apuração dos votos mas a
     * sessão de votação ainda está aberta.
     *
     * @param ex Exception que foi lançada.
     * @return ResponseEntity enviado ao usuário.
     */
    @ExceptionHandler({SessaoVotacaoNaoEncerradaException.class})
    public ResponseEntity<Object> handleSessaoVotacaoNaoEncerradaException(SessaoVotacaoNaoEncerradaException ex) {
        String mensagemUsuario = messageSource.getMessage("sessaoVotacao.nao-encerrada", null,
                LocaleContextHolder.getLocale());
        String mensagemDesenvolvedor = ex.toString();
        List<Erro> erros = Collections.singletonList(new Erro(mensagemUsuario, mensagemDesenvolvedor));
        return ResponseEntity.badRequest().body(erros);
    }

    /**
     * Método que trata a exception quando o associado não tem permissão para votar.
     *
     * @param ex Exception que foi lançada.
     * @return ResponseEntity enviado ao usuário.
     */
    @ExceptionHandler({AssociadoSemPermissaoParaVotarException.class})
    public ResponseEntity<Object> handleAssociadoSemPermissaoParaVotarException(
            AssociadoSemPermissaoParaVotarException ex) {
        String mensagemUsuario = messageSource.getMessage("voto.associado-sem-permissao", null,
                LocaleContextHolder.getLocale());
        String mensagemDesenvolvedor = ex.toString();
        List<Erro> erros = Collections.singletonList(new Erro(mensagemUsuario, mensagemDesenvolvedor));
        return ResponseEntity.badRequest().body(erros);
    }

    /**
     * Como essa exception é exclusiva do voto, eu não coloco no ControllerAdvice,
     * eu posso deixar somente nessa classe.
     *
     * @param ex Exception que foi lançada.
     * @return ResponseEntity enviado ao usuário.
     */
    @ExceptionHandler({SessaoVotacaoNaoIniciadaException.class})
    public ResponseEntity<Object> handleSessaoVotacaoNaoIniciadaException(SessaoVotacaoNaoIniciadaException ex) {
        String mensagemUsuario = messageSource.getMessage("sessaoVotacao.nao-iniciada", null,
                LocaleContextHolder.getLocale());
        String mensagemDesenvolvedor = ex.toString();
        List<Erro> erros = Collections.singletonList(new Erro(mensagemUsuario, mensagemDesenvolvedor));
        return ResponseEntity.badRequest().body(erros);
    }

    /**
     * Método que trata a exception quando é feita uma tentativa de voto após o
     * término da sessão.
     *
     * @param ex Exception que foi lançada.
     * @return ResponseEntity enviado ao usuário.
     */
    @ExceptionHandler({SessaoVotacaoEncerradaException.class})
    public ResponseEntity<Object> handleSessaoVotacaoEncerradaException(SessaoVotacaoEncerradaException ex) {
        String mensagemUsuario = messageSource.getMessage("sessaoVotacao.encerrada", null,
                LocaleContextHolder.getLocale());
        String mensagemDesenvolvedor = ex.toString();
        List<Erro> erros = Collections.singletonList(new Erro(mensagemUsuario, mensagemDesenvolvedor));
        return ResponseEntity.badRequest().body(erros);
    }

    /**
     * Método que trata a exception quando o associado já votou na pauta.
     *
     * @param ex Exception que foi lançada.
     * @return ResponseEntity enviado ao usuário.
     */
    @ExceptionHandler({AssociadoJaVotouException.class})
    public ResponseEntity<Object> handleAssociadoJaVotouException(AssociadoJaVotouException ex) {
        String mensagemUsuario = messageSource.getMessage("voto.associado-ja-votou", null,
                LocaleContextHolder.getLocale());
        String mensagemDesenvolvedor = ex.toString();
        List<Erro> erros = Collections.singletonList(new Erro(mensagemUsuario, mensagemDesenvolvedor));
        return ResponseEntity.status(HttpStatus.CONFLICT).body(erros);
    }


}
