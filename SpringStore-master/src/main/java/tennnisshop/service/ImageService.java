package tennnisshop.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tennnisshop.entity.Image;
import tennnisshop.repository.ImageRepository;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class ImageService {

    private final ImageRepository imageRepository;

    public ImageService(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    public void uploadImage(MultipartFile file, Long productId) throws IOException {
        Image image = new Image();
        image.setName(file.getOriginalFilename());
        image.setFileName(file.getOriginalFilename());
        image.setFileType(file.getContentType());
        image.setSize(file.getSize());
        image.setBytes(file.getBytes());
        image.setProductId(productId);
        imageRepository.save(image);

    }

    public Optional<Image> getMainImage(Long productId) {
        return Optional.ofNullable(imageRepository.getFirstImageByProductId(productId));
    }

    public List<Image> getAllImages(Long productId) {
        return imageRepository.getImageByProductId(productId);
    }


    public Optional<Image> getImageById(Long id) {
        return Optional.ofNullable(imageRepository.findById(id));
    }

    public void deleteImage(Long id) {
        imageRepository.deleteById(id);
    }
    public void deleteImageByProductId(Long id) {
        imageRepository.deleteByProductId(id);
    }
}
