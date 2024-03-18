package example.services;

import example.model.Image;
import example.repositories.ImageRepository;
import example.thirdparty.imagga.ImageDetector;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
public class ImageService {

    @Autowired
    ImageRepository imageRepository;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    ImageDetector imageDetector;

    public List<Image> getAll() {
        return imageRepository.findAll();
    }

    public List<Image> getImagesWithObjects(List<String> objects) {
        return imageRepository.findContainingObject(objects);
    }

    public Image getImageById(Long id) {
        return imageRepository.getReferenceById(id);
    }

    public Image add(Image image) throws IOException {

        // generate label, if it's not specified
        if (ObjectUtils.isEmpty(image.getLabel())) {
            image.setLabel(UUID.randomUUID().toString());
        }

        // object detection for image with url
        if (image.getObjectDetection() && !ObjectUtils.isEmpty(image.getUrl()) ) {
            List<String> objectNames = imageDetector.detect(image.getUrl());
            image.setObjects(objectNames);
        // object detection for image given as a file
        } else if (image.getObjectDetection() && !ObjectUtils.isEmpty(image.getContent()) ) {
            List<String> objectNames = imageDetector.detectForUpload(image.getContent());
            image.setObjects(objectNames);
        } else {
            throw new IllegalArgumentException("Must specify a url or file for object detection");
        }

        return imageRepository.save(image);
    }
}
