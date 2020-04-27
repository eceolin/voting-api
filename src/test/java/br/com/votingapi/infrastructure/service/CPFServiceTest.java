package br.com.votingapi.infrastructure.service;

import br.com.votingapi.application.CPFService;
import br.com.votingapi.domain.model.AssociateVotePermission;
import br.com.votingapi.domain.model.CpfResponse;
import br.com.votingapi.domain.model.Voto;
import br.com.votingapi.infrastructure.service.exception.AssociadoSemPermissaoParaVotarException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import reactor.test.StepVerifier;

import java.io.IOException;

@SpringBootTest
@DirtiesContext
public class CPFServiceTest {

    public static MockWebServer server;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void setUp() throws IOException {
        server = new MockWebServer();
        server.start();
    }

    @AfterAll
    static void tearDown() throws IOException {
        server.shutdown();
    }

    @Test
    public void verificarSeCPFPodeVotar() throws JsonProcessingException {
        var voto = new Voto(null, "26622817073", true);
        var cpfResponseMock = new CpfResponse(AssociateVotePermission.ABLE_TO_VOTE);
        server.enqueue(new MockResponse()
                .setBody(objectMapper.writeValueAsString(cpfResponseMock))
                .addHeader("Content-Type", "application/json"));

        String url = server.url("/").toString();
        CPFService cpfService = new CPFServiceImpl(url);
        var votoMono = cpfService.verificarSeCPFPodeVotar(voto);

        StepVerifier.create(votoMono)
                .expectSubscription()
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    public void cpfSemPermissaoParaVotar() throws JsonProcessingException {
        var voto = new Voto(null, "26622817073", true);
        var cpfResponseMock = new CpfResponse(AssociateVotePermission.UNABLE_TO_VOTE);
        server.enqueue(new MockResponse()
                .setBody(objectMapper.writeValueAsString(cpfResponseMock))
                .addHeader("Content-Type", "application/json"));

        String url = server.url("/").toString();
        CPFService cpfService = new CPFServiceImpl(url);
        var votoMono = cpfService.verificarSeCPFPodeVotar(voto);

        StepVerifier.create(votoMono)
                .expectSubscription()
                .expectError(AssociadoSemPermissaoParaVotarException.class)
                .verify();
    }

    @Test
    public void cpfPodeVotar() {
        String url = server.url("/").toString();
        CPFService cpfService = new CPFServiceImpl(url);
        Boolean resultado = cpfService.podeVotar(new CpfResponse(AssociateVotePermission.ABLE_TO_VOTE));
        Assertions.assertEquals(true, resultado);
    }

    @Test
    public void cpfNaoPodeVotar() {
        String url = server.url("/").toString();
        CPFService cpfService = new CPFServiceImpl(url);
        Boolean resultado = cpfService.podeVotar(new CpfResponse(AssociateVotePermission.UNABLE_TO_VOTE));
        Assertions.assertEquals(false, resultado);
    }

}
