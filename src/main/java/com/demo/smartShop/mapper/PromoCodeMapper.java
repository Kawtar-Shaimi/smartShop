package com.demo.smartShop.mapper;

import com.demo.smartShop.dto.request.CreatePromoCodeRequest;
import com.demo.smartShop.dto.response.PromoCodeDTO;
import com.demo.smartShop.entity.PromoCode;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PromoCodeMapper {

    @org.mapstruct.Mapping(target = "id", ignore = true)
    @org.mapstruct.Mapping(target = "active", ignore = true)
    @org.mapstruct.Mapping(target = "currentUsage", ignore = true)
    PromoCode toEntity(CreatePromoCodeRequest request);

    PromoCodeDTO toResponse(PromoCode promoCode);
}
