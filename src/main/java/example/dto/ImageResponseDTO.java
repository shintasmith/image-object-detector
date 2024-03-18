package example.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class ImageResponseDTO {
    Long id;

    String url;

    String label;

    Boolean objectDetection;

    List<String> objects;
}
