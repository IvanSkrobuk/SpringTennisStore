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
import java.util.List;
import java.util.stream.Collectors;

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
//    public ResponseEntity<MultiValueMap<String, Object>> getImagesByProductId(@PathVariable Long id) {
//        // Получаем все изображения для продукта
//        List<Image> images = imageRepository.getImageByProductId(id);
//
//        if (images.isEmpty()) {
//            throw new IllegalArgumentException("Images not found");
//        }
//
//        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
//
//        // Добавляем каждое изображение в список
//        for (Image image : images) {
//            InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream(image.getBytes()));
//
//            // Создаем заголовки для изображения
//            body.add("images", resource);
//
//            // Вставляем метаданные (например, название файла) в заголовки
//            // (вместо header можно использовать атрибуты изображения в теле ответа)
//            body.add("Content-Disposition", "inline; filename=\"" + image.getFileName() + "\"");
//            body.add("Content-Type", image.getFileType());
//        }
//
//        // Возвращаем изображения с соответствующим типом контента
//        return ResponseEntity.ok()
//                .contentType(MediaType.MULTIPART_FORM_DATA)
//                .body(body);
//    }


    @GetMapping("/image/{id}")
    public ResponseEntity<?> getImageByProductId(@PathVariable Long id) {
        // Получаем первое изображение для продукта
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
