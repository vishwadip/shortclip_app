// src/app/models/video.ts

export interface Video {
  id: number;
  title: string;
  description: string | null;
  // backend may send either blobUrl or url – support both
  blobUrl?: string;
  url?: string;
  likeCount: number;
  createdAt: string; // ISO date string from backend
}
