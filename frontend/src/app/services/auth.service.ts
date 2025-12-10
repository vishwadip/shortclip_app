// src/app/services/auth.service.ts

import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { environment } from '../../environments/environment';
import { User } from '../models/user.model';

// Backend returns: { id, username, role }
type LoginResponse = User;

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  // e.g. environment.apiBaseUrl = 'http://localhost:8080/api'
  private readonly baseUrl = environment.apiBaseUrl;

  private currentUserSubject = new BehaviorSubject<User | null>(this.loadUser());
  currentUser$ = this.currentUserSubject.asObservable();

  constructor(private http: HttpClient) {}

  /** Read user from localStorage when app starts */
  private loadUser(): User | null {
    const raw = localStorage.getItem('currentUser');
    if (!raw) {
      return null;
    }
    try {
      return JSON.parse(raw) as User;
    } catch {
      return null;
    }
  }

  /** Call backend /auth/login and store user */
  login(username: string, password: string): Observable<LoginResponse> {
    return this.http
      .post<LoginResponse>(`${this.baseUrl}/auth/login`, { username, password })
      .pipe(
        tap((user) => {
          // keep user in memory
          this.currentUserSubject.next(user);

          // persist to localStorage for refresh
          localStorage.setItem('currentUser', JSON.stringify(user));
          // no token for now – simple demo auth
        })
      );
  }

  logout(): void {
    this.currentUserSubject.next(null);
    localStorage.removeItem('currentUser');
  }

  getCurrentUser(): User | null {
    return this.currentUserSubject.value;
  }

  /** Used by the feed to check if upload panel should be shown */
  isCreator(): boolean {
    const u = this.currentUserSubject.value;
    if (!u || !u.role) {
      return false;
    }
    // be safe with casing
    return u.role.toUpperCase() === 'CREATOR';
  }

  isLoggedIn(): boolean {
    return !!this.currentUserSubject.value;
  }
}
