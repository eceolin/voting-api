package br.com.votingapi.infrastructure.api.rest;

import br.com.votingapi.application.CPFService;
import br.com.votingapi.domain.model.Pauta;
import br.com.votingapi.domain.model.SessaoVotacao;
import br.com.votingapi.domain.model.Voto;
import br.com.votingapi.infrastructure.api.rest.dto.SessaoVotacaoDto;
import br.com.votingapi.infrastructure.api.rest.dto.VotoDTO;
import br.com.votingapi.infrastructure.persistence.repository.jpa.PautaRepository;
import br.com.votingapi.infrastructure.persistence.repository.jpa.SessaoVotacaoRepository;
import br.com.votingapi.infrastructure.persistence.repository.jpa.VotoRepository;
import br.com.votingapi.infrastructure.service.exception.AssociadoSemPermissaoParaVotarException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static java.time.LocalDateTime.now;
import static java.time.LocalDateTime.parse;
import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@DirtiesContext
@AutoConfigureWebTestClient
public class SessaoVotacaoControllerTest {

    private final static String ENDPOINT_URL = "/api/v1/sessoes";

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private VotoRepository votoRepository;

    @Autowired
    private PautaRepository pautaRepository;

    @Autowired
    private SessaoVotacaoRepository sessaoVotacaoRepository;

    @MockBean
    private CPFService cpfService;

    private List<Voto> votosList() {
        return Arrays.asList(
                new Voto("A", "89221190048", true),
                new Voto("B", "84996652098", false),
                new Voto("C", "71658911024", true),
                new Voto("D", "21165748037", false),
                new Voto("E", "31588533026", true)
        );
    }

    private List<Pauta> pautaList() {
        return Arrays.asList(
                new Pauta("A", "Assunto 1"),
                new Pauta("B", "Assunto 2"),
                new Pauta("C", "Assunto 3"),
                new Pauta("D", "Assunto 4"),
                new Pauta("E", "Assunto 5")
        );
    }

    private List<SessaoVotacao> sessaoList() {
        return Arrays.asList(
                new SessaoVotacao("A",
                        new Pauta("A", null), now(), now().plusHours(1), emptyList()),
                new SessaoVotacao("B",
                        new Pauta("B", null), now().plusHours(1), now(), emptyList()),
                new SessaoVotacao("C",
                        new Pauta("C", null), now().minusHours(1), now(), emptyList()),
                new SessaoVotacao("ABC",
                        new Pauta("D", null),
                        parse("2020-04-19T17:03:00"),
                        parse("2020-04-19T17:03:00"), emptyList())
        );
    }

    @BeforeEach
    public void setUp() {
        votoRepository.deleteAll()
                .thenMany(Flux.fromIterable(votosList()))
                .flatMap(votoRepository::save)
                .doOnNext(pauta -> System.out.println("Voto inserido : " + pauta))
                .blockLast();
        pautaRepository.deleteAll()
                .thenMany(Flux.fromIterable(pautaList()))
                .flatMap(pautaRepository::save)
                .doOnNext(pauta -> System.out.println("Pauta inserida : " + pauta))
                .blockLast();
        sessaoVotacaoRepository.deleteAll()
                .thenMany(Flux.fromIterable(sessaoList()))
                .flatMap(sessaoVotacaoRepository::save)
                .doOnNext(sessao -> System.out.println("Sessão inserida : " + sessao))
                .blockLast();
        sessaoVotacaoRepository.findById("ABC")
                .map(sessaoVotacao -> {
                    sessaoVotacao.getVotos().addAll(votosList());
                    return sessaoVotacao;
                })
                .flatMap(sessaoVotacaoRepository::save)
                .block();
    }

    @Test
    public void listarTodas() {
        webTestClient.get().uri(ENDPOINT_URL)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(SessaoVotacaoDto.class)
                .hasSize(4)
                .consumeWith(response -> {
                    List<SessaoVotacaoDto> sessoes = response.getResponseBody();
                    assert sessoes != null;
                    sessoes.forEach(sessao -> {
                        assertNotNull(sessao.getPauta());
                        assertNotNull(sessao.getDataInicio());
                        assertNotNull(sessao.getDataInicio());
                    });
                });
    }

