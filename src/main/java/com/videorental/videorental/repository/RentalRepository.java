package com.videorental.videorental.repository;


import com.videorental.videorental.entity.Rental;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RentalRepository extends JpaRepository<Rental, Long> {
    boolean existsByUserIdAndLateFeeGreaterThan(Long userId, double v);


    List<Rental> findByUserId(Long userId);
    // In RentalRepository
    List<Rental> findByUserEmail(String email);
}
