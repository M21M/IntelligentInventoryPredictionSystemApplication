package com.example.intelligent_inventory_prediction_system.mapper.request;

import com.example.intelligent_inventory_prediction_system.dto.request.ProductRequestDTO;
import com.example.intelligent_inventory_prediction_system.model.Product;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductRequestMapper {
    Product toProduct(ProductRequestDTO productRequestDTO);
    ProductRequestDTO toProductRequestDTO(Product product);


}
