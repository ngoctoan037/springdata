package com.example.product.controller;

import com.example.product.entity.Product;
import com.example.product.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/products")
public class ProductController {
    
    @Autowired
    private ProductService productService;
    
    @GetMapping
    public String listProducts(@RequestParam(required = false) String keyword, Model model) {
        List<Product> products;
        if (keyword != null && !keyword.trim().isEmpty()) {
            products = productService.searchProducts(keyword);
            model.addAttribute("keyword", keyword);
        } else {
            products = productService.getAllProducts();
        }
        model.addAttribute("products", products);
        return "products/list";
    }
    
    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("product", new Product());
        return "products/add";
    }
    
    @PostMapping("/add")
    public String addProduct(@Valid @ModelAttribute("product") Product product, 
                           BindingResult result, 
                           RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "products/add";
        }
        productService.saveProduct(product);
        redirectAttributes.addFlashAttribute("successMessage", "product.add.success");
        return "redirect:/products";
    }
    
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        return productService.getProductById(id)
            .map(product -> {
                model.addAttribute("product", product);
                return "products/edit";
            })
            .orElseGet(() -> {
                redirectAttributes.addFlashAttribute("errorMessage", "product.not.found");
                return "redirect:/products";
            });
    }
    
    @PostMapping("/edit/{id}")
    public String updateProduct(@PathVariable Long id, 
                              @Valid @ModelAttribute("product") Product product,
                              BindingResult result, 
                              RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "products/edit";
        }
        product.setId(id);
        productService.saveProduct(product);
        redirectAttributes.addFlashAttribute("successMessage", "product.update.success");
        return "redirect:/products";
    }
    
    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            productService.deleteProduct(id);
            redirectAttributes.addFlashAttribute("successMessage", "product.delete.success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "product.delete.error");
        }
        return "redirect:/products";
    }
}