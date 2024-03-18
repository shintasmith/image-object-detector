package example.config;

import example.dto.ImageRequestDTO;
import example.dto.ImageResponseDTO;
import example.model.Image;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class Common {

    @Bean
    public RestClient restClient() {
        return RestClient.create();
    }

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();
        // make mapper work with builder pattern:
        //   https://github.com/modelmapper/modelmapper/issues/265
        org.modelmapper.config.Configuration builderConfiguration = mapper.getConfiguration().copy();
        builderConfiguration.setDestinationNamingConvention((name, type) -> true);
        builderConfiguration.setDestinationNameTransformer((name, type) -> name);
        mapper.createTypeMap(ImageRequestDTO.class, Image.ImageBuilder.class, builderConfiguration);
        mapper.createTypeMap(Image.class, ImageResponseDTO.ImageResponseDTOBuilder.class, builderConfiguration);
        return mapper;
    }
}
