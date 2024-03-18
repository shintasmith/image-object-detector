package example.services;

import example.model.Image;
import example.repositories.ImageRepository;
import example.thirdparty.imagga.ImageDetector;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOError;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ImageServiceTest {

    @Mock
    ImageDetector imageDetector;

    @Mock
    ImageRepository imageRepository;

    @InjectMocks
    ImageService imageService;

    @Test
    public void addImageWithUrl_happyPath() throws Exception {
        // setup
        String url = "https://someurl.somewhere.com/foo.jpg";
        Image testImage = Image.builder()
                .url(url)
                .label("my-image")
                .objectDetection(true)
                .build();

        List<String> objectNames = List.of("house","tree");
        when(imageDetector.detect(eq(url))).thenReturn(objectNames);

        Image expectedImage = testImage.toBuilder()
                .objects(objectNames)
                .build();
        when(imageRepository.save(any(Image.class))).thenReturn(expectedImage);

        // call the service
        Image result = imageService.add(testImage);

        // verify
        assertEquals(expectedImage, result, "Image objects are the same");
        verify(imageRepository).save(any(Image.class));
    }

    @Test
    public void addImageWithUploadedFile_happyPath() throws Exception {
        // setup
        String url = "https://someurl.somewhere.com/foo.jpg";
        byte[] byteInput = "test".getBytes();
        Image testImage = Image.builder()
                .label("my-image")
                .content(byteInput)
                .objectDetection(true)
                .build();

        List<String> objectNames = List.of("object1","object2");
        when(imageDetector.detectForUpload(eq(byteInput))).thenReturn(objectNames);

        Image expectedImage = testImage.toBuilder()
                .objects(objectNames)
                .build();
        when(imageRepository.save(any(Image.class))).thenReturn(expectedImage);

        // call the service
        Image result = imageService.add(testImage);

        // verify
        assertEquals(expectedImage, result, "Image objects are the same");
        verify(imageRepository).save(any(Image.class));
    }

    @Test
    public void addImageWithNoUrlOrFile_shouldThrowException() {
        Image testImage = Image.builder()
                .label("my-image")
                .objectDetection(true)
                .build();

        // call the service
        Throwable thr = assertThrows(IllegalArgumentException.class, () -> imageService.add(testImage));
    }

    @Test
    public void addImageWithNoLabel_shouldGenerateOne() throws Exception {
        // setup
        String url = "https://someurl.somewhere.com/foo.jpg";
        Image testImage = Image.builder()
                .url(url)
                .objectDetection(true)
                .build();

        List<String> objectNames = List.of("cup","bowl");
        when(imageDetector.detect(eq(url))).thenReturn(objectNames);

        Image expectedImage = testImage.toBuilder()
                .objects(objectNames)
                .build();
        when(imageRepository.save(any(Image.class))).thenReturn(expectedImage);

        // call the service
        Image result = imageService.add(testImage);

        // verify
        assertEquals(expectedImage, result, "Image objects are the same");
        ArgumentCaptor<Image> captor = ArgumentCaptor.forClass(Image.class);
        verify(imageRepository).save(captor.capture());
        Image toBeSaved = captor.getValue();
        assertNotNull(toBeSaved.getLabel(), "label is not null");
    }

    @Test
    public void getAllImages_happyPath() throws Exception {
        // setup
        when(imageRepository.findAll()).thenReturn(List.of());

        // call the service
        List<Image> images = imageService.getAll();

        verify(imageRepository).findAll();
    }

    @Test
    public void getAllImageWithObjects_happyPath() throws Exception {
        // setup
        Image expected = Image.builder()
                .id(1000L)
                .url("https://someimage.somewhere.com/images/foo.jpg")
                .objectDetection(true)
                .objects(List.of("cat", "bird"))
                .build();
        when(imageRepository.findContainingObject(anyList())).thenReturn(List.of(expected));

        // call the service
        List<Image> images = imageService.getImagesWithObjects(List.of("cat", "dog"));

        // verify
        verify(imageRepository).findContainingObject(eq(List.of("cat", "dog")));
        assertEquals(1, images.size(), "number of elements result list");
        Image actual = images.get(0);
        assertEquals(expected, actual, "result is the correct image");
    }

    @Test
    public void getImageById_happyPath() throws Exception {
        // setup
        Image expected = Image.builder()
                .id(1L)
                .url("https://someimage.somewhere.com/images/foo.jpg")
                .objectDetection(true)
                .objects(List.of("cat", "bird"))
                .build();
        when(imageRepository.getReferenceById(anyLong())).thenReturn(expected);

        // call the service
        Image result = imageService.getImageById(1L);

        // verify
        verify(imageRepository).getReferenceById(eq(1L));
        assertEquals(expected, result, "result is the correct image");
    }
}
