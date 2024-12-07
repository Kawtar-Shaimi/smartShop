package com.demo.smartShop.controller;

import com.demo.smartShop.dto.response.ProductDTO;
import com.demo.smartShop.entity.enums.UserRole;
import com.demo.smartShop.exception.UnauthorizedException;
import com.demo.smartShop.service.ProductService;
import com.demo.smartShop.service.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final SessionService sessionService;

    @GetMapping
    public ResponseEntity<Page<ProductDTO>> getAllProducts(Pageable pageable) {
        // Public endpoint - anyone can view products
        return ResponseEntity.ok(productService.getAllProducts(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Long id) {
        // Public endpoint - anyone can view a product
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @PostMapping
    public ResponseEntity<ProductDTO> createProduct(@Valid @RequestBody ProductDTO productDTO,
            HttpServletRequest request) {
        // Only ADMIN can create products
        requireAdmin(request);
        return ResponseEntity.ok(productService.createProduct(productDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductDTO productDTO,
            HttpServletRequest request) {
        // Only ADMIN can update products
        requireAdmin(request);
        return ResponseEntity.ok(productService.updateProduct(id, productDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ProductDTO> deleteProduct(@PathVariable Long id, HttpServletRequest request) {
        // Only ADMIN can delete products
        requireAdmin(request);
        ProductDTO deletedProduct = productService.deleteProduct(id);
        return ResponseEntity.ok(deletedProduct);
    }

    // Helper method
    private void requireAdmin(HttpServletRequest request) {
        UserRole role = sessionService.getCurrentUserRole(request.getSession())
                .orElseThrow(() -> new UnauthorizedException("User not authenticated"));
        if (role != UserRole.ADMIN) {
            throw new UnauthorizedException("Admin access required");
        }
    }
}
