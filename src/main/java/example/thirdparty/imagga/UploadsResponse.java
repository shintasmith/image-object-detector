package example.thirdparty.imagga;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UploadsResponse {

    UploadsResult result;

    ApiStatus status;
}
