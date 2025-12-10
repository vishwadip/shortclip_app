// src/app/pages/feed/feed.component.ts

import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { VideoService } from '../../services/video.service';
import { AuthService } from '../../services/auth.service';
import { Video } from '../../models/video';

@Component({
  selector: 'app-feed',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './feed.component.html',
  styleUrls: ['./feed.component.scss'],
})
export class FeedComponent implements OnInit {
  // ---- feed state ----
  videos: Video[] = [];
  page = 0;
  size = 5;
  loading = false;

  // ---- upload state ----
  selectedFile: File | null = null;
  newTitle = '';
  newDescription = '';
  uploadInProgress = false;
  uploadError: string | null = null;
  uploadSuccess: string | null = null;

  constructor(
    private videoService: VideoService,
    private auth: AuthService
  ) {}

  // used in the template: *ngIf="isCreator"
  get isCreator(): boolean {
    return this.auth.isCreator();
  }

  ngOnInit(): void {
    this.loadMore();
  }

  // load paged feed from backend
  loadMore(): void {
    if (this.loading) {
      return;
    }
    this.loading = true;

    this.videoService.getFeed(this.page, this.size).subscribe({
      next: (pageData: any) => {
        const content: Video[] = pageData.content ?? pageData;
        this.videos = this.videos.concat(content);
        this.page++;
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading feed', err);
        this.loading = false;
      },
    });
  }

  // like button click
  like(video: Video): void {
    this.videoService.like(video.id).subscribe({
      next: (updated: Video) => {
        video.likeCount = updated.likeCount;
      },
      error: (err) => {
        console.error('Error liking video', err);
      },
    });
  }

  // handle <input type="file"> change
  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.selectedFile = input.files[0];
      this.uploadError = null;
      this.uploadSuccess = null;
    }
  }

  // upload form submit
  onUpload(): void {
    if (!this.selectedFile || !this.newTitle) {
      this.uploadError = 'Please select a video and enter a title.';
      return;
    }

    const currentUser = this.auth.getCurrentUser();
    if (!currentUser) {
      this.uploadError = 'You must be logged in as a creator to upload.';
      return;
    }

    this.uploadInProgress = true;
    this.uploadError = null;
    this.uploadSuccess = null;

    this.videoService
      .upload(this.selectedFile, this.newTitle, this.newDescription, currentUser.id)
      .subscribe({
        next: (created: Video) => {
          // show new video at the top
          this.videos.unshift(created);

          // reset form
          this.selectedFile = null;
          this.newTitle = '';
          this.newDescription = '';
          this.uploadInProgress = false;
          this.uploadSuccess = 'Video uploaded successfully!';
        },
        error: (err) => {
          console.error('Upload failed', err);
          this.uploadError = 'Upload failed. Please try again.';
          this.uploadInProgress = false;
        },
      });
  }
}
