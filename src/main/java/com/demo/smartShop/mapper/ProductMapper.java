package com.demo.smartShop.mapper;

import com.demo.smartShop.dto.ProductDTO;
import com.demo.smartShop.entity.Product;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    ProductDTO toDTO(Product product);
    Product toEntity(ProductDTO productDTO);
}
