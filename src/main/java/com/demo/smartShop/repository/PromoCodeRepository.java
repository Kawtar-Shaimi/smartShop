package com.demo.smartShop.repository;

import com.demo.smartShop.entity.PromoCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PromoCodeRepository extends JpaRepository<PromoCode, Long> {

    Optional<PromoCode> findByCode(String code);

    Optional<PromoCode> findByCodeAndActiveTrue(String code);

    boolean existsByCode(String code);
}
