package com.videorental.videorental.service;

import com.videorental.videorental.Exception.DuplicateEmailException;
import com.videorental.videorental.Exception.UnauthorizedOperationException;
import com.videorental.videorental.Exception.UserNotFoundException;
import com.videorental.videorental.Role;
import com.videorental.videorental.dto.UserDto;
import com.videorental.videorental.entity.Users;
import com.videorental.videorental.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Users mapToEntity(UserDto dto) {
        Users user = new Users();
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setRole(dto.getRole() != null ? dto.getRole() : Role.valueOf("CUSTOMER"));
        return user;
    }

    private UserDto mapToDto(Users user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setRole(user.getRole());  // Role should default to "CUSTOMER" if not provided
        // You can include additional fields as needed.
        return dto;
    }

    @Transactional
    public UserDto registerUser(UserDto userDto) {

        // check if emailId already exist
        if(userRepository.existsByEmail(userDto.getEmail())){
            throw new DuplicateEmailException("Email already in use.");
        }

        Users newUser = mapToEntity(userDto);

        // encrypt password before saving
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        Users savedUser = userRepository.save(newUser);
        return mapToDto(savedUser);
    }

    public UserDto findByEmail(String email) {
        Users USERS = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
        return mapToDto(USERS);
    }

    public UserDto getUserById(Long id) {
       Users user =  userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));   // this must return a UserDto

        return mapToDto(user);
    }

    public Users getUserEntityById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
    }

    // update userinfo
    @Transactional
     public UserDto updateUser(Long userId , UserDto userDto){
         // Retrieve the currently authenticated user's ID
         Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
         String currentUserEmail = authentication.getName();

         // Fetch the user corresponding to the currently authenticated user
         UserDto currentUserDto  = findByEmail(currentUserEmail);

         // If the current user is not an ADMIN, ensure they can only update their own profile
         if(!currentUserDto.getRole().equals(Role.valueOf("ADMIN")) &&  !currentUserDto.getId().equals(userId)){
             throw new UnauthorizedOperationException("You are not authorized to update another user's profile.");
         }

         // Retrieve the user that is to be updated
         Users existingUser = userRepository.findById(userId)
                 .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

         // Update fields
         existingUser.setFirstName(userDto.getFirstName());
         existingUser.setLastName(userDto.getLastName());

         // Check and update email if it's changed
         if(!existingUser.getEmail().equals(userDto.getEmail())){
            // check if the email is not already taken
             if(userRepository.existsByEmail(userDto.getEmail())){
                 throw new DuplicateEmailException("Email already in use.");
             }

             // else update the user
             existingUser.setEmail(userDto.getEmail());
         }

         Users updatedUser = userRepository.save(existingUser);
         return mapToDto(updatedUser);
     }
}
