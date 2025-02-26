package com.videorental.videorental.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;


public class RentalDto {
    private Long id;  // For response (rental details)
    private Long videoId;  // ID of the video being rented
    private Long userId;   // ID of the user renting the video
    private String rentalStatus;  // "RENTED", "RETURNED", etc.
    private Double lateFee;
    private LocalDateTime  rentalDate;  // ✅ Include rental date
    private LocalDateTime returnDate;

    public RentalDto() {
    }

    public RentalDto(Long id, LocalDateTime  returnDate, LocalDateTime  rentalDate, Double lateFee, String rentalStatus, Long userId, Long videoId) {
        this.id = id;
        this.returnDate = returnDate;
        this.rentalDate = rentalDate;
        this.lateFee = lateFee;
        this.rentalStatus = rentalStatus;
        this.userId = userId;
        this.videoId = videoId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getVideoId() {
        return videoId;
    }

    public void setVideoId(Long videoId) {
        this.videoId = videoId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Double getLateFee() {
        return lateFee;
    }

    public void setLateFee(Double lateFee) {
        this.lateFee = lateFee;
    }

    public String getRentalStatus() {
        return rentalStatus;
    }

    public void setRentalStatus(String rentalStatus) {
        this.rentalStatus = rentalStatus;
    }

    public LocalDateTime  getRentalDate() {
        return rentalDate;
    }

    public void setRentalDate(LocalDateTime  rentalDate) {
        this.rentalDate = rentalDate;
    }

    public LocalDateTime  getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDateTime  returnDate) {
        this.returnDate = returnDate;
    }

    @Override
    public String toString() {
        return "RentalDto{" +
                "id=" + id +
                ", videoId=" + videoId +
                ", userId=" + userId +
                ", rentalStatus='" + rentalStatus + '\'' +
                ", lateFee=" + lateFee +
                ", rentalDate=" + rentalDate +
                ", returnDate=" + returnDate +
                '}';
    }
}
