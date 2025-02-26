package com.videorental.videorental.controller;


import com.videorental.videorental.dto.VideoDto;
import com.videorental.videorental.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/videos")
public class VideoController {

    @Autowired
    private VideoService videoService;

    // addVideos , deleteVideos, updateVideo , getVideo

    //anyone can access the video


    @GetMapping()
    public ResponseEntity<List<VideoDto>> getAvailableVideos(){
        List<VideoDto> availableVideos = videoService.getAllVideos();
        return ResponseEntity.status(HttpStatus.OK).body(availableVideos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VideoDto> getVideoById(@PathVariable Long id) {
        VideoDto video = videoService.getVideoById(id);
        return ResponseEntity.ok(video); // 200 OK
    }


    // Only ADMIN can update a video
    @PostMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<VideoDto> addVideo(@RequestBody VideoDto videoDto){
        VideoDto savedVideo =videoService.saveVideo(videoDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedVideo);
    }

    // Only ADMIN can update a video
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<VideoDto> updateVideo(@PathVariable Long id ,@RequestBody VideoDto videoDto){
        VideoDto updatedVideo =videoService.updateVideo(id , videoDto);
        return ResponseEntity.ok(updatedVideo);
    }

    // Only ADMIN can delete a video
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteVideo(@PathVariable Long id){
        videoService.deleteVideo(id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }

    @PostMapping("/bulk")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<VideoDto>> addMultipleVideos(@RequestBody List<VideoDto> videoDtos) {
        List<VideoDto> savedVideos = videoService.saveVideos(videoDtos);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedVideos);
    }

}
