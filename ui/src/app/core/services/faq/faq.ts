import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import {
  DTORegisterFAQ,
  DTOReturnFAQ,
  DTOUpdateFaq,
  FAQ,
  PageResult,
} from '../../models/models';
import { environment } from '../../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class FaqService {
  private base = `${environment.apiUrl}/faqs`;

  constructor(private http: HttpClient) {}

  /** GET /conecta/faqs */
  listarFaqs(page = 0, size = 20): Observable<PageResult<DTOReturnFAQ>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<PageResult<DTOReturnFAQ>>(this.base, { params });
  }

  /** GET /conecta/faqs/management */
  listarFaqsSecretaria(page = 0, size = 20): Observable<PageResult<FAQ>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<PageResult<FAQ>>(`${this.base}/management`, { params });
  }

  /** POST /conecta/faqs/management */
  escreverFaq(dto: DTORegisterFAQ): Observable<void> {
    return this.http.post<void>(`${this.base}/management`, dto);
  }

  /** POST /conecta/faqs/management/{id} — publicar */
  publicarFaq(id: string): Observable<void> {
    return this.http.post<void>(`${this.base}/management/${id}`, null);
  }

  /** PATCH /conecta/faqs/management/{id} */
  alterarFaq(id: string, dto: DTOUpdateFaq): Observable<void> {
    return this.http.patch<void>(`${this.base}/management/${id}`, dto);
  }

  /** DELETE /conecta/faqs/management/{id} */
  apagarFaq(id: string): Observable<void> {
    return this.http.delete<void>(`${this.base}/management/${id}`);
  }

  /** PATCH /conecta/faqs/management/{id}/relevance — elevar */
  elevarRelevancia(id: string): Observable<void> {
    return this.http.patch<void>(`${this.base}/management/${id}/relevance`, null);
  }

  /** DELETE /conecta/faqs/management/{id}/relevance — reduzir */
  reduzirRelevancia(id: string): Observable<void> {
    return this.http.delete<void>(`${this.base}/management/${id}/relevance`);
  }
}