package com.videorental.videorental.controller;


import com.videorental.videorental.dto.RentalDto;
import com.videorental.videorental.service.RentalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/rentals")
@RestController
public class RentalController {

    @Autowired
    private RentalService rentalService;


    //rent video
    @PostMapping("/rent/{videoId}")
    @PreAuthorize("hasRole('CUSTOMER)")
    public ResponseEntity<RentalDto> rentVideo(@PathVariable Long videoId){
        RentalDto rentedVideo = rentalService.rentVideo(videoId);
        return ResponseEntity.status(HttpStatus.CREATED).body(rentedVideo);
    }

    //return video
    @PostMapping("/return/{rentalId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<String> returnVideo(@PathVariable Long rentalId) {
        rentalService.returnVideo(rentalId);
        return ResponseEntity.ok("Video returned successfully. Late fees (if any) applied.");
    }

    // get user rental history
    @GetMapping("/history")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<List<RentalDto>> getUserRentalHistory() {

        // here also we might not need the userId , it will be fetched froms security context
       List<RentalDto> rentals = rentalService.getUserRentalHistory();
        return ResponseEntity.ok(rentals);
    }

    // Admins can view all rentals
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<RentalDto>> getAllRentals() {
        List<RentalDto> rentals = rentalService.getAllRentals();
        return ResponseEntity.ok(rentals); // 200 OK
    }
}

/*
QUESTION : when renting the video ,
iam using authorization of user 1 but sending user id 2 in query parameter , its not chekcing that the users are different ,
which is not at all acceptable

SOLOTION:
Best Practices:
Avoid Passing UserId as a Query Parameter:
The best practice is to remove the userId parameter entirely from the request. Instead, determine the user from the authentication context. For example, if a user is logged in, retrieve their details from the SecurityContextHolder and use that user&rsquo;s id.

If You Must Accept a userId Parameter:
Compare the userId passed in the query parameter with the id from the authenticated user. If they don&rsquo;t match, throw an exception (e.g., UnauthorizedOperationException).

How to Fix Your Code:
Option 1: Remove the Query Parameter
Modify your RentalController's rent endpoint to not require a userId query parameter. Instead, inside your service method, retrieve the authenticated user's id.

Option 2: Validate the Query Parameter
If you must accept a query parameter for userId (for some reason), then add an explicit check:

Which Option to Choose?
Option 1 is preferred since it avoids potential misuse and reduces redundancy&mdash;the authenticated user's id is already available in the security context.
Option 2 is less ideal because it requires the client to supply a userId, which could be tampered with. If you must use it, make sure to validate it as shown.
*/
