package com.demo.smartShop.service;

import com.demo.smartShop.dto.ProductDTO;
import com.demo.smartShop.entity.Product;
import com.demo.smartShop.mapper.ProductMapper;
import com.demo.smartShop.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public Page<ProductDTO> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable).map(productMapper::toDTO);
    }

    public ProductDTO getProductById(Long id) {
        return productRepository.findById(id)
                .map(productMapper::toDTO)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));
    }

    @Transactional
    public ProductDTO createProduct(ProductDTO productDTO) {
        Product product = productMapper.toEntity(productDTO);
        product.setDeleted(false); // Ensure not deleted on creation
        return productMapper.toDTO(productRepository.save(product));
    }

    @Transactional
    public ProductDTO updateProduct(Long id, ProductDTO productDTO) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));

        product.setNom(productDTO.getNom());
        product.setPrice(productDTO.getPrice());
        product.setStock(productDTO.getStock());

        return productMapper.toDTO(productRepository.save(product));
    }

    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));
        product.setDeleted(true);
        productRepository.save(product);
    }
}
