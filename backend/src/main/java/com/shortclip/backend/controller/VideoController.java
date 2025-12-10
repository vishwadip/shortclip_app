package com.shortclip.backend.controller;

import com.shortclip.backend.Repository.UserRepository;
import com.shortclip.backend.dto.VideoDto;
import com.shortclip.backend.entity.User;
import com.shortclip.backend.entity.Video;
import com.shortclip.backend.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/videos")
@CrossOrigin(origins = "*")
public class VideoController {

    private final VideoService videoService;

    private final UserRepository userRepository;

    @Autowired
    public VideoController(VideoService videoService, UserRepository userRepository) {
        this.videoService = videoService;
        this.userRepository = userRepository;
    }

    @PostMapping("/upload")
    public ResponseEntity<VideoDto> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam("title") String title,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam("uploaderId") Long uploaderId   // 👈 comes from frontend
    ) throws IOException {

        // Find the uploader in the DB, or fail fast
        User uploader = userRepository.findById(uploaderId)
                .orElseThrow(() -> new RuntimeException("Uploader not found"));

        // Delegate to service – method should accept (User, MultipartFile, String, String)
        Video saved = videoService.uploadVideo(uploader, file, title, description);

        return ResponseEntity.ok(VideoDto.from(saved));
    }

    @GetMapping("/feed")
    public Page<VideoDto> feed(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        return videoService.getFeed(PageRequest.of(page, size))
                .map(VideoDto::from);
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<VideoDto> like(@PathVariable Long id) {
        Video updated = videoService.likeVideo(id);
        return ResponseEntity.ok(VideoDto.from(updated));
    }
}
