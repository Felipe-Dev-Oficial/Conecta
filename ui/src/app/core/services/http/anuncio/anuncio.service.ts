import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import {
  DTOAnuncio,
  DTOAlteraAnuncio,
  DTORetornoAnuncio,
  Statement,
  PageResult,
} from '../../../models/models';
import { environment } from '../../../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class AnuncioService {
  private base = `${environment.apiUrl}/anuncios`;

  constructor(private http: HttpClient) {}

  /** GET /conecta/anuncios */
  listarAnuncios(page = 0, size = 20): Observable<PageResult<DTORetornoAnuncio>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<PageResult<DTORetornoAnuncio>>(this.base, { params });
  }

  /** GET /conecta/anuncios/management */
  listarAnunciosSecretaria(page = 0, size = 20): Observable<PageResult<Statement>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<PageResult<Statement>>(`${this.base}/management`, { params });
  }

  /** POST /conecta/anuncios/management */
  gerarAnuncio(dto: DTOAnuncio): Observable<void> {
    return this.http.post<void>(`${this.base}/management`, dto);
  }

  /** PATCH /conecta/anuncios/management/{id} */
  alterarAnuncio(id: string, dto: DTOAlteraAnuncio): Observable<void> {
    return this.http.patch<void>(`${this.base}/management/${id}`, dto);
  }

  /** PATCH /conecta/anuncios/management/{id}/prioridade — elevar */
  elevarPrioridade(id: string): Observable<void> {
    return this.http.patch<void>(`${this.base}/management/${id}/prioridade`, null);
  }

  /** DELETE /conecta/anuncios/management/{id}/prioridade — reduzir */
  reduzirPrioridade(id: string): Observable<void> {
    return this.http.delete<void>(`${this.base}/management/${id}/prioridade`);
  }

  /** DELETE /conecta/anuncios/management/{id} */
  apagarAnuncio(id: string): Observable<void> {
    return this.http.delete<void>(`${this.base}/management/${id}`);
  }
}