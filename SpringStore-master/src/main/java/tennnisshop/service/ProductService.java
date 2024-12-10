package tennnisshop.service;


import org.springframework.web.multipart.MultipartFile;
import tennnisshop.entity.Image;
import tennnisshop.entity.Product;
import tennnisshop.entity.ProductDetails;
import tennnisshop.repository.ImageRepository;
import tennnisshop.repository.ProductDetailsRepository;
import tennnisshop.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProductService {

    private ProductRepository productRepository;
    private ImageRepository imageRepository;
    private ProductDetailsRepository productDetailsRepository;
    private ProductDetailsService productDetailsService;

    @Autowired
    public void setProductRepository(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Autowired
    public void setImageRepository(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    @Autowired
    public void setProductDetailsRepository(ProductDetailsRepository productDetailsRepository) {
        this.productDetailsRepository = productDetailsRepository;
    }

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public Page<Product> getAllProducts(Pageable pageable) {
        return productRepository.getAllProducts(pageable);
    }


    public Product getProductById(Long id) {
        if (productRepository.findById(id).isPresent()) {
            return productRepository.findById(id).get();
        }
        return null;
    }


    public void deleteProductById(Long id) {
        productRepository.deleteById(id);
    }

    public void addProduct(Product product) {
        productRepository.save(product);
    }


    public void saveProduct(Product product, ProductDetails productDetails, MultipartFile file1, MultipartFile file2, MultipartFile file3) throws IOException {
        // Создаем изображения и связываем их с продуктом
        List<Image> images = new ArrayList<>();

        if (file1 != null && file1.getSize() > 0) {
            Image image1 = toImageEntity(file1);
            images.add(image1);
        }
        if (file2 != null && file2.getSize() > 0) {
            Image image2 = toImageEntity(file2);
            images.add(image2);
        }
        if (file3 != null && file3.getSize() > 0) {
            Image image3 = toImageEntity(file3);
            images.add(image3);
        }


        Product productFromDb = productRepository.save(product);

        productDetails.setProductId(productFromDb.getId());
        productDetailsRepository.save(productDetails);

        // Сохраняем изображения и связываем их с продуктом
        for (Image image : images) {
            image.setProductId(productFromDb.getId());
            imageRepository.save(image);
        }


    }

    public Page<Product> getProductsByCategory(String category, Pageable pageable) {
        return productRepository.findByCategory(category, pageable);
    }

    public Page<Product> searchProductsByTitleAndCategory(String title, String category, Pageable pageable) {
        return productRepository.findByTitleContainingAndCategory(title, category, pageable);
    }

    public List<String> getAllCategories() {
        return productRepository.findAllDistinctCategories();
    }

    public Page<Product> searchProductsByTitle(String title, Pageable pageable) {
        return productRepository.findByTitle(title, pageable);
    }


    public void updateTitle(Long id, String title) {
        Product product = productRepository.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));
        product.setTitle(title);
        productRepository.update(product);

        // Обновляем связанный ProductDetails
        ProductDetails details = productDetailsService.findByProductId(id);
        details.setProductName(title);
        productDetailsService.update(details);
    }

    public void update(Product product) {
        productRepository.update(product);
    }

    public void updatePrice(Long id, int price) {
        Product product = productRepository.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));
        product.setPrice(price);
        productRepository.update(product);

        // Обновляем связанный ProductDetails
        ProductDetails details = productDetailsService.findByProductId(id);
        details.setPrice(price);
        productDetailsService.update(details);
    }

    public void updateQuantity(Long id, int quantity) {
        Product product = productRepository.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));
        product.setQuantity(quantity);
        productRepository.update(product);

    }

    private Image toImageEntity(MultipartFile file) throws IOException {
        Image image = new Image();
        image.setName(file.getOriginalFilename());
        image.setFileName(file.getOriginalFilename());
        image.setFileType(file.getContentType());
        image.setSize(file.getSize());
        image.setBytes(file.getBytes());
        return image;
    }


    @Autowired
    public void setProductDetailsService(ProductDetailsService productDetailsService) {
        this.productDetailsService = productDetailsService;
    }

    public List<Product> findProductsByOrderId(Long orderId) {
        return productRepository.findProductsByOrderId(orderId);
    }
}