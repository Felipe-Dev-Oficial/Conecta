import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { DTORequirement, DTOReturnRequirement, PageResult, Solicitation } from '../../../models/models';
import { environment } from '../../../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class SolicitationService {
  private base = `${environment.apiUrl}`;

  constructor(private http: HttpClient) {}

  /** GET /conecta/solicitations — requerimentos do próprio usuário */
  getSolicitations(page = 0, size = 20): Observable<PageResult<DTOReturnRequirement>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<PageResult<DTOReturnRequirement>>(`${this.base}/solicitations`, { params });
  }

  /** POST /conecta/solicitations — enviar requerimento */
  sendSolicitation(dto: DTORequirement): Observable<void> {
    return this.http.post<void>(`${this.base}/solicitations`, dto);
  }

  /** GET /conecta/management/solicitations — secretaria vê todos */
  getSolicitationsSecretaria(search: string, page = 0, size = 20): Observable<PageResult<Solicitation>> {
    const params = new HttpParams()
      .set('search', search)
      .set('page', page)
      .set('size', size);
    return this.http.get<PageResult<Solicitation>>(`${this.base}/management/solicitations`, { params });
  }

  /** PATCH /conecta/management/solicitations?id={id} — marcar como resolvido */
  solveSolicitation(id: string): Observable<void> {
    const params = new HttpParams().set('id', id);
    return this.http.patch<void>(`${this.base}/management/solicitations`, null, { params });
  }
}