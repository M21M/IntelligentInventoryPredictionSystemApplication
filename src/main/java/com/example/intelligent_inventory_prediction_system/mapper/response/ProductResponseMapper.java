package com.example.intelligent_inventory_prediction_system.mapper.response;

import com.example.intelligent_inventory_prediction_system.dto.response.ProductResponseDTO;
import com.example.intelligent_inventory_prediction_system.model.Product;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductResponseMapper {
    Product toProduct(ProductResponseDTO productResponseDTO);
    ProductResponseDTO toProductResponseDTO(Product product);
    List<ProductResponseDTO> toProductResponseDTOList(List<Product> products);

}
