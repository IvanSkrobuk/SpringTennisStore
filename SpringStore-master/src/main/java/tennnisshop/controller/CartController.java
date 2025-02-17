package tennnisshop.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tennnisshop.entity.OrderItem;
import tennnisshop.entity.Product;
import tennnisshop.util.ShoppingCart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Controller
@RequestMapping("/cart")
public class CartController {

    private ShoppingCart cart;


    @Autowired
    public void setCart(ShoppingCart cart) {
        this.cart = cart;
    }

    @GetMapping("")
    public String showCart(Model model) {
        model.addAttribute("orderItems", cart.getOrderItems());
        model.addAttribute("totalPrice", cart.getTotalPrice());

        return "cart";
    }


    @PostMapping("/remove/{id}")
    public String removeProductFromCart(@PathVariable Long id) {
        cart.removeProductById(id);
        return "redirect:/cart";
    }
    @PostMapping("/remove_all")
    public String removeProductFromCart() {
        cart.clearOrderItems();
        return "redirect:/cart";
    }

    @GetMapping("/add/{id}")
    @ResponseBody
    public ResponseEntity<String> addToCart(@PathVariable("id") Long id,
                                            @RequestParam(value = "quantity", defaultValue = "1", required = false) int quantity) {
        for (int i = 0; i < quantity; i++) {
            cart.addProductById(id);
        }
        return ResponseEntity.ok("Товар добавлен в корзину");
    }
}
