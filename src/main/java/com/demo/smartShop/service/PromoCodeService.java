package com.demo.smartShop.service;

import com.demo.smartShop.dto.request.CreatePromoCodeRequest;
import com.demo.smartShop.dto.response.PromoCodeDTO;
import com.demo.smartShop.entity.PromoCode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PromoCodeService {

    PromoCodeDTO createPromoCode(CreatePromoCodeRequest request);

    PromoCodeDTO getPromoCodeByCode(String code);

    Page<PromoCodeDTO> getAllPromoCodes(Pageable pageable);

    PromoCode validateAndGet(String code);

    void incrementUsage(String code);

    PromoCodeDTO deactivatePromoCode(String code);
}
