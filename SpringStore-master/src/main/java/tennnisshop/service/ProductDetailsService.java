package tennnisshop.service;


import org.springframework.stereotype.Service;
import tennnisshop.entity.ProductDetails;
import tennnisshop.repository.ProductDetailsRepository;

import java.util.List;

@Service
public class ProductDetailsService {

    private final ProductDetailsRepository productDetailsRepository;

    public ProductDetailsService(ProductDetailsRepository productDetailsRepository) {
        this.productDetailsRepository = productDetailsRepository;
    }

    public void save(ProductDetails productDetails) {
        productDetailsRepository.save(productDetails);
    }

    public List<ProductDetails> findAll() {
        return productDetailsRepository.findAll();
    }

    public ProductDetails findById(Long id) {
        return productDetailsRepository.findById(id);
    }
    public ProductDetails findByProductId(Long productid) {
        return productDetailsRepository.findByProductId(productid);
    }

    public void update(ProductDetails productDetails) {
        productDetailsRepository.update(productDetails);
    }

    public void deleteById(Long id) {
        productDetailsRepository.deleteById(id);
    }


}
