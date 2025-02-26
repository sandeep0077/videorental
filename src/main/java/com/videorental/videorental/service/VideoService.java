package com.videorental.videorental.service;


import com.videorental.videorental.Exception.VideoNotFoundException;
import com.videorental.videorental.dto.VideoDto;
import com.videorental.videorental.entity.Videos;
import com.videorental.videorental.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class VideoService {

    @Autowired
    private VideoRepository videoRepository;

    // utility methods

    public VideoDto mapToDto(Videos video){
        VideoDto dto = new VideoDto();
        dto.setId(video.getId());
        dto.setTitle(video.getTitle());
        dto.setDirector(video.getDirector());
        dto.setGenre(video.getGenre());
        dto.setAvailable(video.isAvailable());
        return dto;
    }

    public Videos mapToEntity(VideoDto dto){
        Videos video = new Videos();
        // it is auto-generated.
        video.setTitle(dto.getTitle());
        video.setDirector(dto.getDirector());
        video.setGenre(dto.getGenre());
        /*
         the rentals field will simply be empty (or null, if not initialized) when the video is saved
         The isAvailable flag is set explicitly in service methods
        */
        return video;
    }

    /* Retrieve all videos that are active. */
    public List<VideoDto> getAllVideos(){
       //  soft-deleted video is marked as not available

       List<Videos> videos =  videoRepository.findByIsAvailableTrue();
       return videos.stream()
               .map(this::mapToDto)
               .toList();
    }

    // Get video details by ID
    public VideoDto getVideoById(Long id) {
        Videos video = fetchVideoById(id);
        return mapToDto(video);
    }

    @Transactional
    public VideoDto saveVideo(VideoDto dto) {
        // convert to entity
        Videos video = mapToEntity(dto);

        // ensure before saving we set the isAvailable to true
        video.setAvailable(true);
        Videos savedVideo = videoRepository.save(video);
        return mapToDto(savedVideo);
    }

    @Transactional
    public VideoDto updateVideo(Long id , VideoDto videoDto){

        Videos existingVideo = fetchVideoById(id);

        // Update fields from DTO
        existingVideo.setDirector(videoDto.getDirector());
        existingVideo.setTitle(videoDto.getTitle());
        existingVideo.setGenre(videoDto.getGenre());
        Videos updatedVideo = videoRepository.save(existingVideo);
        return mapToDto(updatedVideo);
    }

    @Transactional
    public void deleteVideo(Long id) {
        Videos video = fetchVideoById(id);


        // Soft delete: mark the video as inactive so it doesn't show up in available videos
        video.setAvailable(false);
        videoRepository.save(video);
    }

    public Videos fetchVideoById(Long id) {
        return videoRepository.findById(id)
                .orElseThrow(() -> new VideoNotFoundException("Video not found with id: " + id));
    }

    @Transactional
    public List<VideoDto> saveVideos(List<VideoDto> videoDtos) {
        return videoDtos.stream().map(dto -> {
            // Convert DTO to entity
            Videos video = mapToEntity(dto);
            // Ensure the video is marked as available
            video.setAvailable(true);
            // Save the video entity
            Videos savedVideo = videoRepository.save(video);
            // Convert the saved entity back to DTO
            return mapToDto(savedVideo);
        }).collect(Collectors.toList());
    }

}
