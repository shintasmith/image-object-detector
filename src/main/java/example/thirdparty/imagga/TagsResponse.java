package example.thirdparty.imagga;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TagsResponse {

    TagsResult result;

    ApiStatus status;
}
