package br.com.votingapi.infrastructure.service;

import br.com.votingapi.application.PautaService;
import br.com.votingapi.domain.model.Pauta;
import br.com.votingapi.infrastructure.persistence.repository.jpa.PautaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@DirtiesContext
public class PautaServiceTest {

    @Autowired
    private PautaService pautaService;

    @Autowired
    private PautaRepository pautaRepository;

    private List<Pauta> data() {
        return Arrays.asList(
                new Pauta(null, "Assunto 1"),
                new Pauta(null, "Assunto 2"),
                new Pauta(null, "Assunto 3"),
                new Pauta("PAUTA4", "Assunto 4"));
    }

    @BeforeEach
    public void setUp() {
        pautaRepository.deleteAll()
                .thenMany(Flux.fromIterable(data()))
                .flatMap(pautaRepository::save)
                .doOnNext(pauta -> System.out.println("Pauta inserida : " + pauta))
                .blockLast();
    }

    @Test
    public void listarTodas() {
        var pautaFlux = pautaService.listarTodas();
        StepVerifier.create(pautaFlux)
                .expectSubscription()
                .expectNextCount(4)
                .verifyComplete();
    }

    @Test
    public void salvar() {
        var pauta = new Pauta(null, "Assunto 5");
        var pautaMono = pautaService.salvar(pauta);
        StepVerifier.create(pautaMono)
                .expectSubscription()
                .consumeNextWith(pautaSalva -> {
                    assertNotNull(pautaSalva.getId());
                    assertEquals("Assunto 5", pautaSalva.getAssunto());
                })
                .verifyComplete();
    }

    @Test
    public void buscarPeloId() {
        StepVerifier.create(pautaService.buscarPeloId("PAUTA4"))
                .expectSubscription()
                .expectNextMatches(item -> item.getAssunto().equals("Assunto 4"))
                .verifyComplete();
    }
}
