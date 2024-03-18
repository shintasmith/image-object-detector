package example.thirdparty.imagga;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TagsResult {

    List<TagConfidence> tags;
}
