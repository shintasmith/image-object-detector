package example.thirdparty.imagga;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * For manual testing during development only. Need a database to connect to.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class ImageDetectorManualIT {

    @Autowired
    private ImageDetector imageDetector;

    @Test
    public void tags_happyPath() throws Exception {

        List<String> names = imageDetector.detect(
                "https://i.natgeofe.com/n/548467d8-c5f1-4551-9f58-6817a8d2c45e/NationalGeographic_2572187_square.jpg");

        assertTrue(names.contains("cat"), "has cat");
        assertTrue(names.contains("kitty"), "has kitty");
    }

    @Test
    public void uploads_happyPath() throws Exception {

        TagsResponse response = imageDetector.detectWithUploadId("i22d3c9fd6644d3dd1bb80635czpqbqh");

        assertTrue(List.of("cat").contains("cat"), "has cat");
    }

}
