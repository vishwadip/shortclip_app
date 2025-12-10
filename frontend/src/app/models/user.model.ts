// src/app/models/user.ts
export interface User {
  id: number;
  username: string;
  role: 'CREATOR' | 'VIEWER';
}
