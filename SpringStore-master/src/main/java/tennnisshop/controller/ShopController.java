package tennnisshop.controller;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestParam;
import tennnisshop.entity.Image;
import tennnisshop.entity.Product;
import tennnisshop.entity.ProductDetails;
import tennnisshop.service.ImageService;
import tennnisshop.service.ProductDetailsService;
import tennnisshop.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class ShopController {

    private ProductService productService;
    private ImageService imageService;
    private ProductDetailsService productDetailsService;


    @Autowired
    public void setProductService(ProductService productService) {
        this.productService = productService;
    }

    @Autowired
    public void setProductService(ImageService imageService) {this.imageService = imageService;}

    @Autowired
    public void setProductService(ProductDetailsService productDetailsService) {
        this.productDetailsService = productDetailsService;
    }

    @GetMapping("/shop")
    public String getProducts(
            @RequestParam(value = "searchQuery", required = false, defaultValue = "") String searchQuery,
            @RequestParam(value = "category", required = false) String category,
            Pageable pageable,
            Model model) {

        Page<Product> productsPage;

        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            if (category != null && !category.trim().isEmpty()) {
                productsPage = productService.searchProductsByTitleAndCategory(searchQuery, category, pageable);
            } else {
                productsPage = productService.searchProductsByTitle(searchQuery, pageable);
            }
        } else if (category != null && !category.trim().isEmpty()) {
            productsPage = productService.getProductsByCategory(category, pageable);
        } else {
            productsPage = productService.getAllProducts(pageable);
        }

        model.addAttribute("products", productsPage);
        model.addAttribute("searchQuery", searchQuery);
        model.addAttribute("category", category);
        model.addAttribute("categories", productService.getAllCategories());

        return "shop";
    }



    @GetMapping("/details/{id}")
    public String toDetails(Model model, @PathVariable("id") Long id) {
        ProductDetails productDetails = productDetailsService.findByProductId(id);
        List<Image> images = imageService.getAllImages(id);
        model.addAttribute("productImages", images);
        model.addAttribute("productDetails", productDetails);
        return "details";
    }

}
