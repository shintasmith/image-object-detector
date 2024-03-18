package example.thirdparty.imagga;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ImageDetector {

    @Autowired
    RestClient restClient;

    private static final String IMAGGA_TAGS_API_URL = "https://api.imagga.com/v2/tags";
    private static final String IMAGGA_UPLOADS_API_URL = "https://api.imagga.com/v2/uploads";
    private static final String IMAGGA_API_KEY = "acc_f9e4ef8a584451a";
    private static final String IMAGGA_API_SECRET = "4be1acc56d8cf3344524e5823b5127dc";
    private static final Double IMAGGA_CONFIDENCE_THRESHOLD = 50.0;

    public List<String> detect(String imageUrl) {
        TagsResponse response = detectWithUrl(imageUrl);

        log.debug("Response: {}", response);
        if ( response == null || response.getResult() == null || CollectionUtils.isEmpty(response.getResult().getTags()) ) {
            log.warn("Result is null or does not detect any tags for image={}", imageUrl);
            return List.of();
        }
        return extractObjects(response);
    }

    public List<String> detectForUpload(byte[] content) throws IOException {
        UploadsResponse uploadsResponse = upload(content);
        if (uploadsResponse == null || uploadsResponse.getResult() == null) {
            log.error("Unable to retrieve upload id. UploadsResponse={}", uploadsResponse);
            throw new IOException("Unable to retrieve upload id. Either response body is empty or it does not contain result element.");
        }

        String uploadId = uploadsResponse.getResult().getUploadId();
        TagsResponse tagsResponse = detectWithUploadId(uploadId);
        log.debug("Response: {}", tagsResponse);
        if ( tagsResponse == null || tagsResponse.getResult() == null || CollectionUtils.isEmpty(tagsResponse.getResult().getTags()) ) {
            log.warn("Result is null or does not detect any tags for uploadId={}", uploadId);
            return List.of();
        }
        return extractObjects(tagsResponse);
    }

    UploadsResponse upload(byte[] content) throws IllegalArgumentException {
        MultiValueMap<String, Object> parts = new LinkedMultiValueMap<>();
        parts.add("image", content);
        ResponseEntity<UploadsResponse> uploadsResponseEntity =
                restClient
                        .post()
                        .uri(IMAGGA_UPLOADS_API_URL)
                        .header(HttpHeaders.AUTHORIZATION, encodeBasic(IMAGGA_API_KEY, IMAGGA_API_SECRET))
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .body(parts)
                        .retrieve()
                        .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                            throw new IllegalArgumentException(
                                    String.format("Unable to upload image for object detection, statusCode=%s, body=%s",
                                            response.getStatusCode().value(), new String(response.getBody().readAllBytes())));
                        })
                        .toEntity(UploadsResponse.class);
        return uploadsResponseEntity.getBody();
    }

    TagsResponse detectWithUrl(String imageUrl) {
        return restClient
                .get()
                // if we ever want to support non-english names, we have to change this and
                // the @JsonAlias in the Tag class.
                .uri(IMAGGA_TAGS_API_URL + "?image_url={image_url}&language=en&threshold={confidence}",
                        imageUrl, IMAGGA_CONFIDENCE_THRESHOLD)
                .header(HttpHeaders.AUTHORIZATION, encodeBasic(IMAGGA_API_KEY, IMAGGA_API_SECRET))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    throw new IllegalArgumentException(
                            String.format("Unable to detect objects in image with url=%s, statusCode=%s, body=%s",
                                    imageUrl, response.getStatusCode().value(),
                                    new String(response.getBody().readAllBytes())));
                })
                .body(TagsResponse.class);
    }

    TagsResponse detectWithUploadId(String uploadId) {
        return restClient
                .get()
                // if we ever want to support non-english names, we have to change this and
                // the @JsonAlias in the Tag class.
                .uri(IMAGGA_TAGS_API_URL + "?image_upload_id={uploadId}&language=en&threshold={confidence}",
                        uploadId, IMAGGA_CONFIDENCE_THRESHOLD)
                .header(HttpHeaders.AUTHORIZATION, encodeBasic(IMAGGA_API_KEY, IMAGGA_API_SECRET))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    throw new IllegalArgumentException(
                            String.format("Unable to detect objects in image with uploadId=%s, statusCode=%s, body=%s",
                                    uploadId, response.getStatusCode().value(),
                                    new String(response.getBody().readAllBytes())));
                })
                .body(TagsResponse.class);
    }

    private List<String> extractObjects(TagsResponse response) {
        if ( response == null || response.getResult() == null || CollectionUtils.isEmpty(response.getResult().getTags()) ) {
            return List.of();
        }
        return response.getResult().getTags().stream()
                .filter(tagConfidence -> tagConfidence.getTag() != null && !ObjectUtils.isEmpty(tagConfidence.getTag().getName()))
                .map(tagConfidence -> tagConfidence.getTag().getName())
                .collect(Collectors.toList());
    }

    private String encodeBasic(String username, String password) {
        String usernamePassword = String.format("%s:%s", username, password);
        return String.format("Basic %s",
                Base64.getEncoder().encodeToString(usernamePassword.getBytes(StandardCharsets.UTF_8)));
    }
}
