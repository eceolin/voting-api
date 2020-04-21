package br.com.votingapi.infrastructure.api.rest;

import br.com.votingapi.domain.model.Pauta;
import br.com.votingapi.infrastructure.persistence.repository.jpa.PautaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@DirtiesContext
@AutoConfigureWebTestClient
public class PautaControllerTest {

    private final static String ENDPOINT_URL = "/api/v1/pautas";

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private PautaRepository pautaRepository;

    private List<Pauta> data() {
        return Arrays.asList(
                new Pauta(null, "Assunto 1"),
                new Pauta(null, "Assunto 2"),
                new Pauta(null, "Assunto 3"),
                new Pauta("ABC", "Assunto 4"));
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
        webTestClient.get().uri(ENDPOINT_URL)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaTypes.HAL_JSON_VALUE)
                .expectBodyList(Pauta.class)
                .hasSize(4)
                .consumeWith(response -> {
                    List<Pauta> pautas = response.getResponseBody();
                    assert pautas != null;
                    pautas.forEach(pauta -> {
                        /* Verificar se todas as pautas foram inseridas. */
                        assertNotNull(pauta.getId());
                    });
                });
    }

    @Test
    public void buscarUmaPauta() {
        webTestClient.get().uri(ENDPOINT_URL.concat("/{id}"), "ABC")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.assunto", "Assunto 4");
    }

    @Test
    public void buscarUmaPauta_notFound() {
        webTestClient.get().uri(ENDPOINT_URL.concat("/{id}"), "DEF")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    public void criarPauta() {
        Pauta pauta = new Pauta(null, "Assunto 5");

        webTestClient.post().uri(ENDPOINT_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(pauta), Pauta.class)
                .exchange()
                .expectStatus().isCreated()
                /* Acessa a resposta. */
                .expectBody()
                /* Valida os dados. */
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.assunto").isEqualTo("Assunto 5");
    }

}
