package br.com.votingapi;

import lombok.Generated;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.hateoas.config.EnableHypermediaSupport;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

@SpringBootApplication
@EnableHypermediaSupport(type = EnableHypermediaSupport.HypermediaType.HAL)
public class VotingApiApplication {

    @Generated
    public static void main(String[] args) {
        SpringApplication.run(VotingApiApplication.class, args);
    }

    /**
     * Configura o Spring para usar o timezone -3, pois o Heroku usa UTC.
     */
    @PostConstruct
    public void init() {
        TimeZone.setDefault(TimeZone.getTimeZone("America/Sao_Paulo"));
    }

}
