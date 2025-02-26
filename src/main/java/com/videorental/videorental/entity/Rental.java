package com.videorental.videorental.entity;

import jakarta.persistence.GenerationType;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
public class Rental {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime rentalDate;

    private LocalDateTime  returnDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "video_id", nullable = false)
    private Videos video;

    private Double lateFee;

    public Rental(LocalDateTime  rentalDate, LocalDateTime  returnDate, Users user, Videos video, Double lateFee) {
        this.rentalDate = rentalDate;
        this.returnDate = returnDate;
        this.user = user;
        this.video = video;
        this.lateFee = lateFee;
    }

    public Rental() {

    }

    public Long getId() {
        return id;
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

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }

    public Videos getVideo() {
        return video;
    }

    public void setVideo(Videos video) {
        this.video = video;
    }

    public Double getLateFee() {
        return lateFee;
    }

    public void setLateFee(Double lateFee) {
        this.lateFee = lateFee;
    }


    @Override
    public String toString() {
        return "Rental{" +
                "id=" + id +
                ", rentalDate=" + rentalDate +
                ", returnDate=" + returnDate +
                ", user=" + user +
                ", video=" + video +
                '}';
    }


}
