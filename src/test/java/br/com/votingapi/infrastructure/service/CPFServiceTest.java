package br.com.votingapi.infrastructure.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class CPFServiceTest {

    @Autowired
    private CPFService cpfService;

    @Test
    public void cpfPodeVotar() {
        Boolean resultado = cpfService.podeVotar(new CpfResponse(Status.ABLE_TO_VOTE));
        Assertions.assertEquals(true, resultado);
    }

    @Test
    public void cpfNaoPodeVotar() {
        Boolean resultado = cpfService.podeVotar(new CpfResponse(Status.UNABLE_TO_VOTE));
        Assertions.assertEquals(false, resultado);
    }

}
