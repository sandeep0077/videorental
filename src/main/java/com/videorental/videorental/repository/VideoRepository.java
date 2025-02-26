package com.videorental.videorental.repository;

import com.videorental.videorental.entity.Videos;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VideoRepository extends JpaRepository<Videos, Long> {
    List<Videos> findByIsAvailableTrue();
}
