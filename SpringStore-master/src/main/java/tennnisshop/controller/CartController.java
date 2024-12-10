package tennnisshop.controller;

import org.springframework.web.bind.annotation.*;
import tennnisshop.util.ShoppingCart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;


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

    @GetMapping("/add/{id}")
    public String addToCart(@PathVariable("id") Long id,
                            @RequestParam(value = "topage", defaultValue = "shop",
                                    required = false) String query) {
        cart.addProductById(id);
        if (query.equals("stayonpage")) {
            return "redirect:/details/{id}";
        }
        return "redirect:/" + query;
    }
}
