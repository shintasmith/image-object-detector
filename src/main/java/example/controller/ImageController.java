package example.controller;

import example.dto.ImageResponseDTO;
import example.model.Image;
import example.dto.ImageRequestDTO;
import example.services.ImageService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Encoding;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class ImageController {

    @Autowired
    ImageService imageService;

    @Autowired
    ModelMapper modelMapper;

    @GetMapping("/images")
    public List<ImageResponseDTO> get(@RequestParam(name="objects", required = false) List<String> objects) {
        List<Image> images;
        if (CollectionUtils.isEmpty(objects)) {
            images = imageService.getAll();
        } else {
            images = imageService.getImagesWithObjects(objects);
        }
        return images.stream()
                .map(img -> modelMapper.map(img, ImageResponseDTO.ImageResponseDTOBuilder.class).build())
                .collect(Collectors.toList());
    }

    @GetMapping("/images/{imageId}")
    public ImageResponseDTO getByImageId(@PathVariable Long imageId) {
        Image image = imageService.getImageById(imageId);
        return modelMapper.map(image, ImageResponseDTO.ImageResponseDTOBuilder.class).build();
    }

    @PostMapping(value = "/images", consumes = "multipart/form-data")
    @RequestBody(content = @Content(encoding = @Encoding(name = "request", contentType = "application/json")))
    public ImageResponseDTO add(@RequestPart(value = "request") ImageRequestDTO imageDto,
                    @RequestPart(value = "file", required = false) MultipartFile mfile) throws IOException {

        Image image = modelMapper.map(imageDto, Image.ImageBuilder.class).build();
        if (ObjectUtils.isEmpty(imageDto.getUrl())) {
            try {
                if (mfile == null || mfile.isEmpty()) {
                    throw new IllegalArgumentException("Unable to get image. Either url or file must be supplied");
                }
                image.setContent(mfile.getBytes());
            } catch(IOException ioex) {
                throw new IllegalArgumentException("Unable to get the contents of image file", ioex);
            }
        }
        Image result = imageService.add(image);
        return modelMapper.map(result, ImageResponseDTO.ImageResponseDTOBuilder.class).build();
    }
}
