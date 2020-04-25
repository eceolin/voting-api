package br.com.votingapi.infrastructure.service;

import br.com.votingapi.application.CPFService;
import br.com.votingapi.application.SessaoVotacaoService;
import br.com.votingapi.domain.model.Pauta;
import br.com.votingapi.domain.model.SessaoVotacao;
import br.com.votingapi.domain.model.Voto;
import br.com.votingapi.infrastructure.persistence.repository.jpa.PautaRepository;
import br.com.votingapi.infrastructure.persistence.repository.jpa.SessaoVotacaoRepository;
import br.com.votingapi.infrastructure.persistence.repository.jpa.VotoRepository;
import br.com.votingapi.infrastructure.service.exception.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
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
public class SessaoVotacaoServiceTest {

    @Autowired
    private PautaRepository pautaRepository;

    @Autowired
    private VotoRepository votoRepository;

    @Autowired
    private SessaoVotacaoRepository sessaoVotacaoRepository;

    @Autowired
    private SessaoVotacaoService sessaoVotacaoService;

    @MockBean
    private CPFService cpfService;

    private List<Voto> votosList() {
        return Arrays.asList(
                new Voto("VOTO1", "89221190048", true),
                new Voto("VOTO2", "84996652098", false),
                new Voto("VOTO3", "71658911024", true),
                new Voto("VOTO4", "21165748037", false),
                new Voto("VOTO5", "31588533026", true)
        );
    }

    private List<Pauta> pautaList() {
        return Arrays.asList(
                new Pauta("PAUTA1", "Assunto 1"),
                new Pauta("PAUTA2", "Assunto 2"),
                new Pauta("PAUTA3", "Assunto 3"),
                new Pauta("PAUTA4", "Assunto 4"),
                new Pauta("PAUTA5", "Assunto 5")
        );
    }

