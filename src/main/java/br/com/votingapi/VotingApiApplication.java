package br.com.votingapi;

import java.util.TimeZone;

import javax.annotation.PostConstruct;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class VotingApiApplication {

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
