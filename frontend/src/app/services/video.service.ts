// src/app/services/video.service.ts

import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Video } from '../models/video';

@Injectable({
  providedIn: 'root',
})
export class VideoService {
  // if environment.apiBaseUrl = 'http://localhost:8080/api'
  // backend controller is @RequestMapping("/api/videos")
  private readonly baseUrl = `${environment.apiBaseUrl}/videos`;

  constructor(private http: HttpClient) {}

  /** Feed: GET /api/videos/feed?page=&size= */
  getFeed(page = 0, size = 5): Observable<any> {
    return this.http.get<any>(`${this.baseUrl}/feed`, {
      params: { page, size },
    });
  }

  /** Like: POST /api/videos/{id}/like */
  like(id: number): Observable<Video> {
    return this.http.post<Video>(`${this.baseUrl}/${id}/like`, {});
  }

  /**
   * Upload: POST /api/videos/upload  (multipart/form-data)
   * Now includes uploaderId (logged-in user id).
   */
  upload(
    file: File,
    title: string,
    description: string | undefined,
    uploaderId: number
  ): Observable<Video> {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('title', title);
    if (description) {
      formData.append('description', description);
    }
    formData.append('uploaderId', String(uploaderId));

    return this.http.post<Video>(`${this.baseUrl}/upload`, formData);
  }
}
