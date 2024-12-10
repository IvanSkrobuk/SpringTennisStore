package tennnisshop.controller;


import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import tennnisshop.entity.Image;
import tennnisshop.repository.ImageRepository;

import java.io.ByteArrayInputStream;

@RestController
public class ImageAPIController {
    private final ImageRepository imageRepository;

    public ImageAPIController(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    @GetMapping("/images/{id}")
    private ResponseEntity<?> getImageById(@PathVariable Long id) {
        Image image = imageRepository.findById(id);

        if (image == null) {
            throw new IllegalArgumentException("Image not found");
        }

        return ResponseEntity.ok()
                .header("Content-Disposition", "inline; filename=\"" + image.getFileName() + "\"")
                .contentType(MediaType.valueOf(image.getFileType()))
                .contentLength(image.getSize())
                .body(new InputStreamResource(new ByteArrayInputStream(image.getBytes())));
    }

    @GetMapping("/image/{id}")
    public ResponseEntity<?> getImageByProductId(@PathVariable Long id) {
        Image image = imageRepository.getFirstImageByProductId(id);

        if (image == null) {
            throw new IllegalArgumentException("Image not found");
        }

        return ResponseEntity.ok()
                .header("Content-Disposition", "inline; filename=\"" + image.getFileName() + "\"")
                .contentType(MediaType.valueOf(image.getFileType()))
                .contentLength(image.getSize())
                .body(new InputStreamResource(new ByteArrayInputStream(image.getBytes())));
    }


}