    private List<SessaoVotacao> sessaoList() {
        return Arrays.asList(
                new SessaoVotacao("SESSAO1", new Pauta("PAUTA1", null),
                        now(), now().plusHours(1), emptyList()),
                new SessaoVotacao("SESSAO2", new Pauta("PAUTA2", null),
                        now().plusHours(1), now(), emptyList()),
                new SessaoVotacao("SESSAO3", new Pauta("PAUTA3", null),
                        now().minusHours(1), now(), emptyList()),
                new SessaoVotacao("SESSAO4", new Pauta("PAUTA4", null),
                        parse("2020-04-19T17:03:00"), parse("2020-04-19T17:03:00"), emptyList())
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
        sessaoVotacaoRepository.findById("SESSAO4")
                .map(sessaoVotacao -> {
                    sessaoVotacao.getVotos().addAll(votosList());
                    return sessaoVotacao;
                })
                .flatMap(sessaoVotacaoRepository::save)
                .block();
    }

    @Test
    public void listarTodas() {
        var sessaoVotacaoFlux = sessaoVotacaoService.listarTodas();
        StepVerifier.create(sessaoVotacaoFlux)
                .expectSubscription()
                .expectNextCount(4)
                .verifyComplete();
    }

    @Test
    public void salvar() {
        var sessaoVotacao = new SessaoVotacao(null, new Pauta("PAUTA5", null),
                parse("2020-04-19T17:03:00"), parse("2020-04-19T17:03:00"), emptyList());
        var sessaoVotacaoMono = sessaoVotacaoService.salvar(sessaoVotacao);
        StepVerifier.create(sessaoVotacaoMono)
                .expectSubscription()
                .consumeNextWith(sessaoVotacaoSalva -> {
                    assertNotNull(sessaoVotacaoSalva.getId());
                    assertEquals("PAUTA5", sessaoVotacaoSalva.getPauta().getId());
                    assertEquals(parse("2020-04-19T17:03:00"), sessaoVotacaoSalva.getDataInicio());
                    assertEquals(parse("2020-04-19T17:03:00"), sessaoVotacaoSalva.getDataFim());
                    assertEquals(emptyList(), sessaoVotacaoSalva.getVotos());
                })
                .verifyComplete();
    }

    @Test
    public void salvar_semInformarDatas() {
        var sessaoVotacao = new SessaoVotacao(null, new Pauta("PAUTA5", null),
                null, null, emptyList());
        var sessaoVotacaoMono = sessaoVotacaoService.salvar(sessaoVotacao);
        StepVerifier.create(sessaoVotacaoMono)
                .expectSubscription()
                .consumeNextWith(sessaoVotacaoSalva -> {
                    assertNotNull(sessaoVotacaoSalva.getId());
                    assertEquals("PAUTA5", sessaoVotacaoSalva.getPauta().getId());
                    assertNotNull(sessaoVotacaoSalva.getDataInicio());
                    assertNotNull(sessaoVotacaoSalva.getDataFim());
                    assertEquals(emptyList(), sessaoVotacaoSalva.getVotos());
                })
                .verifyComplete();
    }

    @Test
    public void salvar_semInformarDataFim() {
        LocalDateTime dataInicio = parse("2020-04-19T17:03:00");
        LocalDateTime dataEsperada = dataInicio.plusMinutes(1);
        var sessaoVotacao = new SessaoVotacao(null, new Pauta("PAUTA5", null),
                dataInicio, null, emptyList());
        var sessaoVotacaoMono = sessaoVotacaoService.salvar(sessaoVotacao);
        StepVerifier.create(sessaoVotacaoMono)
                .expectSubscription()
                .consumeNextWith(sessaoVotacaoSalva -> {
                    assertNotNull(sessaoVotacaoSalva.getId());
                    assertEquals("PAUTA5", sessaoVotacaoSalva.getPauta().getId());
                    assertEquals(dataInicio, sessaoVotacaoSalva.getDataInicio());
                    assertEquals(dataEsperada, sessaoVotacaoSalva.getDataFim());
                    assertEquals(emptyList(), sessaoVotacaoSalva.getVotos());
                })
                .verifyComplete();
    }

    @Test
    public void salvar_sessaoJaCadastrada() {
        var sessaoVotacao = new SessaoVotacao(null, new Pauta("PAUTA1", null),
                parse("2020-04-19T17:03:00"), parse("2020-04-19T17:03:00"), emptyList());
        var sessaoVotacaoMono = sessaoVotacaoService.salvar(sessaoVotacao);
        StepVerifier.create(sessaoVotacaoMono)
                .expectSubscription()
                .expectError(SessaoVotacaoJaCadastradaException.class)
                .verify();
    }

    @Test
    public void buscarPeloId() {
        StepVerifier.create(sessaoVotacaoService.buscarSessaoVotacaoPeloId("SESSAO4"))
                .expectSubscription()
                .consumeNextWith(sessaoVotacaoSalva -> {
                    assertNotNull(sessaoVotacaoSalva.getId());
                    assertEquals("PAUTA4", sessaoVotacaoSalva.getPauta().getId());
                    assertEquals(parse("2020-04-19T17:03:00"), sessaoVotacaoSalva.getDataInicio());
                    assertEquals(parse("2020-04-19T17:03:00"), sessaoVotacaoSalva.getDataFim());
                    assertEquals(5, sessaoVotacaoSalva.getVotos().size());
                })
                .verifyComplete();
    }

    @Test
    public void votar() {
        var voto = new Voto(null, "26622817073", true);
        Mockito.when(cpfService.verificarSeCPFPodeVotar(voto)).thenReturn(Mono.just(voto));
        var votoMono = sessaoVotacaoService.votar("SESSAO1", voto);
        StepVerifier.create(votoMono)
                .expectSubscription()
                .consumeNextWith(votoSalvo -> assertNotNull(votoSalvo.getId()))
                .verifyComplete();
    }

    @Test
    public void votar_associadoJaVotou() {
        var voto = new Voto(null, "26622817073", true);
        Mockito.when(cpfService.verificarSeCPFPodeVotar(voto)).thenReturn(Mono.just(voto));
        var votoMonoOk = sessaoVotacaoService.votar("SESSAO1", voto);
        StepVerifier.create(votoMonoOk)
                .expectSubscription()
                .consumeNextWith(votoSalvo -> assertNotNull(votoSalvo.getId()))
                .verifyComplete();

        var votoMonoErro = sessaoVotacaoService.votar("SESSAO1", voto);
        StepVerifier.create(votoMonoErro)
                .expectSubscription()
                .expectError(AssociadoJaVotouException.class)
                .verify();
    }

    @Test
    public void votar_sessaoNaoIniciada() {
        var voto = new Voto(null, "26622817073", true);
        Mockito.when(cpfService.verificarSeCPFPodeVotar(voto)).thenReturn(Mono.just(voto));
        var votoMono = sessaoVotacaoService.votar("SESSAO2", voto);
        StepVerifier.create(votoMono)
                .expectSubscription()
                .expectError(SessaoVotacaoNaoIniciadaException.class)
                .verify();
    }

    @Test
    public void votar_sessaoEncerrada() {
        var voto = new Voto(null, "26622817073", true);
        Mockito.when(cpfService.verificarSeCPFPodeVotar(voto)).thenReturn(Mono.just(voto));
        var votoMono = sessaoVotacaoService.votar("SESSAO3", voto);
        StepVerifier.create(votoMono)
                .expectSubscription()
                .expectError(SessaoVotacaoEncerradaException.class)
                .verify();
    }

    @Test
    public void votar_associadoSemPermissao() {
        var voto = new Voto(null, "26622817073", true);
        Mockito.when(cpfService.verificarSeCPFPodeVotar(voto))
                .thenReturn(Mono.error(new AssociadoSemPermissaoParaVotarException()));
        var votoMono = sessaoVotacaoService.votar("SESSAO1", voto);
        StepVerifier.create(votoMono)
                .expectSubscription()
                .expectError(AssociadoSemPermissaoParaVotarException.class)
                .verify();
    }

    @Test
    public void apurarResultado_aprovado() {
        var resumoVotacaoMono = sessaoVotacaoService.apurarResultadoVotacao("SESSAO4");
        StepVerifier.create(resumoVotacaoMono)
                .expectSubscription()
                .consumeNextWith(resumoVotacao -> {
                    assertEquals("Assunto 4", resumoVotacao.getAssunto());
                    assertEquals(3, resumoVotacao.getPros());
                    assertEquals(2, resumoVotacao.getContra());
                    assertEquals(true, resumoVotacao.getAprovado());
                })
                .verifyComplete();
    }

    @Test
    public void apurarResultado_naoAprovado() {
        var voto = new Voto("VOTO6", "31588533026", false);
        votoRepository.save(voto).block();
        sessaoVotacaoRepository.findById("SESSAO2")
                .map(sessaoVotacao -> {
                    sessaoVotacao.getVotos().add(voto);
                    return sessaoVotacao;
                })
                .flatMap(sessaoVotacaoRepository::save)
                .block();
        var resumoVotacaoMono = sessaoVotacaoService.apurarResultadoVotacao("SESSAO2");
        StepVerifier.create(resumoVotacaoMono)
                .expectSubscription()
                .consumeNextWith(resumoVotacao -> {
                    assertEquals("Assunto 2", resumoVotacao.getAssunto());
                    assertEquals(0, resumoVotacao.getPros());
                    assertEquals(1, resumoVotacao.getContra());
                    assertEquals(false, resumoVotacao.getAprovado());
                })
                .verifyComplete();
    }

    @Test
    public void votar_sessaoNaoEncerrada() {
        var resumoVotacaoMono = sessaoVotacaoService.apurarResultadoVotacao("SESSAO1");
        StepVerifier.create(resumoVotacaoMono)
                .expectSubscription()
                .expectError(SessaoVotacaoNaoEncerradaException.class)
                .verify();
    }

}
