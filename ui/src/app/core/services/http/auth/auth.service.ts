import { Injectable, inject } from '@angular/core';
import { Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { DTOLogin } from '../../../models/models';
import { environment } from '../../../../../environments/environment';
import { UserData } from '../../../models/models';
import { NotificationService } from '../notification/notification.service';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private base = `${environment.apiUrl}/auth`;
  private router = inject(Router);
  private notificationService = inject(NotificationService);

  constructor(private http: HttpClient) {}

  /** POST /conecta/auth/login → retorna JWT */
  login(dto: DTOLogin): Observable<string> {
    return this.http.post(`${this.base}/login`, dto, { responseType: 'text' });
  }

  async logout() {
    await this.notificationService.unsubscribe();
    localStorage.removeItem('token');
    this.router.navigate(['/auth/login']);
  }

  private getDecodedToken(): any {
    const token = localStorage.getItem('token');
    if (!token) return null;

    try {
      const payload = token.split('.')[1];
      return JSON.parse(atob(payload));
    } catch (e) {
      return null;
    }
  }

  getUserData(): UserData | null {
    const decoded = this.getDecodedToken();
    if (!decoded) return null;

    return {
      id: decoded.sub,          
      nome: decoded.user_nome,    
      role: decoded.user_role   
    };
  }
}