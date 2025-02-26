package com.videorental.videorental.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
public class Videos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String director;

    @Column(nullable = false)
    private String genre;

    @Column(nullable = false)
    private boolean isAvailable = true;


/**
 * new HashSet<>(); This ensures that even
 * if you don't explicitly set rentals in your mapToEntity method, the field will be an empty collection rather than null,
 * preventing potential NullPointerExceptions.
 */

@OneToMany(mappedBy = "video", fetch = FetchType.LAZY)
    private Set<Rental> rentals  = new HashSet<>();

/*
    Cascade PERSIST and MERGE are useful if you want changes in the parent (Video) to automatically propagate to newly added or updated child entities (Rental)
    when they are created or modified at the same time as the video.
    For example, if you build a use case where a video is created along with its initial rental records, using these cascades can simplify your code.

    However, if rental records are managed independently (for instance, created later as users rent the video),
    then you might not need cascading for persist or merge. In many production systems, rentals have an independent life cycle,
    so you might only use cascading in certain scenarios or not at all.
*/

    public Videos(String title, String director, String genre, boolean isAvailable, Set<Rental> rentals) {
        this.title = title;
        this.director = director;
        this.genre = genre;
        this.isAvailable = isAvailable;
        this.rentals = rentals;
    }

    public Videos() {
    }

    public Long getId() {
        return id;
    }

    public Set<Rental> getRentals() {
        return rentals;
    }

    public void setRentals(Set<Rental> rentals) {
        this.rentals = rentals;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "Videos{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", director='" + director + '\'' +
                ", genre='" + genre + '\'' +
                ", isAvailable=" + isAvailable +
                ", rentals=" + rentals +
                '}';
    }
}