    @Test
    public void criarSessao() {
        SessaoVotacaoDto sessao = new SessaoVotacaoDto(null, "E", parse("2020-04-19T17:03:00"),
                parse("2020-04-19T17:03:00"));

        webTestClient.post().uri(ENDPOINT_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(sessao), SessaoVotacaoDto.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.pauta").isEqualTo("E")
                .jsonPath("$.dataInicio").isEqualTo("2020-04-19T17:03:00")
                .jsonPath("$.dataFim").isEqualTo("2020-04-19T17:03:00");
    }

    @Test
    public void criarSessaoSemInformarDatas() {
        SessaoVotacaoDto sessaoVotacao = new SessaoVotacaoDto(null, "E", null, null);
        Flux<SessaoVotacaoDto> sessaoVotacaoFlux = webTestClient.post().uri(ENDPOINT_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(sessaoVotacao), SessaoVotacaoDto.class)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .returnResult(SessaoVotacaoDto.class)
                .getResponseBody();

        StepVerifier.create(sessaoVotacaoFlux)
                .expectSubscription()
                .consumeNextWith(sessao -> {
                    assertNotNull(sessao.getPauta());
                    assertNotNull(sessao.getDataInicio());
                    assertNotNull(sessao.getDataFim());
                })
                .verifyComplete();
    }

    @Test
    public void criarSessaoSemInformarDataFim() {
        LocalDateTime dataInicio = parse("2020-04-19T17:03:00");
        LocalDateTime dataEsperada = dataInicio.plusMinutes(1);
        SessaoVotacaoDto sessao = new SessaoVotacaoDto(null, "E", dataInicio, null);
        Flux<SessaoVotacaoDto> sessaoVotacaoFlux = webTestClient.post().uri(ENDPOINT_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(sessao), SessaoVotacaoDto.class)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .returnResult(SessaoVotacaoDto.class)
                .getResponseBody();

        StepVerifier.create(sessaoVotacaoFlux)
                .expectSubscription()
                .consumeNextWith(sessaoVotacao -> {
                    assertNotNull(sessaoVotacao.getPauta());
                    assertNotNull(sessaoVotacao.getDataInicio());
                    assertNotNull(sessaoVotacao.getDataFim());
                    assertEquals(dataInicio, sessaoVotacao.getDataInicio());
                    assertEquals(dataEsperada, sessaoVotacao.getDataFim());
                })
                .verifyComplete();
    }

    @Test
    public void criarSessao_badRequest_sessaoJaCadastrada() {
        SessaoVotacaoDto sessao = new SessaoVotacaoDto(null, "A",
                parse("2020-04-19T17:03:00"), parse("2020-04-19T17:03:00"));
        webTestClient.post().uri(ENDPOINT_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(sessao), SessaoVotacaoDto.class)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$[0].mensagemUsuario")
                .isEqualTo("Sessão de votação já cadastrada para essa pauta");
    }

    @Test
    public void criarSessao_badRequest_dataInicialMaiorQueFinal() {
        SessaoVotacaoDto sessao = new SessaoVotacaoDto(null, "E",
                parse("2020-04-19T17:03:01"), parse("2020-04-19T17:03:00"));
        webTestClient.post().uri(ENDPOINT_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(sessao), SessaoVotacaoDto.class)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$[0].mensagemUsuario").isEqualTo("Data inválida");
    }

    @Test
    public void buscarUmaSessao() {
        webTestClient.get().uri(ENDPOINT_URL.concat("/{id}"), "ABC")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.pauta").isEqualTo("D")
                .jsonPath("$.dataInicio").isEqualTo("2020-04-19T17:03:00")
                .jsonPath("$.dataFim").isEqualTo("2020-04-19T17:03:00");
    }

    @Test
    public void buscarUmaSessao_notFound() {
        webTestClient.get().uri(ENDPOINT_URL.concat("/{id}"), "DEF")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    public void votar() {
        var voto = new Voto(null, "26622817073", true);
        Mockito.when(cpfService.verificarSeCPFPodeVotar(voto)).thenReturn(Mono.just(voto));

        webTestClient.post().uri(ENDPOINT_URL.concat("/{idSessao}/votar"), "A")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(voto), Voto.class)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.cpfAssociado").isEqualTo("26622817073")
                .jsonPath("$.voto").isEqualTo(true);
    }

    @Test
    public void votar_badRequest_associadoJaVotou() {
        var voto = new Voto(null, "26622817073", true);
        Mockito.when(cpfService.verificarSeCPFPodeVotar(voto)).thenReturn(Mono.just(voto));

        webTestClient.post().uri(ENDPOINT_URL.concat("/{idSessao}/votar"), "A")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(voto), VotoDTO.class)
                .exchange()
                .expectStatus().isCreated();

        webTestClient.post().uri(ENDPOINT_URL.concat("/{idSessao}/votar"), "A")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(voto), Voto.class)
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody()
                .jsonPath("$[0].mensagemUsuario").isEqualTo("Associado já votou");
    }

    @Test
    public void votar_badRequest_sessaoNaoIniciada() {
        var voto = new Voto(null, "26622817073", true);
        Mockito.when(cpfService.verificarSeCPFPodeVotar(voto)).thenReturn(Mono.just(voto));

        webTestClient.post().uri(ENDPOINT_URL.concat("/{idSessao}/votar"), "B")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(voto), Voto.class)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$[0].mensagemUsuario")
                .isEqualTo("Sessão de votação não iniciada");
    }

    @Test
    public void votar_badRequest_sessaoJaEncerrada() {
        var voto = new Voto(null, "26622817073", true);
        Mockito.when(cpfService.verificarSeCPFPodeVotar(voto)).thenReturn(Mono.just(voto));

        webTestClient.post().uri(ENDPOINT_URL.concat("/{idSessao}/votar"), "C")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(voto), Voto.class)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$[0].mensagemUsuario")
                .isEqualTo("Sessão de votação encerrada");
    }

    @Test
    public void votar_badRequest_associadoSemPermissao() {
        var voto = new Voto(null, "26622817073", true);
        Mockito.when(cpfService.verificarSeCPFPodeVotar(voto))
                .thenReturn(Mono.error(new AssociadoSemPermissaoParaVotarException()));

        webTestClient.post().uri(ENDPOINT_URL.concat("/{idSessao}/votar"), "A")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(voto), Voto.class)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$[0].mensagemUsuario")
                .isEqualTo("Associado sem permissão para votar");
    }

    @Test
    public void apurarResultado_aprovado() {
        webTestClient.get().uri(ENDPOINT_URL.concat("/{idSessao}/resultado"), "ABC")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.assunto").isEqualTo("Assunto 4")
                .jsonPath("$.pros").isEqualTo(3)
                .jsonPath("$.contra").isEqualTo(2)
                .jsonPath("$.aprovado").isEqualTo(true);
    }

    @Test
    public void apurarResultado_badRequest_sessaoNaoEncerrada() {
        webTestClient.get().uri(ENDPOINT_URL.concat("/{idSessao}/resultado"), "A")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$[0].mensagemUsuario")
                .isEqualTo("Sessão de votação ainda está aberta");
    }

}
