package com.demo.smartShop.controller;

import com.demo.smartShop.dto.request.CreatePromoCodeRequest;
import com.demo.smartShop.dto.response.PromoCodeDTO;
import com.demo.smartShop.entity.enums.UserRole;
import com.demo.smartShop.exception.UnauthorizedException;
import com.demo.smartShop.service.PromoCodeService;
import com.demo.smartShop.service.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/promo-codes")
@RequiredArgsConstructor
public class PromoCodeController {

    private final PromoCodeService promoCodeService;
    private final SessionService sessionService;

    @PostMapping
    public ResponseEntity<PromoCodeDTO> createPromoCode(@Valid @RequestBody CreatePromoCodeRequest request,
            HttpServletRequest httpRequest) {
        requireAdmin(httpRequest);
        return ResponseEntity.ok(promoCodeService.createPromoCode(request));
    }

    @GetMapping("/{code}")
    public ResponseEntity<PromoCodeDTO> getPromoCodeByCode(@PathVariable String code,
            HttpServletRequest httpRequest) {
        requireAdmin(httpRequest);
        return ResponseEntity.ok(promoCodeService.getPromoCodeByCode(code));
    }

    @GetMapping
    public ResponseEntity<Page<PromoCodeDTO>> getAllPromoCodes(Pageable pageable,
            HttpServletRequest httpRequest) {
        requireAdmin(httpRequest);
        return ResponseEntity.ok(promoCodeService.getAllPromoCodes(pageable));
    }

    @PostMapping("/{code}/deactivate")
    public ResponseEntity<PromoCodeDTO> deactivatePromoCode(@PathVariable String code,
            HttpServletRequest httpRequest) {
        requireAdmin(httpRequest);
        return ResponseEntity.ok(promoCodeService.deactivatePromoCode(code));
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
