package example.thirdparty.imagga;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TagConfidence {

    Double confidence;

    Tag tag;
}
