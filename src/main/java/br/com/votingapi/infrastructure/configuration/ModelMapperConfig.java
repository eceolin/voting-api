package br.com.votingapi.infrastructure.configuration;

import br.com.votingapi.domain.model.SessaoVotacao;
import br.com.votingapi.infrastructure.api.rest.dto.SessaoVotacaoDto;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        return createMappings(new ModelMapper());
    }

    private ModelMapper createMappings(ModelMapper modelMapper) {
        modelMapper.createTypeMap(SessaoVotacaoDto.class, SessaoVotacao.class)
                .addMapping(SessaoVotacaoDto::getPauta,
                        (destination, value) -> destination.getPauta().setId((String) value));

        modelMapper.createTypeMap(SessaoVotacao.class, SessaoVotacaoDto.class)
                .addMapping(src -> src.getPauta().getId(),
                        (destination, value) -> destination.setPauta((String) value));
        return modelMapper;
    }
}
