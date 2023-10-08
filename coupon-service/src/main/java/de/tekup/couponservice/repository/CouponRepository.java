package de.tekup.couponservice.repository;

import de.tekup.couponservice.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CouponRepository extends JpaRepository<Coupon,Long> {
    Optional<Coupon> findByCode(String code);
    boolean existsByCode(String code);
}