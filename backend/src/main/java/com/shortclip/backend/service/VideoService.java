package com.shortclip.backend.service;

import com.shortclip.backend.Repository.VideoRepository;
import com.shortclip.backend.entity.User;
import com.shortclip.backend.entity.Video;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class VideoService {

    private final VideoRepository videoRepository;
    private final AzureBlobService blobService;

    public VideoService(VideoRepository videoRepository, AzureBlobService blobService) {
        this.videoRepository = videoRepository;
        this.blobService = blobService;
    }

    /**
     * Uploads a file to Azure Blob Storage and creates a Video entity
     * linked to the given uploader.
     */
    public Video uploadVideo(User uploader,
                             MultipartFile file,
                             String title,
                             String description) throws IOException {

        // 1) Upload to Azure Blob and get the public URL
        String blobUrl = blobService.upload(file);   // 👈 call the real upload(..) method

        // 2) Build Video entity
        Video video = new Video();
        video.setUploader(uploader);                 // link to creator
        video.setTitle(title);
        video.setDescription(description);
        video.setBlobUrl(blobUrl);
        // createdAt + likeCount are initialised via defaults in the entity

        // 3) Persist and return
        return videoRepository.save(video);
    }

    public Page<Video> getFeed(Pageable pageable) {
        return videoRepository.findLatest(pageable);
    }

    public Video likeVideo(Long id) {
        Video video = videoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Video not found"));
        video.setLikeCount(video.getLikeCount() + 1);
        return videoRepository.save(video);
    }
}
