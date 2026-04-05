import { Injectable, inject } from '@angular/core';
import { Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { DTOLogin } from '../../models/models';
import { environment } from '../../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private base = `${environment.apiUrl}/auth`;
  private router = inject(Router);

  constructor(private http: HttpClient) {}

  /** POST /conecta/auth/login → retorna JWT */
  login(dto: DTOLogin): Observable<string> {
    return this.http.post(`${this.base}/login`, dto, { responseType: 'text' });
  }

  logout() {
    localStorage.removeItem('token');
    this.router.navigate(['/auth/login']);
  }
}