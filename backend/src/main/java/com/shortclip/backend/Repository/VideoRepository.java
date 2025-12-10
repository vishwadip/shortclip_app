package com.shortclip.backend.Repository;

import com.shortclip.backend.entity.Video;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface VideoRepository extends JpaRepository<Video, Long> {

    @Query("select v from Video v order by v.createdAt desc")
    Page<Video> findLatest(Pageable pageable);
}

