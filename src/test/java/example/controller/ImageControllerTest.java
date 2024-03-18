package example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import example.dto.ImageResponseDTO;
import example.exceptions.ResourceNotFoundException;
import example.model.Image;
import example.services.ImageService;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.nio.file.ReadOnlyFileSystemException;
import java.util.List;

import static org.mockito.Mockito.*;

@WebMvcTest(controllers = {ImageController.class})
public class ImageControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    ModelMapper modelMapper;

    @MockBean
    ImageService imageService;

    private static Image myCatImage = Image.builder()
            .url("http://mycat.com/cat1.png")
            .label("first-cat")
            .objectDetection(true)
            .build();

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void getAll_empty_shouldReturn200() throws Exception {
        when(imageService.getAll()).thenReturn(List.of());

        mockMvc.perform(MockMvcRequestBuilders.get("/images"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));

        verify(imageService, times(1)).getAll();
        verify(modelMapper, never()).map(any(), any());
    }

    @Test
    public void getImageById_existing_shouldReturn200() throws Exception {
        // setup
        Long imageId = 1000L;
        Image result = Image.builder()
                .id(imageId)
                .url("http://pictures.com/tree.png")
                .label("front-yard")
                .objectDetection(true)
                .build();
        when(modelMapper.map(eq(result), eq(ImageResponseDTO.ImageResponseDTOBuilder.class))).thenReturn(imageToResponseDtoBuilder(result));
        when(imageService.getImageById(imageId)).thenReturn(result);

        // send the request
        mockMvc.perform(MockMvcRequestBuilders.get("/images/" + imageId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(result.getId()));

        // verify
        verify(imageService, times(1)).getImageById(eq(imageId));
        verify(modelMapper, times(1)).map(eq(result), eq(ImageResponseDTO.ImageResponseDTOBuilder.class));
    }

    @Test
    public void getImageById_notExisting_shouldReturn404() throws Exception {
        // setup
        Long imageId = 1000L;
        when(imageService.getImageById(imageId)).thenThrow(new ResourceNotFoundException("no such id"));

        // send the request
        mockMvc.perform(MockMvcRequestBuilders.get("/images/" + imageId))
                .andExpect(MockMvcResultMatchers.status().is(404));

        // verify
        verify(imageService, times(1)).getImageById(eq(imageId));
        verify(modelMapper, never()).map(any(), eq(ImageResponseDTO.ImageResponseDTOBuilder.class));
    }

    private ImageResponseDTO.ImageResponseDTOBuilder imageToResponseDtoBuilder(Image image) throws Exception {
        String imageJson = objectMapper.writeValueAsString(image);
        return objectMapper.readValue(imageJson, ImageResponseDTO.class).toBuilder();
    }
}
