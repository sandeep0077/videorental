package com.videorental.videorental.service;


import com.videorental.videorental.Exception.RentalAlreadyReturnedException;
import com.videorental.videorental.Exception.RentalNotFoundException;
import com.videorental.videorental.Exception.UserPendingLateFeesException;
import com.videorental.videorental.Exception.VideoUnavailableException;
import com.videorental.videorental.dto.RentalDto;
import com.videorental.videorental.dto.UserDto;
import com.videorental.videorental.dto.VideoDto;
import com.videorental.videorental.entity.Rental;
import com.videorental.videorental.entity.Users;
import com.videorental.videorental.entity.Videos;
import com.videorental.videorental.repository.RentalRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RentalService {

    @Autowired
    private RentalRepository rentalRepository;

    @Autowired
    UserService userService;

    @Autowired
    VideoService videoService;


    // helper functions
    private RentalDto mapToDto(Rental rental){
        RentalDto dto = new RentalDto();

        dto.setId(rental.getId());
        dto.setVideoId(rental.getVideo().getId());
        dto.setUserId(rental.getUser().getId());
        dto.setRentalDate(rental.getRentalDate());
        dto.setReturnDate(rental.getReturnDate());

        // get the status
        dto.setRentalStatus(rental.getReturnDate() == null ? "Rented" : "Returned");
        dto.setLateFee(rental.getLateFee() != null ? rental.getLateFee() : 0.0);
        return dto;
    }

    private Rental mapToEntity(RentalDto dto, Users user, Videos video) {
        Rental rental = new Rental();
        rental.setUser(user);
        rental.setVideo(video);
        rental.setRentalDate(dto.getRentalDate());
        rental.setReturnDate(dto.getReturnDate());
        rental.setLateFee(dto.getLateFee());
        return rental;
    }

    // rent a video
    @Transactional
    public RentalDto rentVideo(Long videoId ){

        // Retrieve authenticated user by email
        String authEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        UserDto userDto = userService.findByEmail(authEmail);

        Long userId = userDto.getId();


        // check if user exist
        Users user = userService.getUserEntityById(userId);

        // find if user has late fees
        if(hasPendingFees(userId)){
            throw new UserPendingLateFeesException("User has pending late fees. Cannot rent more videos.");
        }

        // check if video exist
        Videos video = videoService.fetchVideoById(videoId);

        if (!video.isAvailable()) {
            throw new VideoUnavailableException("Video is already rented.");
        }

        // create rental records
        Rental rental = new Rental();
        rental.setRentalDate(LocalDateTime.now());
        rental.setUser(user);
        rental.setVideo(video);

        // Mark the video as rented by setting its availability to false.
        // Because 'video' is a managed (persistent) entity, JPA automatically tracks this change.
        // When the transaction commits, JPA flushes(saves) all changes to the database, so an explicit call
        // to update (e.g., videoService.updateVideo(videoId, videoService.mapToDto(video))) is redundant.
        // If additional business logic (such as extra validations, logging, or side effects) is required
        // during a video update, then invoking updateVideo() might be justified. For now, the simple state
        // change is enough.
        video.setAvailable(false);
        rentalRepository.save(rental);
      //  videoService.updateVideo(videoId , videoService.mapToDto(video));

        return mapToDto(rental);
    }

    @Transactional
    public void returnVideo(Long rentalId){
        // check if rental records are available
        //check if already returned
        // check for late fees ( rented date - todaydate)
        // mark the video available again

      Rental rental =  rentalRepository.findById(rentalId).orElseThrow(() -> new RentalNotFoundException("Rental record not found"));

        if (rental.getReturnDate() != null) {
            throw new RentalAlreadyReturnedException("Video has already been returned.");
        }
        
        // setReturned date
        rental.setReturnDate(LocalDateTime.now());

        // Late fee calculation (Assuming 7 days free rental, then $2 per day)
        long daysRented = ChronoUnit.DAYS.between(rental.getRentalDate().toLocalDate() , LocalDate.now());
        if(daysRented > 7){
            rental.setLateFee((daysRented - 7) * 2.0);
        }else{
            rental.setLateFee(0.0);
        }

        // video available
        Videos video = rental.getVideo();
        video.setAvailable(true);


        videoService.updateVideo( video.getId() ,videoService.mapToDto(video));

        rentalRepository.save(rental);
    }

    public List<RentalDto> getUserRentalHistory(){

        // Retrieve authenticated user by email
        String authEmail = SecurityContextHolder.getContext().getAuthentication().getName();


        /**
         UserDto userDto = userService.findByEmail(authEmail);
         List<Rental> rentalList = rentalRepository.findByUserId(userDto.getId());

         so below instead of making two db calls for finding userdto, we just added another method in rental service to find rental by userEmail
         Combine user lookup and rental fetching in one method
         */


        List<Rental> rentalList = rentalRepository.findByUserEmail(authEmail);

        //convert this to DTO
       return rentalList.stream()
               .map(this::mapToDto)
               .toList();
    }

    public List<RentalDto> getAllRentals() {
        List<Rental> rentals = rentalRepository.findAll();
        return rentals.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    public boolean hasPendingFees(Long userId) {
        return rentalRepository.existsByUserIdAndLateFeeGreaterThan(userId, 0.0);
    }
}
