package example.repositories;

import example.model.Image;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * For manual testing during development only. Need a database to connect to.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class ImageRepositoryManualIT {

    @Autowired
    ImageRepository repository;

    @Test
    public void saveIt() {

        Image image1 = Image.builder()
                .label("label1")
                .url("https://myhost.co/imagedb")
                .objectDetection(true)
                .objects(List.of("cat", "dog"))
                .build();

        Image saved1 = repository.save(image1);

        assertNotNull(saved1, "not null");
        assertNotNull(saved1.getId(), "id not null");
    }
}
