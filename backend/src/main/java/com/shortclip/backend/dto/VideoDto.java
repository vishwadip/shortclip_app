package com.shortclip.backend.dto;


import com.shortclip.backend.entity.Video;

import java.time.Instant;

public class VideoDto {

    public Long id;
    public String title;
    public String description;
    public String url;
    public long likeCount;
    public Instant createdAt;

    public static VideoDto from(Video v) {
        VideoDto dto = new VideoDto();
        dto.id = v.getId();
        dto.title = v.getTitle();
        dto.description = v.getDescription();
        dto.url = v.getBlobUrl();
        dto.likeCount = v.getLikeCount();
        dto.createdAt = v.getCreatedAt();
        return dto;
    }
}
