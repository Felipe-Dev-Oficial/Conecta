import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class AlteradorService {
  private base = `${environment.apiUrl}/alterador`;

  constructor(private http: HttpClient) {}

  /** PATCH /conecta/alterador/email */
  alterarEmail(id: string, token: string, email: string): Observable<void> {
    const params = new HttpParams()
      .set('id', id)
      .set('token', token)
      .set('email', email);
    return this.http.patch<void>(`${this.base}/email`, null, { params });
  }

  /** PATCH /conecta/alterador/senha */
  alterarSenha(id: string, token: string, senha: string): Observable<void> {
    const params = new HttpParams()
      .set('id', id)
      .set('token', token)
      .set('senha', senha);
    return this.http.patch<void>(`${this.base}/senha`, null, { params });
  }
}